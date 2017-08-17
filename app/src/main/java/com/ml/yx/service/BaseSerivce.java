package com.ml.yx.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public abstract class BaseSerivce extends Service{
	
	@Override
	public void sendBroadcast(Intent intent) {
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
	}
	
	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
		return null;
	}
	
	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
	}


}
