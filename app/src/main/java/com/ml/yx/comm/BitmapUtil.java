package com.ml.yx.comm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;

import com.ml.yx.YouXinApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressLint("NewApi")
public class BitmapUtil {
	private static final int MAX_IMG = 1024 * 1024;

	public static Bitmap getBmpFromUriSafely(Context context, Uri uri, int screenWidth, int screenHeight) {
		int maxSize = APPUtil.getMaxBitmapSize(context);
		try {
			return getBmpFromUriByLimit(uri, maxSize, maxSize);
		} catch (OutOfMemoryError error) {
			maxSize = maxSize / 2;
			if (screenWidth > maxSize) {
				screenWidth = maxSize;
			}
			if (screenHeight > maxSize) {
				screenHeight = maxSize;
			}
			try {
				return getBmpFromUriByLimit(uri, screenWidth, screenHeight);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param bitmap
	 *            , probably recyled by call this function
	 * @return properly scaled bitmap
	 * @throws FileNotFoundException
	 */
	public static Bitmap getBmpFromUriByLimit(Uri uri, int sw, int sh) throws FileNotFoundException, OutOfMemoryError {
		Context c = YouXinApplication.getInstance();

		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		bmpFactoryOptions.inPreferredConfig = Config.RGB_565;
		BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, bmpFactoryOptions);

		int widthRatio = sw == -1 ? 1 : (int) Math.ceil(bmpFactoryOptions.outWidth / (float) sw);

		int heightRatio = sh == -1 ? 1 : (int) Math.ceil(bmpFactoryOptions.outHeight / (float) sh);

		if (heightRatio >= 1 && widthRatio >= 1) {
			bmpFactoryOptions.inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
		}
		bmpFactoryOptions.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, bmpFactoryOptions);

		return bm;
	}

	public static Bitmap getBmpFromFileSafely(Context context, String path, int width, int height) {
		Uri uri = Uri.fromFile(new File(path));
		int maxSize = APPUtil.getMaxBitmapSize(context);
		try {
			return getBmpFromUriByLimit(uri, maxSize, maxSize);
		} catch (OutOfMemoryError error) {
			maxSize = maxSize / 2;
			if (width > maxSize) {
				width = maxSize;
			}
			if (height > maxSize) {
				height = maxSize;
			}
			try {
				return getBmpFromUriByLimit(uri, width, height);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void formatBitmap(String path, boolean finalSize, CompressFormat format) {
		formatBitmap(path, finalSize, format, 0);
	}

	public static int formatBitmap(String path, boolean finalSize, CompressFormat format, int angle) {
		Bitmap result = null;
		int fangle = 0;
		try {
			ExifInterface exif = new ExifInterface(path);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			FileInputStream fis = new FileInputStream(path);
			Bitmap bmp = null;
			float maxSize = 1024.0f;
			if (Build.VERSION.SDK_INT >= 14) {
				maxSize = 2048.0f;
			}
			// limit the bitmap size to at most 1024
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			opts.inPreferredConfig = Config.RGB_565;
			BitmapFactory.decodeStream(fis, null, opts);
			int ratio = (int) (Math.max(opts.outWidth, opts.outHeight) / maxSize + 0.9999f);
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = ratio;
			fis = new FileInputStream(path);
			bmp = BitmapFactory.decodeStream(fis, null, opts);
			FileOutputStream fos = new FileOutputStream(path);
			if (bmp != null) {
				bmp.compress(format, 100, fos);
			}
			fos.close();
			fis.close();
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				fangle = 90 - angle;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				fangle = 180 - angle;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				fangle = 270 - angle;
				break;
			default:
				fangle = -angle;
				break;
			}
			result = rotateBmp(fangle, path, bmp, 100);
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result != null) {
			result.recycle();
		}
		return fangle;
	}

	public static int getBitmapAngle(String path) {
		if (path.contains("file://")) {
			path = path.replace("file://", "");
		}
		int fangle = 0;
		try {
			ExifInterface exif = new ExifInterface(path);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				fangle = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				fangle = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				fangle = 270;
				break;
			default:
				fangle = 0;
				break;
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// BBLog call replaced
		;
		return fangle;
	}

	public static Bitmap rotateBmp(int angle, String path, Bitmap bmp, int quality) {
		if (bmp == null) {
			return null;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap result = null;
		FileOutputStream outputStream = null;
		try {
			result = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
			if (result != null) {
				if (result != bmp) {
					bmp.recycle();
				}
			} else {
				result = bmp;
			}

			outputStream = new FileOutputStream(path);
			result.compress(CompressFormat.JPEG, quality, outputStream);
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
					outputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static String copyUriToPath(Context c, String uri, String path) {
		try {
			InputStream is = c.getContentResolver().openInputStream(Uri.parse(uri));
			OutputStream os = new FileOutputStream(path);
			byte[] buf = new byte[1024];
			while (is.read(buf) > 0) {
				os.write(buf);
			}
			is.close();
			os.close();
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return path;

	}

	public static String copyFileToPath(Context c, String oldfile, String path) {
		try {
			InputStream is = new FileInputStream(oldfile);
			OutputStream os = new FileOutputStream(path);
			byte[] buf = new byte[1024];
			while (is.read(buf) > 0) {
				os.write(buf);
			}
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return path;

	}

	public static boolean moveBitmapFromUri(Context c, String uri, String disPath) {
		boolean flag = true;
		InputStream is = null;
		OutputStream os = null;
		try {
			is = c.getContentResolver().openInputStream(Uri.parse(uri));
			os = new FileOutputStream(disPath);
			byte[] buf = new byte[1024];
			while (is.read(buf) > 0) {
				os.write(buf);
			}
			is.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static boolean copyFile(Context c, String srcPath, String disPath) {
		boolean flag = true;
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(srcPath);
			os = new FileOutputStream(disPath);
			byte[] buf = new byte[1024];
			while (is.read(buf) > 0) {
				os.write(buf);
			}
			is.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static void limitBmp(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			int length = fis.available();
			fis.close();
			if (length >= MAX_IMG) {
				int ratio = (int) (Math.sqrt(length * 1.0 / MAX_IMG) + 0.9999);
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = ratio;
				Bitmap newBmp = BitmapFactory.decodeFile(path, opts);
				FileOutputStream fos = new FileOutputStream(path);
				newBmp.compress(CompressFormat.JPEG, 100, fos);
				fos.close();
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getBitmapFromPath(String path) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeFile(path);
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 2;
				bm = BitmapFactory.decodeFile(path, options);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}

	public static Bitmap getBitmap(Context context, int drawableId) {
		return getBitmap(context.getResources(), drawableId,Config.RGB_565);
	}
	
	public static Bitmap getBitmap(Context context, int drawableId,Config config) {
		return getBitmap(context.getResources(), drawableId,config);
	}
	
	public static Bitmap getBitmap(Resources resources, int drawableId) {
		return getBitmap(resources, drawableId, Config.RGB_565);
	}

	public static Bitmap getBitmap(Resources resources, int drawableId,Config mDecodeConfig) {
		Bitmap bitmap = null;
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.inPreferredConfig = mDecodeConfig;
		try {
			bitmap = BitmapFactory.decodeResource(resources, drawableId);
			
			//对rgb进行判断，如果与指定rgb不一致，通过指定目标rgb转换一次
			Config config = bitmap.getConfig();
			if(config != null && config.compareTo(mDecodeConfig) != 0){
				Bitmap tempBitamp = bitmap;
				bitmap = tempBitamp.copy(mDecodeConfig, false);
				tempBitamp.recycle();
				tempBitamp = null;
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap getImageFromPath(String srcPath, float ww, float hh) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		float w = newOpts.outWidth;
		float h = newOpts.outHeight;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (ww != 0 && hh != 0) {
			if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
				be = (int) Math.floor(w / ww);
			} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
				be = (int) Math.floor(h / hh);
			}
		}
		if (be <= 0) {
			be = 1;
		} else if (be < 1) {
			be = 1;
		}
		newOpts.inSampleSize = be;// 设置缩放比例
		Bitmap bitmap = null;
		try {
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		} catch (OutOfMemoryError err) {
			err.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static int[] getBmpSizeFromPath(Context c, String path) {
		int[] size = new int[2];
		try {
			BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
			bmpFactoryOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, bmpFactoryOptions);

			int width = bmpFactoryOptions.outWidth;
			int height = bmpFactoryOptions.outHeight;
			size[0] = width;
			size[1] = height;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static LayoutParams getLayoutParams(LayoutParams layoutParams, Bitmap bitmap, int screenWidth, boolean widthInvariant) {

		float rawWidth = bitmap.getWidth();
		float rawHeight = bitmap.getHeight();

		int width = 0;
		int height = 0;

		// BBLog call replaced
		// BBLog call replaced

		if (rawWidth > screenWidth) {
			height = (int) ((rawHeight / rawWidth) * screenWidth);
			width = screenWidth;
		} else {
			width = (int) rawWidth;
			height = (int) rawHeight;
		}

		// BBLog call replaced

		if (layoutParams == null) {
			layoutParams = new LayoutParams(width, height);
		} else {
			if (!widthInvariant) {
				layoutParams.width = width;
			}
			layoutParams.height = height;
		}
		return layoutParams;
	}

	public static double getRadarAngle(Point pntFirst, Point pntNext) {
		double dRotateAngle = Math.atan2(Math.abs(pntNext.y - pntFirst.y), Math.abs(pntNext.x - pntFirst.x));

		if (pntNext.x >= pntFirst.x) {
			if (pntNext.y <= pntFirst.y) {
				dRotateAngle = Math.PI / 2 - dRotateAngle;
			} else {
				dRotateAngle = Math.PI / 2 + dRotateAngle;
			}
		} else {
			if (pntNext.y <= pntFirst.y) {
				dRotateAngle = Math.PI / 2 * 3 + dRotateAngle;
			} else {
				dRotateAngle = Math.PI / 2 * 3 - dRotateAngle;
			}
		}
		dRotateAngle = dRotateAngle * 180 / Math.PI;
		return dRotateAngle;
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	public static Bitmap convertViewToBitmap(View view, int width, int height) {
		view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		view.layout(0, 0, width, height);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, float width, float height) {
		if (bitmap == null) {
			return null;
		}
		float scaleX = width / bitmap.getWidth();
		float scaleY = height / bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.postScale(scaleX, scaleY); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		// 保证是方形，并且从中心画
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int w;
		int deltaX = 0;
		int deltaY = 0;
		if (width <= height) {
			w = width;
			deltaY = height - w;
		} else {
			w = height;
			deltaX = width - w;
		}
		final Rect rect = new Rect(deltaX, deltaY, w, w);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		// 圆形，所有只用一个

		int radius = (int) (Math.sqrt(w * w * 2.0d) / 2);
		canvas.drawRoundRect(rectF, radius, radius, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
}
