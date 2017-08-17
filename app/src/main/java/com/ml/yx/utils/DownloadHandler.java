/**
 *
 */
package com.ml.yx.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ml.yx.YouXinApplication;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.MultiThreadAsyncTask;
import com.ml.yx.db.DownloadTaskDBHelper;

import java.util.HashMap;
import java.util.Map.Entry;


/**
 * @author Mr.Yang
 */
public class DownloadHandler {

    private static final String TAG = DownloadHandler.class.getSimpleName();

    public static enum DownloadStatus {
        INITIALIZED, DOWNLOADING, COMPLETED, PAUSED, INTERRUPTED, CANCELED, ERROR, NO_NETWORK, DownloadStatus, DISCONNECTED
    }

    public static final int NETWORK_STATE_ERROR = -1; // 网络状态获取异常
    public static final int NETWORK_STATE_NO_CONNECTION = 0; // 网络状态当前没有连接
    public static final int NETWORK_STATE_WIFI = 1; // 当前使用的是wifi
    public static final int NETWORK_STATE_MOBILE = 2; // 当前使用的是移动数据

    public static final String PARAM_TASKID = "param_task_id";
    public static final String PARAM_URL = "param_url";
    public static final String PARAM_PERCENT = "param_percent";
    public static final String PARAM_STATUS = "param_status";

    public static final String BROADCAST_DOWNLOAD_DOWNLOADING = "com.ml.yx.broadcast.download.downloading";
    public static final String BROADCAST_DOWNLOAD_STOPED = "com.ml.yx.broadcast.download.stoped";

    private static HashMap<String, DownloadTask> taskPool = new HashMap<String, DownloadTask>();

    private static Context getContext() {
        return YouXinApplication.getInstance();
    }

    public static void download(String taskId, boolean wifiOnly) {
        DownloadTask task = taskPool.get(taskId);
        if (task != null) {
            task.setWifiOnly(wifiOnly);
            task.setStatus(DownloadStatus.DOWNLOADING);
            MultiThreadAsyncTask.getExecutor().execute(task);
        } else {
            Intent intent = new Intent(BROADCAST_DOWNLOAD_STOPED);
            intent.putExtra(PARAM_TASKID, taskId);
            intent.putExtra(PARAM_STATUS, DownloadStatus.ERROR.toString());
            getContext().sendBroadcast(intent);
            //LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
        }
    }

    public static String download(final String url, final String destFile) {
        return download(url, destFile, false);
    }

    public static String download(final String url, final String destFile, boolean wifiOnly) {
        synchronized (DownloadHandler.class) {
            DownloadTask task = findTaskInPool(url);
            if (task == null) {
                task = DownloadTaskDBHelper.getInstance().find(url);
                DownloadTask.DownloadListener downloadListener = new DownloadTask.DownloadListener() {

                    int lastPercent = 0;

                    @Override
                    public void onDownload(String taskId, int totalSize, int curSize) {
                        int percent = (int) (((float) curSize / totalSize) * 100);
                        if (percent != lastPercent) {
                            lastPercent = percent;
                            Intent intent = new Intent(BROADCAST_DOWNLOAD_DOWNLOADING);
                            intent.putExtra(PARAM_TASKID, taskId);
                            intent.putExtra(PARAM_URL, url);
                            intent.putExtra(PARAM_PERCENT, percent);
                            getContext().sendBroadcast(intent);
                        }
                    }

                    @Override
                    public void onStop(DownloadStatus status, String taskId, int totalSize, int curSize) {
                        DownloadTask task = DownloadTaskDBHelper.getInstance().find(url);
                        if (task == null) {
                            DownloadTaskDBHelper.getInstance().save(taskId, url, totalSize, curSize);
                        } else {
                            DownloadTaskDBHelper.getInstance().update(url, totalSize, curSize);
                        }
                        if (status == DownloadStatus.COMPLETED || status == DownloadStatus.CANCELED) {
                            taskPool.remove(taskId);
                            DownloadTaskDBHelper.getInstance().delete(url);
                        }

                        Intent intent = new Intent(BROADCAST_DOWNLOAD_STOPED);
                        intent.putExtra(PARAM_TASKID, taskId);
                        intent.putExtra(PARAM_URL, url);
                        intent.putExtra(PARAM_STATUS, status.toString());
                        getContext().sendBroadcast(intent);
                        //LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
                    }

                };
                if (task == null) {
                    task = new DownloadTask(getTaskId(), url, destFile, 0, 0, downloadListener);
                } else {
                    task.setDestFile(destFile);
                    task.setListener(downloadListener);
                }
                //BBLog call replaced
                taskPool.put(task.getTaskId(), task);
            } else {
                //BBLog call replaced
            }
            if (task.getStatus() == DownloadStatus.DOWNLOADING) {
                //BBLog call replaced
                return task.getTaskId();
            }
            //BBLog call replaced
            task.setStatus(DownloadStatus.DOWNLOADING);
            task.setWifiOnly(wifiOnly);
            MultiThreadAsyncTask.getExecutor().execute(task);
            return task.getTaskId();
        }
    }

    private static String getTaskId() {
        String taskId;
        taskId = "dlt" + System.currentTimeMillis();
        return taskId;
    }

    public static void pause(String taskId) {
        if (taskId == null) {
            return;
        }
        DownloadTask task = taskPool.get(taskId);
        if (task != null) {
            task.setPaused();
        }
    }

    public static void cancelTask(String taskId) {
        if (taskId == null) {
            return;
        }
        DownloadTask task = taskPool.get(taskId);
        if (task != null) {
            task.setStatus(DownloadStatus.CANCELED);
        }
    }

    public static DownloadTask findTaskInPool(String url) {
        for (Entry<String, DownloadTask> entry : taskPool.entrySet()) {
            DownloadTask task = (DownloadTask) entry.getValue();
            if (task != null && task.getUrl().equals(url)) {
                return task;
            }
        }
        return null;
    }

    /**
     * 获取网络类型 -1:错误 0:无连接 1:wifi 2:移动数据
     *
     * @return
     */
    public static int getNetworkType() {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                            return NETWORK_STATE_WIFI;
                        } else {
                            return NETWORK_STATE_MOBILE;
                        }
                    } else {
                        return NETWORK_STATE_NO_CONNECTION;
                    }
                } else {
                    return NETWORK_STATE_NO_CONNECTION;
                }
            }
        } catch (Exception e) {
            //BBLog call replaced
            return NETWORK_STATE_ERROR;
        }
        return NETWORK_STATE_ERROR;
    }

}
