package com.ml.yx.comm;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class CacheStoreUtil {

	private static boolean isAvailableDirectory(File file) {
		if (file == null) {
			return false;
		}
		
		if (!file.exists()) {
			return false;
		}
		
		if (!file.isDirectory()) {
			return false;
		}
		
		if (file.listFiles() == null) {
			return false;
		}
		
		if (!file.canWrite()) {
			return false;
		}
		
		return true;
	}

	private static File getRootDir(Context ctx, String childPath,boolean external) {
		File file = null;
		if (ctx != null) {
			file = ctx.getExternalCacheDir();
			if (!isAvailableDirectory(file)) {
				if(!external){
					file = ctx.getCacheDir();
					if (!isAvailableDirectory(file)) {
						file = ctx.getFilesDir();
						if (!isAvailableDirectory(file)) {
							file = null;
						}
					}
				}else{
					file = null;
				}
			}
		}
		if (file == null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			file = Environment.getExternalStorageDirectory();
		}
		if (file != null) {
			file = new File(file, childPath);
		}
		if (file != null){
			if(!file.exists()){
				file.mkdirs();
			}
		}
		return file;
	}

	public static File getCacheDir(Context ctx) {
		return getRootDir(ctx, Constants.WENBA_CACHE,false);
	}

	public static File getLogsDir(Context ctx) {
		return getRootDir(ctx, Constants.WENBA_LOGS,false);
	}
	
	public static File getUserEventDir(Context ctx){
		return getRootDir(ctx, Constants.WENBA_EVENTS,false);
	}



}
