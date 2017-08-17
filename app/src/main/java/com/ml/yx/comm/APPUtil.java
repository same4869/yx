package com.ml.yx.comm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.ml.yx.R;
import com.ml.yx.YouXinApplication;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressLint("InflateParams")
public class APPUtil {
	public static final int SCREEN_SMALL = 1;
	public static final int SCREEN_NORMAL = 2;
	public static final int SCREEN_LARGE = 3;
	public static final int SCREEN_XLARGE = 4;
	public static final int SCREEN_XXLARGE = 5;

	private static final String IMG_STRETCH = "-st";
	private static long lastClickTime;

	public static int getMaxBitmapSize(Context context) {
		int scMax = Math.max(ScreenUtils.getScreenHeight(context), ScreenUtils.getScreenWidth(context));

		int max = Math.max(scMax, 2048);

		return max;
	}

	public static int dpToPx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dipValue * scale + 0.5f);
	}

	public static int spToPx(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static void closeObject(Closeable closeable) {
		if (closeable == null) {
			return;
		}

		try {
			closeable.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getScreeType(Context context) {
		int screenWidth = ScreenUtils.getScreenWidth(context);
		if (screenWidth == 0) {
			return SCREEN_LARGE;
		}
		if (screenWidth >= 1080) {
			return SCREEN_XXLARGE;
		} else if (screenWidth >= 720) {
			return SCREEN_XLARGE;
		} else if (screenWidth >= 480) {
			return SCREEN_NORMAL;
		} else {
			return SCREEN_SMALL;
		}
	}

	@SuppressLint("NewApi")
	public static final void showToast(final Context context, final String text, final int duration) {
		if (context == null || TextUtils.isEmpty(text)) {
			return;
		}
		MyThreadPool.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, text, duration).show();
			}
		});

	}

	public static final void showToast(Context context,final String text) {
		showToast(context, text, Toast.LENGTH_LONG);
	}

	public static final void showToast(Context context, int textId) {
		showToast(context, context.getResources().getString(textId), Toast.LENGTH_LONG);
	}

	public static boolean checkSDCard(Context c) {
		File f = c.getExternalCacheDir();
		if (f == null || !f.exists() || !f.isDirectory() || !f.canWrite()) {
			return false;
		}
		return true;
	}

	public static boolean isFileExist(String str) {
		if (StringUtil.isEmpty(str)) {
			return false;
		}
		File file = new File(str);
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				if (fis.available() > 0) {
					return true;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * wifi网络是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWifiActive(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ninfo = cm.getActiveNetworkInfo();
		return ninfo != null && ninfo.isAvailable() && ninfo.getType() == ConnectivityManager.TYPE_WIFI && ninfo.getDetailedState() == DetailedState.CONNECTED;
	}

	/**
	 * 网络是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAction(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
			return true;
		}

		if (mobNetInfo != null && mobNetInfo.isConnected()) {
			return true;
		}

		return false;

	}

	@SuppressWarnings("deprecation")
	public static void generateNotification(Context context, String title, String message, Intent action, int type, int flag, boolean voice) {
		int icon = R.mipmap.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		PendingIntent intent = PendingIntent.getActivity(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setAutoCancel(true).setContentTitle(title).setContentText(message).setContentIntent(intent).setSmallIcon(icon).setWhen(when).setOngoing(true);
		Notification notification = builder.getNotification();
		notification.flags |= flag | Notification.FLAG_ONLY_ALERT_ONCE;
		if (voice) {
			notification.defaults = Notification.DEFAULT_ALL;
		}
		notificationManager.notify(1, notification);
	}

	/**
	 * cancel notification
	 *
	 * @param context
	 */

	public static void cancelAllNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}

	public static void toScore(Context context, String noMarket) {
		try {
			Uri uri = Uri.parse("market://details?id=com.wenba.bangbang");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			APPUtil.showToast(context, noMarket);
		}
	}

	public static String getString(int stringId) {
		return YouXinApplication.getInstance().getString(stringId);
	}

	public static String _ev2name(int action) {
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				return "down";
			case MotionEvent.ACTION_MOVE:
				return "move";
			case MotionEvent.ACTION_UP:
				return "up";
			case MotionEvent.ACTION_CANCEL:
				return "cancel";
		}
		return "other";
	}

	/**
	 * 获得手机安装应用的所有包名
	 *
	 * @param context
	 * @return
	 */
	public static ArrayList<String> getInstalledAppcations(Context context) {
		ArrayList<String> packageList = null;
		PackageManager packageManager = context.getPackageManager();
		List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
		if (installedApps != null) {
			packageList = new ArrayList<String>();
			for (ApplicationInfo appInfo : installedApps) {
				packageList.add(appInfo.packageName);
			}
		}
		return packageList;
	}

	/**
	 * 判断当前应用程序处于前台还是后台
	 *
	 * @param context
	 * @return
	 */
	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (tasks != null && !tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获得手机当前打开的应用包名
	 *
	 * @param context
	 * @return
	 */
	public static String getTopApplication(final Context context) {
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasks = am.getRunningTasks(1);
			if (tasks != null && !tasks.isEmpty()) {
				ComponentName topActivity = tasks.get(0).topActivity;
				if (topActivity != null) {
					return topActivity.getPackageName();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Intent getSystemCameraIntent(Context context, String picPath) {
		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);

		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

		for (ResolveInfo info : infos) {

			ApplicationInfo appInfo = info.activityInfo.applicationInfo;
			if (appInfo == null) {
				continue;
			}

			try {
				Field field = ResolveInfo.class.getDeclaredField("system");

				boolean isSystem = field.getBoolean(info);
				if (isSystem) {
					intent.setPackage(appInfo.packageName);
					break;
				}

			} catch (Exception e) {
				if (appInfo.publicSourceDir != null && appInfo.publicSourceDir.contains("/system")) {
					intent.setPackage(appInfo.packageName);
				}
			}
		}

		if (picPath != null) {
			Uri takeCameraOutUri = Uri.fromFile(new File(picPath));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, takeCameraOutUri);
		}
		return intent;
	}



	/**
	 * 获得手机型号
	 *
	 * @return
	 */
	public static String getPhoneType() {
		return android.os.Build.BRAND + "_" + android.os.Build.MODEL;
	}

	public static String getStretchPath(String imgPath) {
		if (imgPath == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int index = imgPath.lastIndexOf(".");
		if (index != -1) {
			sb.append(imgPath.substring(0, index)).append(IMG_STRETCH).append(imgPath.substring(index));
		} else {
			sb.append(imgPath).append(IMG_STRETCH);
		}
		return sb.toString();
	}

	/**
	 * 获得随机密码
	 *
	 * @param context
	 * @return
	 */
	public static String randomPassword(Context context) {
		String password = UUID.randomUUID().toString().replace("-", "");

		if (password.length() > 12) {
			password = password.substring(0, 12);
		}
		return password;
	}

	public static String readTextAsset(String name) {
		InputStream stream = null;

		String content = null;
		try {
			stream = YouXinApplication.getInstance().getResources().getAssets().open(name);

			byte[] data = new byte[stream.available()];

			stream.read(data);

			content = new String(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeObject(stream);
		}

		return content;
	}

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 添加快捷方式
	 */
	public static void creatShortCut(Activity activity, String shortcutName, int resourceId) {
		Intent intent = new Intent();
		intent.setClass(activity, activity.getClass());
		/* 以下两句是为了在卸载应用的时候同时删除桌面快捷方式 */
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
		// 快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), resourceId);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 点击快捷图片，运行的程序主入口
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播。OK
		activity.sendBroadcast(shortcutintent);
	}

	/**
	 * 删除快捷方式
	 */
	public static void deleteShortCut(Activity activity, String shortcutName) {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
		// 在网上看到到的基本都是一下几句，测试的时候发现并不能删除快捷方式。
		// String appClass = activity.getPackageName()+"."+
		// activity.getLocalClassName();
		// ComponentName comp = new ComponentName( activity.getPackageName(),
		// appClass);
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new
		// Intent(Intent.ACTION_MAIN).setComponent(comp));
		/** 改成以下方式能够成功删除，估计是删除和创建需要对应才能找到快捷方式并成功删除 **/
		Intent intent = new Intent();
		intent.setClass(activity, activity.getClass());
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		activity.sendBroadcast(shortcut);
	}

	/**
	 * 判断是否存在快捷方式
	 */
	public static boolean hasShortcut(Activity activity, String shortcutName) {
		String url = "";
		/* 大于8的时候在com.android.launcher2.settings 里查询（未测试） */
		if (android.os.Build.VERSION.SDK_INT < 8) {
			url = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			url = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		ContentResolver resolver = activity.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(url), null, "title=?", new String[] { shortcutName }, null);
		if (cursor != null && cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		return false;
	}

	/**
	 * 获得当前应用的运行情况
	 *
	 * @return
	 */
	public static String getAppRuntimeStatus() {
		Runtime runtime = Runtime.getRuntime();

		long maxMemory = runtime.maxMemory();
		long useMemory = runtime.totalMemory() - runtime.freeMemory();
		long nativeUseMemory = Debug.getNativeHeapAllocatedSize();

		boolean externalStorageAvailable = isExternalStorageAvailable();
		long availableInternalMemorySize = getAvailableInternalMemorySize();
		long totalInternalMemorySize = getTotalInternalMemorySize();
		long availableExternalMemorySize = getAvailableExternalMemorySize();
		long totalExternalMemorySize = getTotalExternalMemorySize();

		long block = 1024 * 1024;

		JsonObject json = new JsonObject();
		json.addProperty("maxMemory", maxMemory / block);
		json.addProperty("useMemory", useMemory / block);
		json.addProperty("nativeUseMemory", nativeUseMemory / block);
		json.addProperty("externalStorageAvailable", externalStorageAvailable ? 1 : 0);
		json.addProperty("availableInternalMemorySize", availableInternalMemorySize / block);
		json.addProperty("totalInternalMemorySize", totalInternalMemorySize / block);
		json.addProperty("availableExternalMemorySize", availableExternalMemorySize / block);
		json.addProperty("totalExternalMemorySize", totalExternalMemorySize / block);
		return json.toString();
	}

	/**
	 *
	 * 外部存储是否可用 (存在且具有读写权限)
	 *
	 * @return
	 */

	public static boolean isExternalStorageAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 *
	 * 获取手机内部可用空间大小
	 *
	 * @return
	 */

	@SuppressWarnings("deprecation")
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());

		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 *
	 * 获取手机内部空间大小
	 *
	 * @return
	 */

	@SuppressWarnings("deprecation")
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();// Gets the Android data
		// directory
		StatFs stat = new StatFs(path.getPath());

		long blockSize = stat.getBlockSize(); // 每个block 占字节数
		long totalBlocks = stat.getBlockCount(); // block总数
		return totalBlocks * blockSize;
	}

	/**
	 *
	 * 获取手机外部可用空间大小
	 *
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getAvailableExternalMemorySize() {
		if (isExternalStorageAvailable()) {
			File path = Environment.getExternalStorageDirectory();// 获取SDCard根目录
			StatFs stat = new StatFs(path.getPath());

			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return -1;
		}
	}

	/**
	 *
	 * 获取手机外部总空间大小
	 *
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getTotalExternalMemorySize() {
		if (isExternalStorageAvailable()) {
			File path = Environment.getExternalStorageDirectory(); // 获取SDCard根目录
			StatFs stat = new StatFs(path.getPath());

			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return -1;
		}
	}

	/**
	 * 获取当前进程名
	 *
	 * @param context
	 * @return 进程名
	 */
	public static final String getProcessName(Context context) {
		String processName = null;

		ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

		while (true) {
			for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
				if (info.pid == android.os.Process.myPid()) {
					processName = info.processName;
					break;
				}
			}

			// go home
			if (!TextUtils.isEmpty(processName)) {
				return processName;
			}

			// take a rest and again
			try {
				Thread.sleep(100L);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static int createSymbolLink(String target, String linkName) {
		if (!new File(target).exists()) {
			return -1;
		}

		File linkFile = new File(linkName);

		if (!linkFile.getParentFile().exists()) {
			return -1;
		}

		if (linkFile.exists()) {
			if (linkFile.isDirectory()) {
				return -1;
			}

			linkFile.delete();
		}

		int code = -1;

		try {
			code = Runtime.getRuntime().exec("ln -s " + target + " " + linkName).waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return code;
	}
}
