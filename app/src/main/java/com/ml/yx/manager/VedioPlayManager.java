package com.ml.yx.manager;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.MediaController;

import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.StringUtil;
import com.ml.yx.views.TextureVideoView;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 关卡播放视屏manager
 * Created by wangxun on 16/4/11.
 */
public class VedioPlayManager implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static VedioPlayManager instance = null;
    private MediaController mMediaController;
    private TextureVideoView videoView;
    private Context context;

    //维护一个map，里面是一个关卡的所有视频，大约10多个，每个小节大概2个左右
    //value值代表播放MODE，暂定0为播放一次跳下一段，1为循环播放，2为播放一次定格
    //map的顺序为播放顺序
    private Map<String, Integer> videoMap = new LinkedHashMap<>();
    private Iterator iter;
    private String currentUri;
    private int currentMode;
    private VideoPlayListener videoPlayListener;
    private int currentIndex; //播放到map的第几个了

    private VedioPlayManager() {
    }

    public static VedioPlayManager getInstance() {
        if (instance == null) {
            syncInit();
        }
        return instance;
    }

    private static synchronized void syncInit() {
        if (instance == null) {
            instance = new VedioPlayManager();
        }
    }

    public void init(Activity context, TextureVideoView videoView) {
        this.videoView = videoView;
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnPreparedListener(this);
        mMediaController = new MediaController(context);
        this.context = context;
        //videoView.setMediaController(mMediaController);
    }

    public void startPlay() {
        //BBLog.d("kkkkkkkk", "startPlay currentUri --> " + currentUri);
//        if(!fileIsExists(currentUri)){
//            APPUtil.showToast(context, "某些未知原因视频未下载成功，请重启APP");
//            return;
//        }
        if (StringUtil.isNotBlank(currentUri)) {
            videoView.setVideoURI(Uri.parse(currentUri));
            videoView.start();
            if (videoPlayListener != null) {
                videoPlayListener.onPlayStart();
            }
        }
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

    public boolean startNextPlay() {
        if (nextUriAndMode()) {
            videoPlayListener.onPlayNext();
            startPlay();
            return true;
        } else {
            videoPlayListener.onPlayDone();
            return false;
        }
    }

    public void setUpVedioMap(Map<String, Integer> videoMap) {
        //Log.d("kkkkkkkk", "videoMap size -->" + videoMap.size());
        this.videoMap = videoMap;
        currentIndex = 0;
        iter = videoMap.entrySet().iterator();
        nextUriAndMode();
    }

    //获得下一断视频的URI和播放mode
    private boolean nextUriAndMode() {
        if (iter != null && iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            currentUri = (String) entry.getKey();
            currentMode = (int) entry.getValue();
            currentIndex++;
            return true;
        }
        return false;
    }

    public String getCurrentUri() {
        return currentUri;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Log.d("kkkkkkkk", "onCompletion currentMode --> " + currentMode);
        if (currentMode == -1 || context == null) {
            APPUtil.showToast(context, "视频文件名错误");
            return;
        }
        if (videoPlayListener != null) {
            videoPlayListener.onPlayEnd();
        }
        if (currentMode == 0) {
            if (nextUriAndMode()) {
                if (videoPlayListener != null) {
                    videoPlayListener.onPlayNext();
                }
                startPlay();
            } else {

            }
        } else if (currentMode == 1) {
            if (videoPlayListener != null) {
                videoPlayListener.onPlayOrigin();
            }
            //startPlay();
        } else if (currentMode == 2) {

        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setVolume(0f, 0f);
        if (currentMode == 1) {
            mp.setLooping(true);
        }
    }

    public interface VideoPlayListener {
        void onPlayNext(); //视频自动跳到下一段时回调

        void onPlayOrigin(); //视频循环播放时回调

        void onPlayDone(); //当前小节所有视频播放完毕回调

        void onPlayEnd(); //当前视频播放完毕回调

        void onPlayStart(); //当前视频开始播放
    }

    public void setVideoPlayListener(VideoPlayListener videoPlayListener) {
        this.videoPlayListener = videoPlayListener;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //BBLog.d("kkkkkkkk", "onError what --> " + what + " extra --> " + extra);
        return true;
    }

}
