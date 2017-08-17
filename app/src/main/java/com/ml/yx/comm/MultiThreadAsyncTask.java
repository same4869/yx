package com.ml.yx.comm;

import java.util.concurrent.Executor;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

public class MultiThreadAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	public static final Executor getExecutor() {
		return MyThreadPool.getExecutor();
	}
	
	public static void poolExecute(Runnable runnable) {
		getExecutor().execute(runnable);
	}

	@Override
	protected Result doInBackground(Params... params) {
		return null;
	}
	
	@SuppressLint("NewApi")
	public AsyncTask<Params, Progress, Result> executeMultiThread(Params... params) {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			return super.executeOnExecutor(getExecutor(), params);
		} else {
			return execute(params);
		}
	}

}
