/**
 *
 */
package com.ml.yx.utils;

import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Mr.Yang
 */
public class DownloadTask implements Runnable {

    private static final String TAG = DownloadTask.class.getSimpleName();

    private String taskId;
    private String url;
    private String destFile;
    private int curSize;
    private int totalSize;
    private boolean wifiOnly = false;
    private volatile DownloadHandler.DownloadStatus status;

    private DownloadListener listener;

    public interface DownloadListener {

        void onDownload(String taskId, int totalSize, int curSize);

        void onStop(DownloadHandler.DownloadStatus status, String taskId, int totalSize, int curSize);

    }

    public DownloadTask(String taskId, String url, String destFile, int curSize, int totalSize, DownloadListener listener) {
        super();
        this.taskId = taskId;
        this.url = url;
        this.destFile = destFile;
        this.curSize = curSize;
        this.totalSize = totalSize;
        this.listener = listener;
        this.status = DownloadHandler.DownloadStatus.INITIALIZED;
    }

    @Override
    public void run() {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        try {
            File apkFile = new File(destFile);
            File parentFile = apkFile.getParentFile();

            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }

            if (!apkFile.exists()) {
                apkFile.createNewFile();
                curSize = 0;
            }

            URL downUrl = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) downUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(150000);
            if (curSize > 0) {
                conn.setRequestProperty("Range", "bytes=" + curSize + "-");
            }

            conn.connect();

            //BBLog call replaced

            totalSize = curSize + conn.getContentLength();

            is = conn.getInputStream();

            savedFile = new RandomAccessFile(apkFile, "rwd");
            savedFile.setLength(totalSize);
            savedFile.seek(curSize);

            byte buf[] = new byte[1024];
            do {
                if (wifiOnly && DownloadHandler.getNetworkType() == DownloadHandler.NETWORK_STATE_MOBILE) {
                    status = DownloadHandler.DownloadStatus.INTERRUPTED;
                    break;
                }

                int length = is.read(buf);
                if (length <= 0) {
                    break;
                }

                savedFile.write(buf, 0, length);

                curSize += length;

                if (listener != null) {
                    listener.onDownload(taskId, totalSize, curSize);
                }

            } while (status == DownloadHandler.DownloadStatus.DOWNLOADING);

            if (listener != null) {
                listener.onStop(curSize < totalSize ? status : DownloadHandler.DownloadStatus.COMPLETED, taskId, totalSize, curSize);
                //BBLog call replaced
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            if (listener != null) {
                status = DownloadHandler.DownloadStatus.ERROR;
                int netWorkType = DownloadHandler.getNetworkType();
                if (wifiOnly && netWorkType == DownloadHandler.NETWORK_STATE_MOBILE) {
                    status = DownloadHandler.DownloadStatus.INTERRUPTED;
                }
                if (netWorkType == DownloadHandler.NETWORK_STATE_NO_CONNECTION) {
                    status = DownloadHandler.DownloadStatus.NO_NETWORK;
                } else {
                    status = DownloadHandler.DownloadStatus.DISCONNECTED;
                }
                //BBLog call replaced
                listener.onStop(status, taskId, totalSize, curSize);
            }
        } finally {
            APPUtil.closeObject(savedFile);
            APPUtil.closeObject(is);
        }
    }

    public boolean isPaused() {
        return status == DownloadHandler.DownloadStatus.PAUSED;
    }

    public void setPaused() {
        this.status = DownloadHandler.DownloadStatus.PAUSED;
    }

    public DownloadHandler.DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadHandler.DownloadStatus status) {
        this.status = status;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDestFile() {
        return destFile;
    }

    public void setDestFile(String destFile) {
        this.destFile = destFile;
    }

    public DownloadListener getListener() {
        return listener;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    public boolean isWifiOnly() {
        return wifiOnly;
    }

    public void setWifiOnly(boolean wifiOnly) {
        this.wifiOnly = wifiOnly;
    }

    public int getCurSize() {
        return curSize;
    }

    public void setCurSize(int curSize) {
        this.curSize = curSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
