package com.ml.yx.manager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ml.yx.comm.CacheStoreUtil;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.receiver.DownloadVideoReceiver;
import com.ml.yx.utils.DownloadHandler;

import java.io.File;
import java.util.List;

/**
 * Created by xunwang on 16/4/22.
 */
public class DownloadVideoManager {
    private static DownloadVideoManager instance = null;

    private List<String> levelVideoList;//维护一个list，一个关卡对应一个list,用于显示进度
    private int currentPosition;//当前下到列表的第几个了
    private List<List<String>> allVideoList;
    private int currentAllPosition;//当前下到列表的第几关了
    private int unlockedLevel;
    private int currentLevel;

    private DownloadVideoManager() {
    }

    public static DownloadVideoManager getInstance() {
        if (instance == null) {
            syncInit();
        }
        return instance;
    }

    private static synchronized void syncInit() {
        if (instance == null) {
            instance = new DownloadVideoManager();
        }
    }

    public void setLevelVideoList(List<List<String>> allVideoList) {
        this.allVideoList = allVideoList;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void startDownload(Context context) {
        if (allVideoList == null) {
            return;
        }
        if (currentAllPosition < allVideoList.size()) {
            levelVideoList = allVideoList.get(currentAllPosition);
        }
        if (levelVideoList == null) {
            currentAllPosition++;
            startDownload(context);
            return;
        }
        String url = getNextDownloadUrl(context);
        if (url != null) {
            DownloadHandler.download(url, CacheStoreUtil.getCacheDir(context).getAbsolutePath() + "/" + getFileName(url));
        }
    }

    public void setCurrentAllPosition(int currentAllPosition) {
        this.currentAllPosition = currentAllPosition;
    }

    public int getCurrentAllPosition() {
        return currentAllPosition;
    }

    //设置已解锁的关卡，只下载已解锁关卡的视频
    public void setUnlockedLevel(int unlockedLevel) {
        this.unlockedLevel = unlockedLevel;
    }

    public int getUnlockedLevel() {
        return unlockedLevel;
    }

    //重置下载管理器状态
    public void resetAll() {
        levelVideoList = null;
        currentPosition = 0;
        allVideoList = null;
        currentAllPosition = 0;
        unlockedLevel = 0;
        currentLevel = 0;
    }

    //获得下一个应该下的文件url
    private String getNextDownloadUrl(Context context) {
        for (int i = 0; i < levelVideoList.size(); i++) {
            if (!fileIsExists(CacheStoreUtil.getCacheDir(context).getAbsolutePath() + "/" + getFileName(levelVideoList.get(i)))) {
                SharedPreferencesUtil.setIsDownloading(true);
                currentPosition = i;
                return levelVideoList.get(currentPosition);
            } else {
                continue;
            }
        }
        if (currentAllPosition < allVideoList.size() - 1) {
            currentAllPosition++;
            //Log.d("kkkkkkkk", "currentAllPosition --> " + currentAllPosition + " currentLevel --> " + currentLevel);
            if (currentAllPosition <= currentLevel + 1) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DownloadVideoReceiver.DOWNLOAD_LEVEL_ALL_VIDEO_FINISH_ACTION));
                startDownload(context);
            }
        }
        return null;
    }

    public List<String> getLevelVideoList() {
        return levelVideoList;
    }

    public List<List<String>> getAllVideoList() {
        return allVideoList;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public String getCurrentDownloadingFileName(){
        if(levelVideoList!=null){
            return levelVideoList.get(currentPosition);
        }
        return null;
    }

    //获得下载进度，这里只是根据文件获得粗略进度，下载进度需要在广播中继续精细计算
    public int getDownloadProgress() {
        if (levelVideoList == null) {
            return 0;
        }
        return (int) (((float) currentPosition / levelVideoList.size()) * 100);
    }

    //取URL的最后的文件名为下载的文件名
    private String getFileName(String url) {
        if (url != null) {
            String[] strs = url.split("/");
            return strs[strs.length - 1];
        }
        return null;
    }

    public boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }


}
