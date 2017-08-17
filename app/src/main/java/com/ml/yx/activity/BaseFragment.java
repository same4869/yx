package com.ml.yx.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.views.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lijj on 16/3/23.
 */
public abstract class BaseFragment extends Fragment{
    private boolean isDestroyed = false;
    protected LoadingDialog loadingDialog;

    private long startTime = System.currentTimeMillis();

    private Map<String,String> actionParams = new HashMap<String,String>();

    public String getTagText(){
        return this.getClass().getSimpleName();
    }

    public Context getApplicationContext(){
        return getActivity().getApplicationContext();
    }

    public void sendBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (isDestroyed) {
            return null;
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
        return null;
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, boolean system) {
        if (system) {
            return getActivity().registerReceiver(receiver, filter);
        }
        return registerReceiver(receiver, filter);
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
    }

    public void unregisterReceiver(BroadcastReceiver receiver, boolean system) {
        if (system) {
            getActivity().unregisterReceiver(receiver);
        } else {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        }
    }

    protected boolean isActivityDestroyed() {
        return isDestroyed;
    }

    public boolean isDetachedActivity() {
        if (getActivity() == null || isDetached() || isActivityDestroyed()) {
            return true;
        }
        return false;
    }

    protected void addActionEvent(String key,String value){
        actionParams.put(key,value);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(getPageCode() != null){
            long time = System.currentTimeMillis() - startTime;

            addActionEvent(ActionWebService.PARAMS_V70,String.valueOf(time));
            addActionEvent(ActionWebService.PARAMS_EVENTID,getPageCode());

            ActionWebService.requestAction(getApplicationContext(),ActionWebService.USER_ACTION_URL,actionParams);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        cancelLoadingDialog();
    }

    protected abstract String getPageCode();

    protected void showLoadingDialog() {
        if (isActivityDestroyed() || getActivity() == null) {
            return;
        }
        loadingDialog = new LoadingDialog(getActivity());
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

    protected void cancelLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.cancel();
        }
        loadingDialog = null;
    }
}
