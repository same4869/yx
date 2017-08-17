package com.ml.yx.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.display.BitmapDisplayer;
import com.android.volley.display.FadeInBitmapDisplayer;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageLoader.ImageRequestTag;
import com.android.volley.toolbox.Volley;
import com.ml.yx.R;
import com.ml.yx.comm.BitmapUtil;
import com.ml.yx.comm.ImageSizeUtil;
import com.ml.yx.comm.MultiThreadAsyncTask;
import com.ml.yx.comm.StringUtil;

import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @Author:Lijj
 * @Date:2014-4-17上午10:34:02
 * @Todo:TODO
 */
public class WebDataLoader {
    private static final String REQUEST_TAG = "wenba_request";

    public static final Config DEFAULT_BITMAP_CONFIG = Config.ARGB_8888;
    public static final Config LOW_BITMAP_CONFIG = Config.RGB_565;

    private static WebDataLoader mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private ImageCache imageCache;

    private HttpStack httpStack = null;


    private static DefaultHttpClient getDefaultHttpClient(Context context) {
        return Volley.getThreadSafeClient(null);
    }

    private WebDataLoader(Context context) {
        httpStack = new HttpClientStack(getDefaultHttpClient(context));
        mRequestQueue = Volley.newRequestQueue(context, httpStack);
        imageCache = new BitmapCache(context);
        mImageLoader = new ImageLoader(mRequestQueue, imageCache);

//		loadCookie();
    }

    public static WebDataLoader getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WebDataLoader(context.getApplicationContext());
        }

        return mInstance;
    }

    public String getImageCachePath() {
        return imageCache.getCacheRootDir();
    }

