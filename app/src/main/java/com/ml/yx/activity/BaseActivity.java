package com.ml.yx.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;

import com.bugtags.library.Bugtags;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.views.LoadingDialog;
import com.ml.yx.views.YouXinDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lijj on 16/3/23.
 */
public abstract class BaseActivity extends FragmentActivity{
    private boolean isDestroyed = false;
    protected LoadingDialog loadingDialog;
    protected YouXinDialog youXinDialog;

    private long startTime = System.currentTimeMillis();

    private  Map<String,String> actionParams = new HashMap<String,String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bugtags.onResume(this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (isDestroyed) {
            return null;
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
        return null;
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, boolean system) {
        if (system) {
            return super.registerReceiver(receiver, filter);
        }
        return registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
    }

    public void unregisterReceiver(BroadcastReceiver receiver, boolean system) {
        if (system) {
            super.unregisterReceiver(receiver);
        } else {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        }
    }

    protected void addActionEvent(String key,String value){
        actionParams.put(key,value);
    }

    @Override
    public void finish() {
        super.finish();

        if(getPageCode() != null){
            long time = System.currentTimeMillis() - startTime;

            addActionEvent(ActionWebService.PARAMS_V70,String.valueOf(time));
            addActionEvent(ActionWebService.PARAMS_EVENTID,getPageCode());

            ActionWebService.requestAction(getApplicationContext(),ActionWebService.USER_ACTION_URL,actionParams);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        cancelLoadingDialog();
        cancelDialog();
    }

    protected void showLoadingDialog() {
        if (isActivityDestroyed()) {
            return;
        }

        cancelLoadingDialog();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
    }

    public void showLoadingDialog(String message) {
        showLoadingDialog();
        loadingDialog.setContentMessage(message);
    }

    public void setLoadingDialogMessage(String message){
        if(loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.setContentMessage(message);
        }
    }

    public void cancelLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.cancel();
        }

        loadingDialog = null;
    }

    public void cancelDialog() {
        if (youXinDialog != null && youXinDialog.isShowing()) {
            youXinDialog.dismiss();
        }

        youXinDialog = null;
    }

    public void setYouXinDialog(YouXinDialog dialog) {
        this.youXinDialog = dialog;
    }

    protected boolean isActivityDestroyed() {
        return isDestroyed;
    }

    protected abstract String getPageCode();
}
