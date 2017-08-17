package com.ml.yx.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.ml.yx.R;

/**
 * Created by xunwang on 16/4/16.
 */
public class AudioPlayManager {
    private static AudioPlayManager instance = null;
    private Context mContext;
    private MediaPlayer mp;

    private AudioPlayManager() {
    }

    public static AudioPlayManager getInstance() {
        if (instance == null) {
            syncInit();
        }
        return instance;
    }

    private static synchronized void syncInit() {
        if (instance == null) {
            instance = new AudioPlayManager();
        }
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public void playSound() {
        stopSound();
        try {
            mp = MediaPlayer.create(mContext, R.raw.bg_mp3);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setLooping(true);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
            stopSound();
        }
    }

    public void stopSound() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }
    }

    public void pauseSound() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.pause();
            }
        }
    }

    public void replaySound() {
        if (mp != null) {
            if (!mp.isPlaying()) {
                mp.start();
            }
        }
    }
}