//	public void saveCookie() {
//		if (httpStack != null) {
//			WenbaCookies cookies = httpStack.getCookies();
//			UserManager.saveCookie(cookies);
//		}
//	}
//
//	public void clearCookie() {
//		UserManager.clearCookie();
//		if (httpStack != null) {
//			try {
//				httpStack.setCookies(null);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private void loadCookie() {
//		WenbaCookies wenbaCookies = UserManager.getCookie();
//
//		if (httpStack != null && wenbaCookies != null) {
//			try {
//				httpStack.setCookies(wenbaCookies);
//			} catch (Exception e) {
//				e.printStackTrace();
//				UserManager.saveCookie(null);
//			}
//		}
//	}

    public RequestQueue getmRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }

    /**
     * @param url
     * @param listener
     * @return
     */
    private void loadImageWithTag(ImageRequestTag tag, String url, final ImageListener<Bitmap> listener,
                                  Config decodeConfig) {
        if (StringUtil.isBlank(url)) {
            return;
        }

        if (StringUtil.isLocalUrl(url)) {
            new MultiThreadAsyncTask<String, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(String... params) {
                    return BitmapUtil.getBitmapFromPath(params[0]);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        listener.onResponse(bitmap);
                    } else {
                        listener.onErrorResponse(new VolleyError("img path error"));
                    }
                }

            }.execute(url);
        } else {
            mImageLoader.get(tag, url, listener, decodeConfig);
        }
    }

    private void loadImageWithTag(ImageRequestTag tag, String url, int maxWidth, int maxHeight,
                                  ImageListener<Bitmap> listener, Config decodeConfig) {
        if (StringUtil.isBlank(url)) {
            return;
        }

        if (StringUtil.isLocalUrl(url)) {
            Bitmap bitmap = BitmapUtil.getImageFromPath(url, maxWidth, maxHeight);
            if (bitmap != null) {
                listener.onResponse(bitmap);
            } else {
                listener.onErrorResponse(new VolleyError("img path error"));
            }
        } else {
            url = ImageSizeUtil.formatURL(url, maxWidth, maxHeight);
            mImageLoader.get(tag, url, listener, decodeConfig);
        }
    }

    /**
     * 加载图片，默认Config.ARGB_8888
     *
     * @param url
     * @param listener
     */
    public void loadImage(String url, ImageListener<Bitmap> listener) {
        loadImage(url, listener, DEFAULT_BITMAP_CONFIG);
    }

    /**
     * 加载图片
     *
     * @param url
     * @param listener
     * @param decodeConfig
     */
    public void loadImage(String url, ImageListener<Bitmap> listener, Config decodeConfig) {
        loadImageWithTag(null, url, listener, decodeConfig);
    }

    /**
     * 指定imageView加载图片，默认Config.ARGB_8888
     *
     * @param view
     * @param imageUrl
     */
    public void loadImageView(final ImageView view, final String imageUrl) {
        loadImageView(view, imageUrl, new FadeInBitmapDisplayer(1000));
    }

    /**
     * 指定imageView加载图片，默认Config.ARGB_8888
     *
     * @param view
     * @param imageUrl
     * @param displayer 图片加载完成动画
     */
    public void loadImageView(final ImageView view, final String imageUrl, BitmapDisplayer displayer) {
        loadImageView(view, imageUrl, 0, 0, DEFAULT_BITMAP_CONFIG, displayer);
    }

    /**
     * 指定imageView加载图片
     *
     * @param view
     * @param imageUrl
     * @param decodeConfig
     */
    public void loadImageView(final ImageView view, final String imageUrl, Config decodeConfig) {
        loadImageView(view, imageUrl, 0, 0, decodeConfig, new FadeInBitmapDisplayer(1000));
    }

    /**
     * 指定imageView加载图片
     *
     * @param view
     * @param imageUrl
     * @param maxWidth
     * @param maxHeight
     * @param decodeConfig
     */
    public void loadImageView(final ImageView view, final String imageUrl, int maxWidth, int maxHeight,
                              Config decodeConfig) {
        loadImageView(view, imageUrl, maxWidth, maxHeight, decodeConfig, new FadeInBitmapDisplayer(1000));
    }

    /**
     * 指定imageView加载图片
     *
     * @param view
     * @param imageUrl
     * @param decodeConfig
     */
    private void loadImageView(final ImageView view, final String imageUrl, int maxWidth, int maxHeight,
                               Config decodeConfig, final BitmapDisplayer displayer) {
        ImageRequestTag tag = null;
        if (view.getTag() != null) {
            tag = (ImageRequestTag) view.getTag();
        } else {
            tag = new ImageRequestTag();
            view.setTag(tag);
        }

        ImageListener<Bitmap> imageListener = new ImageListener<Bitmap>() {

            @Override
            public void onResponse(final Bitmap response) {
                if (response != null) {
                    if (displayer != null) {
                        displayer.display(response, view);
                    } else {
                        view.setImageBitmap(response);
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(final ImageContainer response, boolean isImmediate) {
                try {
                    ImageRequestTag tag = (ImageRequestTag) view.getTag();
                    tag.imageContainer = null;

                    String url = tag.reqUrl;
                    if (url != null && url.startsWith(imageUrl)) {
                        if (response.getBitmap() != null) {
                            if (displayer != null) {
                                displayer.display(response.getBitmap(), view);
                            } else {
                                view.setImageBitmap(response.getBitmap());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (maxWidth > 0 && maxHeight > 0) {
            loadImageWithTag(tag, imageUrl, maxWidth, maxHeight, imageListener, decodeConfig);
        } else {
            loadImageWithTag(tag, imageUrl, imageListener, decodeConfig);
        }
    }

    public void loadImageViewWithDefaultImage(ImageView view, String url) {
        loadImageViewWithDefaultImage(view, url, DEFAULT_BITMAP_CONFIG);
    }

    public void loadImageViewWithDefaultImage(ImageView view, String url, Config decodeConfig) {
        loadImageViewWithDefaultImage(view, url, R.mipmap.thumbnail_default,
                R.mipmap.thumbnail_default, decodeConfig);
    }

    public void loadImageViewWithDefaultImage(ImageView view, String url, int defaultImg, int errorImg) {
        loadImageViewWithDefaultImage(view, url, defaultImg, errorImg, DEFAULT_BITMAP_CONFIG);
    }

    public void loadImageViewWithDefaultImage(ImageView view, String url, int defaultImg, int errorImg,
                                              Config decodeConfig) {
        if (StringUtil.isBlank(url)) {
            return;
        }

        ImageRequestTag tag;
        if (view.getTag() != null) {
            tag = (ImageRequestTag) view.getTag();
        } else {
            tag = new ImageRequestTag();
            view.setTag(tag);
        }

        mImageLoader.get(tag, url, ImageLoader.getImageListener(view, defaultImg, errorImg), decodeConfig);
    }

    public void addToLocalImageCache(Bitmap bitmap, String imageId) {
        mImageLoader.addLocalImage(bitmap, imageId);
    }

    public Bitmap getLocalImageFromCache(String imageId) {
        return mImageLoader.getLocalImage(imageId);
    }

    public void startHttpLoader(Request<?> request) {
        getmRequestQueue().add(request);
    }

    /**
     * 指定cacheKey加载url
     *
     * @param url
     * @param cacheKey
     * @param listener
     */
    public void startImageLoaderByCacheKey(String url, String cacheKey, ImageListener<Bitmap> listener) {
        if (StringUtil.isBlank(url)) {
            return;
        }

        mImageLoader.getByCacheKey(null, url, cacheKey, listener, DEFAULT_BITMAP_CONFIG);
    }

    public void startImageLoaderByCacheKey(String url, String cacheKey, ImageListener<Bitmap> listener,
                                           Config decodeConfig) {
        if (StringUtil.isBlank(url)) {
            return;
        }

        mImageLoader.getByCacheKey(null, url, cacheKey, listener, decodeConfig);
    }

    public Bitmap getImageByCacheKey(String cacheKey) {
        return mImageLoader.getBitmapByCacheKey(cacheKey);
    }

    public boolean removeImageCache(String cacheKey) {
        return mImageLoader.removeCache(cacheKey);
    }

    /**
     * 清除当前imageCache中的引用
     */
    public void clearImageCache() {
        if (imageCache != null) {
            imageCache.clearAll();
        }
    }

    public void release() {
        mRequestQueue.cancelAll(REQUEST_TAG);
        this.mImageLoader = null;
        this.mRequestQueue = null;
        mInstance = null;
    }
}
