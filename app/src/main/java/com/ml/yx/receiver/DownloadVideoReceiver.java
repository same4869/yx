package com.ml.yx.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import com.ml.yx.comm.CacheStoreUtil;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.manager.DownloadVideoManager;
import com.ml.yx.utils.DownloadHandler;

/**
 * Created by xunwang on 16/4/22.
 */
public class DownloadVideoReceiver extends BroadcastReceiver {
    public static final String DOWNLOAD_LEVEL_VIDEO_FINISH_ACTION = "download_level_video_finish_action";//每个视频下载完
    public static final String DOWNLOAD_LEVEL_ALL_VIDEO_FINISH_ACTION = "download_level_all_video_finish_action";//关卡所有视频下载完
    public static final String DOWNLOAD_ALL_VIDEO_FINISH_ACTION = "download_all_video_finish_action";//所有视频下载完

    public static final String DOWNLOAD_LEVEL_VIDEO_PROGRESS_KEY = "download_level_video_progress_key";//一关视频的哪一个
    public static final String DOWNLOAD_LEVELS_VIDEO_PROGRESS_KEY = "download_levels_video_progress_key";//第几关视频

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadHandler.BROADCAST_DOWNLOAD_STOPED)) {
            String downloadUrl = intent.getStringExtra(DownloadHandler.PARAM_URL);
            String statusString = intent.getStringExtra(DownloadHandler.PARAM_STATUS);
            DownloadHandler.DownloadStatus status = DownloadHandler.DownloadStatus.CANCELED;
            if (statusString != null) {
                status = DownloadHandler.DownloadStatus.valueOf(statusString);
            }
            switch (status) {
                case COMPLETED:
                    //BBLog.d("kkkkkkkk", "download completed!!!!");
                    //每个视频下载完成广播出当前关卡的index
                    SharedPreferencesUtil.setIsDownloading(false);
                    Intent downloadIntent = new Intent(DOWNLOAD_LEVEL_VIDEO_FINISH_ACTION);
                    downloadIntent.putExtra(DOWNLOAD_LEVEL_VIDEO_PROGRESS_KEY, DownloadVideoManager.getInstance().getDownloadProgress());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(downloadIntent);
                    if (DownloadVideoManager.getInstance().getLevelVideoList() == null) {
                        return;
                    }
                    //如果当前关卡下载完毕，判断下个关卡是否解锁，解锁继续下载并广播出关卡index,否则停止下载
//                    Log.d("kkkkkkkk", "DownloadVideoManager.getInstance().getCurrentPosition() --> " + DownloadVideoManager.getInstance().getCurrentPosition() + " DownloadVideoManager.getInstance().getLevelVideoList().size() --> " + DownloadVideoManager.getInstance().getLevelVideoList().size());
                    if (DownloadVideoManager.getInstance().getCurrentPosition() == DownloadVideoManager.getInstance().getLevelVideoList().size() - 1) {
                        DownloadVideoManager.getInstance().setCurrentAllPosition(DownloadVideoManager.getInstance().getCurrentAllPosition() + 1);
//                        Log.d("kkkkkkkk", "DownloadVideoManager.getInstance().getCurrentAllPosition() --> " + DownloadVideoManager.getInstance().getCurrentAllPosition() + " DownloadVideoManager.getInstance().getUnlockedLevel() --> " + DownloadVideoManager.getInstance().getUnlockedLevel());
                        if (DownloadVideoManager.getInstance().getCurrentAllPosition() > DownloadVideoManager.getInstance().getUnlockedLevel()) {
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DOWNLOAD_ALL_VIDEO_FINISH_ACTION));
                            SharedPreferencesUtil.setIsDownloading(false);
                            return;
                        }
                        Intent downloadLevelIntent = new Intent(DOWNLOAD_LEVEL_ALL_VIDEO_FINISH_ACTION);
                        downloadIntent.putExtra(DOWNLOAD_LEVELS_VIDEO_PROGRESS_KEY, DownloadVideoManager.getInstance().getCurrentAllPosition());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(downloadLevelIntent);
                        DownloadVideoManager.getInstance().startDownload(context);
                    } else {
                        DownloadVideoManager.getInstance().startDownload(context);
                    }
                    //所以关卡下载完
                    if (DownloadVideoManager.getInstance().getCurrentAllPosition() == DownloadVideoManager.getInstance().getAllVideoList().size()) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DOWNLOAD_ALL_VIDEO_FINISH_ACTION));
                        SharedPreferencesUtil.setIsDownloading(false);
                    }
                    break;
                case NO_NETWORK:
                case ERROR:
                    //DownloadVideoManager.getInstance().startDownload(context);
                case CANCELED:
                    //如果出错则删除当前正在下载的避免文件不完整
                    DownloadVideoManager.getInstance().deleteFile(CacheStoreUtil.getCacheDir(context).getAbsolutePath() + "/" + DownloadVideoManager.getInstance().getCurrentDownloadingFileName());
                    DownloadVideoManager.getInstance().startDownload(context);
                    break;
                default:
                    break;
            }

        } else if (intent.getAction().equals(DownloadHandler.BROADCAST_DOWNLOAD_DOWNLOADING)) {
            String taskId = intent.getStringExtra(DownloadHandler.PARAM_TASKID);
            String downloadUrl = intent.getStringExtra(DownloadHandler.PARAM_URL);
            int percent = intent.getIntExtra(DownloadHandler.PARAM_PERCENT, 0);
//            if (DownloadVideoManager.getInstance().getAllVideoList() != null) {
//                BBLog.d("kkkkkkkk", "DownloadVideoManager.getInstance().getCurrentAllPosition() --> " + DownloadVideoManager.getInstance().getCurrentAllPosition() + " DownloadVideoManager.getInstance().getCurrentPosition() --> " + DownloadVideoManager.getInstance().getCurrentPosition() + "percent --> " + percent + " downloadUrl --> " + downloadUrl + " DownloadVideoManager.getInstance().getAllVideoList().size() --> " + DownloadVideoManager.getInstance().getAllVideoList().size());
//            }

        } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
            //BBLog.d("kkkkkkkk", "网络状态切换");
            if (netInfo != null && netInfo.isAvailable()) {
                DownloadVideoManager.getInstance().startDownload(context);
            } else {

            }
        }
    }

}
