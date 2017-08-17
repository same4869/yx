package com.ml.yx.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.LruCache;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.CacheStoreUtil;
import com.ml.yx.comm.MyThreadPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @Author:Lijj
 * @Todo:TODO
 */

public class BitmapCache implements ImageCache {
    private final static int MAX_CACHE_SIZE = 12 * 1024 * 1024;
    private final static int MIN_CACHE_SIZE = 4 * 1024 * 1024;

    private LruCache<String, Bitmap> mCache;
    private String rootPath;

    public BitmapCache(Context context) {
        int maxSize = MIN_CACHE_SIZE;

        int heapSize = (int) Runtime.getRuntime().maxMemory() / 12;

        if (heapSize > MIN_CACHE_SIZE && heapSize < MAX_CACHE_SIZE) {
            maxSize = heapSize;
        } else if (heapSize > MAX_CACHE_SIZE) {
            heapSize = MAX_CACHE_SIZE;
        }

        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };

        rootPath = CacheStoreUtil.getCacheDir(context).getAbsolutePath();
    }

    @Override
    public Bitmap getBitmap(String key) {
        if (key == null || mCache == null) {
            return null;
        }

        Bitmap bm = mCache.get(key);
        if (bm != null && bm.isRecycled()) {
            mCache.remove(key);
            bm = null;
        }
        if (bm != null) {
            return bm;
        }

        try {
            String path = rootPath + "/" + key;

            int div = 1;

            while (div <= 8) {
                bm = decodeByResample(path, div);
                if (bm != null) {
                    break;
                }

                System.gc();

                div *= 2;
            }

            mCache.put(key, bm);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bm;
    }

    private Bitmap decodeByResample(String path, int resample) {

        try {
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

            bmpFactoryOptions.inSampleSize = resample;

            bmpFactoryOptions.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(path), null, bmpFactoryOptions);

            return bm;
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * called after network fetch
     */
    @Override
    public void putBitmap(final String key, final Bitmap bitmap) {
        if (mCache == null || key == null) {
            return;
        }

        if (mCache.get(key) != null) {
            return;
        }

        mCache.put(key, bitmap);

        final File file = new File(rootPath + "/" + key);
        if (file.exists()) {
            return;
        }

        // write to fs, serially...
        MyThreadPool.serialExecute(new Runnable() {

            @Override
            public void run() {
                if (file.exists()) {
                    return;
                }

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    APPUtil.closeObject(fos);
                }
            }
        });
    }

    public boolean removeBitmap(String key) {
        if (mCache == null) {
            return true;
        }
        mCache.remove(key);
        String path = rootPath + "/" + key;
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return true;
    }

    @Override
    public String getCacheRootDir() {
        return rootPath;
    }

    @Override
    public void clearAll() {
        if (mCache == null) {
            return;
        }

        mCache.evictAll();
    }

    @Override
    public Bitmap getBitmapImMemory(String key, int width, int height) {
        if (key == null) {
            return null;
        }
        Bitmap bitmap = mCache.get(key);
        if (bitmap != null && bitmap.isRecycled()) {
            mCache.remove(key);
            bitmap = null;
        }
        return bitmap;
    }

    @Override
    public boolean isBitmapFileExist(String key) {
        String path = rootPath + "/" + key;

        File file = new File(path);

        return file.exists();
    }
}
