package com.ml.yx.activity.work;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.CacheStoreUtil;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.comm.StringUtil;
import com.ml.yx.manager.AudioPlayManager;
import com.ml.yx.manager.DownloadVideoManager;
import com.ml.yx.manager.VedioPlayManager;
import com.ml.yx.model.BaseBean;
import com.ml.yx.model.LevelDoneBean;
import com.ml.yx.model.LevelVideoBean;
import com.ml.yx.receiver.DownloadVideoReceiver;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.views.LevelsLayout;
import com.ml.yx.views.RoundProgressBar;
import com.ml.yx.views.TextureVideoView;
import com.ml.yx.views.YouXinDialog;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 训练视频页
 * Created by wangxun on 16/3/30.
 */
public class LevelVideoActivity extends BaseTitleBarActivity implements View.OnClickListener, VedioPlayManager.VideoPlayListener {
    private final String tag = LevelSelectActivity.class.getSimpleName();
    public static final String TASTE_TIME_KEY = "taste_time_key";
    public static final String LEVEL_ID_KEY = "level_id_key";
    public static final String LOOK_SIDE_KEY = "look_side_key";
    public static final String TIP_CAL_KEY = "tip_cal_key";
    public static final String DAY_KEY = "day_key";

    private LevelsLayout levelsView;
    private RoundProgressBar roundProgressBar, downloadProgressBar;
    private RelativeLayout levelFinishBtn;
    private TextView levelFinishTv;
    private TextureVideoView videoView;
    private ImageView playAudioBtn;
    private TextView levelTipTv, actionNameTv, repeatTv;
    private ImageView actionNameDetailIv;
    private RelativeLayout actionDetailLayout;
    private ImageView actionDetailCloseIV;
    private TextView actionDetailNameTv, targetJiRouTipTv, actionYaoLingTipTv, noticeShiXiangTipTv;
    private ImageView levelFinishIv;
    private TextView levelNextTv;

    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mReceiver;
    private IntentFilter filter;

    private int maxLevel = 6;
    private int currentLevel = 6;//反的
    private int currentProgress;//休息圆形进度条进度
    private boolean isPlayingAudio;
    private long startTime, endTime;
    private long tasteTime; //在这个页面停留的时间就是用户当前关卡消耗的时间
    private int currentLevelProgress; //一个小灯里面包含N个视频，标识播放到哪一个了
    private LevelVideoBean levelVideoBean;
    private boolean isStretch; //标识是否进入拉伸状态
    private List<Integer> preActionIndex = new ArrayList<>();//有几个准备动作，需要记录下下标，在下一个回调里currentLevelProgress不能自增

    private int pageStatus;//页面目前状态分为：0为可以正常播放的状态，1为正在下载还需等待的状态，2为小节之间休息的状态
    private Map<String, Integer> videoMap = new LinkedHashMap<>();//正常动作的播放列表
    private Map<String, Integer> videoStretchMap = new LinkedHashMap<>();//拉伸动作的播放列表
    private String levelId;
    private int day;
    private int levelIndex, allLevelIndex;
    private boolean isDownloaded;
    private int savePosition;//视频播放时home键保存进度，回来的时候按进度播放，不然会黑屏
    private int stretchDownTime;
    private boolean isRest = false, isShouldPlayNext;//同一个小灯判断是否进入休息界面
    private long startTimeSt; //统计用
    private boolean isShouldDone; //产品新加需求，最后一个拉伸动作倒计时结束不能直接进入完成页面
    private boolean isStretchDown; //拉伸动作进入倒计时的时候才可以收到切换播放下一个视频

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();
        initData();
    }

    private void initView() {
        setTitleBarTitleText("优形");
        levelsView = (LevelsLayout) findViewById(R.id.levels_layout);
        videoView = (TextureVideoView) findViewById(R.id.level_video_view);

        initLevelsView();

        roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        downloadProgressBar = (RoundProgressBar) findViewById(R.id.downloadRoundProgressBar);
        levelFinishBtn = (RelativeLayout) findViewById(R.id.level_finish_btn);
        levelFinishIv = (ImageView) findViewById(R.id.level_finish_done_iv);
        levelFinishTv = (TextView) findViewById(R.id.level_finish_done_tv);
        levelFinishBtn.setOnClickListener(this);
        levelTipTv = (TextView) findViewById(R.id.level_tip_tv);
        actionNameTv = (TextView) findViewById(R.id.action_name_tv);
        repeatTv = (TextView) findViewById(R.id.repeat_tv);
        actionNameDetailIv = (ImageView) findViewById(R.id.action_name_detail_iv);
        actionNameDetailIv.setOnClickListener(this);

        playAudioBtn = (ImageView) findViewById(R.id.level_bg_audio);
        playAudioBtn.setOnClickListener(this);

        actionDetailLayout = (RelativeLayout) findViewById(R.id.action_detail_layout);
        actionDetailLayout.setOnClickListener(this);
        actionDetailCloseIV = (ImageView) findViewById(R.id.action_detail_close);
        actionDetailCloseIV.setOnClickListener(this);
        actionDetailNameTv = (TextView) findViewById(R.id.action_detail_name_tv);
        targetJiRouTipTv = (TextView) findViewById(R.id.target_jirou_tip);
        actionYaoLingTipTv = (TextView) findViewById(R.id.action_yaoling_tip);
        noticeShiXiangTipTv = (TextView) findViewById(R.id.notice_shixiang_tip);
        levelNextTv = (TextView) findViewById(R.id.level_next_tv);

        VedioPlayManager.getInstance().setVideoPlayListener(this);

        setPageStatus(0);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        filter = new IntentFilter();
        filter.addAction(DownloadVideoReceiver.DOWNLOAD_LEVEL_VIDEO_FINISH_ACTION);
        filter.addAction(DownloadVideoReceiver.DOWNLOAD_LEVEL_ALL_VIDEO_FINISH_ACTION);
        filter.addAction(DownloadVideoReceiver.DOWNLOAD_ALL_VIDEO_FINISH_ACTION);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadVideoReceiver.DOWNLOAD_LEVEL_VIDEO_FINISH_ACTION.equals(intent.getAction())) {
                    //每个视频下载完成收到这个广播，收到当前关卡视频的index
                    levelIndex = intent.getIntExtra(DownloadVideoReceiver.DOWNLOAD_LEVEL_VIDEO_PROGRESS_KEY, 0);
                    //BBLog.d("kkkkkkkk", "levelIndex --> " + levelIndex);
                    if (allLevelIndex + 1 == day) {
                        downloadProgressBar.setProgress(levelIndex);
                    }


                } else if (DownloadVideoReceiver.DOWNLOAD_LEVEL_ALL_VIDEO_FINISH_ACTION.equals(intent.getAction())) {
                    //关卡下载完成收到这个广播，收到关卡的index
                    allLevelIndex = DownloadVideoManager.getInstance().getCurrentAllPosition();
                    //BBLog.d("kkkkkkkk", "allLevelIndex --> " + allLevelIndex + " DownloadVideoManager.getInstance().getCurrentAllPosition() --> " + DownloadVideoManager.getInstance().getCurrentAllPosition());
                    if (allLevelIndex + 1 > day && !isDownloaded) {
                        setPageStatus(0);
                        isDownloaded = true;
                        initData();
                    }

                } else if (DownloadVideoReceiver.DOWNLOAD_ALL_VIDEO_FINISH_ACTION.equals(intent.getAction())) {
                    //BBLog.d("kkkkkkkk", " DOWNLOAD_ALL_VIDEO_FINISH_ACTION ");
                    if (!isDownloaded) {
                        setPageStatus(0);
                        isDownloaded = true;
                        initData();
                    }
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {//监听HOME键按下
                    addActionEvent(ActionWebService.PARAMS_V6, VedioPlayManager.getInstance().getCurrentUri());
                }
            }
        };
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.seekTo(savePosition);
            videoView.start();
        }
        AudioPlayManager.getInstance().replaySound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            savePosition = videoView.getCurrentPosition();
            videoView.stopPlayback();
        }
        AudioPlayManager.getInstance().pauseSound();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        levelId = intent.getStringExtra(LEVEL_ID_KEY);
        day = intent.getIntExtra(LevelSelectActivity.LEVEL_DAY, 0);
        setTitleBarTitleText("第" + day + "天");
        int lookSide = intent.getIntExtra(LOOK_SIDE_KEY, 0);
        String lookSideString;
        if (lookSide == 0) {
            lookSideString = "normal";
        } else {
            lookSideString = "heart";
        }

        if (SharedPreferencesUtil.isDownloading() && day - 1 >= DownloadVideoManager.getInstance().getCurrentAllPosition()) {
            setPageStatus(1);
        } else {
            requestLevelDetail(levelId, lookSideString);
        }

    }

    private void requestLevelDetail(String levelId, String lookSide) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("levelId", levelId);
        params.put("lookSide", lookSide);
        WebRequest request = new WebRequest(WebService.ACCOUNT_LEVEL_DETAIL_URL, params, LevelVideoBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            LevelVideoBean levelVideoBean = (LevelVideoBean) response;
                            updateUIAndData(levelVideoBean);
                        } else {
                            APPUtil.showToast(getApplicationContext(), response.getMsg());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //BBLog call replaced
                        cancelLoadingDialog();
                        APPUtil.showToast(getApplicationContext(), error.getMessage());
                    }

                    @Override
                    public void onStart() {
                        if (isActivityDestroyed()) {
                            return;
                        }
                        showLoadingDialog();
                    }

                });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    private void requestLevelDoneDate(final long tasteTime, final String levelId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("levelId", levelId);
        params.put("time", String.valueOf(tasteTime));
        WebRequest request = new WebRequest(WebService.ACCOUNT_LEVEL_DONE_URL, params, LevelDoneBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            LevelDoneBean levelDoneBean = (LevelDoneBean) response;
                            if (levelDoneBean.isStageFinish()) {
                                Intent intent = new Intent(LevelVideoActivity.this, LevelStageDoneActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(LevelVideoActivity.this, LevelDoneActivity.class);
                                intent.putExtra(TASTE_TIME_KEY, tasteTime);
                                intent.putExtra(TIP_CAL_KEY, levelDoneBean.getTipCal());
                                intent.putExtra(DAY_KEY, day);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            APPUtil.showToast(getApplicationContext(), response.getMsg());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //BBLog call replaced
                        cancelLoadingDialog();
                        APPUtil.showToast(getApplicationContext(), error.getMessage());
                    }

                    @Override
                    public void onStart() {
                        if (isActivityDestroyed()) {
                            return;
                        }
                        showLoadingDialog();
                    }

                });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    private void updateUIAndData(LevelVideoBean levelVideoBean) {
        maxLevel = levelVideoBean.getLight().getRepeatTimes() + 1;
        currentLevel = maxLevel;
        this.levelVideoBean = levelVideoBean;

        initLevelsView();

        levelTipTv.setText("1 / " + levelVideoBean.getLight().getActions().size());
        actionNameTv.setText(levelVideoBean.getLight().getActions().get(0).getTipName());
        repeatTv.setText(levelVideoBean.getLight().getActions().get(0).getTipRepeat());
        actionDetailNameTv.setText(levelVideoBean.getLight().getActions().get(0).getTipName());
        targetJiRouTipTv.setText(levelVideoBean.getLight().getActions().get(0).getTipDetail().getJiqun());
        actionYaoLingTipTv.setText(formatActionDetail(levelVideoBean.getLight().getActions().get(0).getTipDetail().getYaoling()));
        noticeShiXiangTipTv.setText(formatActionDetail(levelVideoBean.getLight().getActions().get(0).getTipDetail().getZhuyi()));

        VedioPlayManager.getInstance().init(this, videoView);
        for (int i = 0; i < levelVideoBean.getLight().getActions().size(); i++) {
            if (StringUtil.isNotBlank(levelVideoBean.getLight().getActions().get(i).getPreVideoUrl())) {
                preActionIndex.add(videoMap.size());
                videoMap.put(getMp4Path(levelVideoBean.getLight().getActions().get(i).getPreVideoUrl()), calculatePlayType(levelVideoBean.getLight().getActions().get(i).getPreVideoUrl()));
            }
            videoMap.put(getMp4Path(levelVideoBean.getLight().getActions().get(i).getVideoUrl()), calculatePlayType(levelVideoBean.getLight().getActions().get(i).getVideoUrl()));
        }

        for (int j = 0; j < levelVideoBean.getLight().getStretch().size(); j++) {
            videoStretchMap.put(getMp4Path(levelVideoBean.getLight().getStretch().get(j).getVideoUrl()), calculatePlayType(levelVideoBean.getLight().getStretch().get(j).getVideoUrl()));
        }

        VedioPlayManager.getInstance().setUpVedioMap(videoMap);
        VedioPlayManager.getInstance().startPlay();

        AudioPlayManager.getInstance().init(getApplicationContext());
        AudioPlayManager.getInstance().playSound();

        isPlayingAudio = true;
        playAudioBtn.setImageResource(R.mipmap.music_on_default);

        startTime = System.currentTimeMillis();
    }

    //根据URL获得用户下载视频路径
    private String getMp4Path(String url) {
        if (url != null) {
            String[] strs = url.split("/");
            String fileName = strs[strs.length - 1];
            return "file://" + CacheStoreUtil.getCacheDir(getApplicationContext()).getAbsolutePath() + "/" + fileName;
        }
        return null;
    }

    //0为可以正常播放的状态，1为正在下载还需等待的状态，2为小节之间休息的状态,3为休息完毕准备进入下一个小灯状态
    private void setPageStatus(int status) {
        switch (status) {
            case 0:
                pageStatus = 0;
                roundProgressBar.setVisibility(View.GONE);
                levelFinishBtn.setVisibility(View.VISIBLE);
                levelFinishBtn.setBackgroundResource(R.drawable.level_finish_btn_shape);
                levelFinishTv.setTextColor(Color.parseColor("#CC9629"));
                levelFinishBtn.setEnabled(true);
                levelFinishIv.setImageResource(R.mipmap.done);
                levelFinishTv.setText("完成");
                downloadProgressBar.setVisibility(View.GONE);
                break;
            case 1:
                pageStatus = 1;
                roundProgressBar.setVisibility(View.GONE);
                levelFinishBtn.setVisibility(View.VISIBLE);
                levelFinishBtn.setBackgroundResource(R.drawable.level_unfinish_btn_shape);
                levelFinishTv.setTextColor(Color.parseColor("#333230"));
                levelFinishBtn.setEnabled(false);
                levelFinishIv.setImageResource(R.mipmap.done_disable);
                levelFinishTv.setText("完成");
                downloadProgressBar.setVisibility(View.VISIBLE);
                downloadProgressBar.setType(1);
                break;
            case 2:
                pageStatus = 2;
                roundProgressBar.setVisibility(View.VISIBLE);
                levelFinishBtn.setVisibility(View.GONE);
                break;
            case 3:
                pageStatus = 3;
                roundProgressBar.setVisibility(View.GONE);
                levelFinishBtn.setVisibility(View.VISIBLE);
                levelFinishBtn.setBackgroundResource(R.drawable.level_btn_bg);
                levelFinishIv.setImageResource(R.mipmap.play);
                levelFinishTv.setTextColor(Color.parseColor("#FFEC1A"));
                levelFinishTv.setText("开始");
                break;
            case 4: //小灯内休息页面
                pageStatus = 4;
                roundProgressBar.setVisibility(View.GONE);
                levelFinishBtn.setVisibility(View.VISIBLE);
                levelFinishBtn.setBackgroundResource(R.drawable.level_btn_bg);
                levelFinishIv.setImageResource(R.mipmap.play);
                levelFinishTv.setTextColor(Color.parseColor("#FFEC1A"));
                levelFinishTv.setText("开始");
                isShouldPlayNext = true;
                break;
            default:
                break;
        }
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://小灯间休息倒计时
                    currentProgress += 5;
                    if (currentProgress >= 100 * 20) {
                        //Log.d("kkkkkkkk", "mHandler isRest --》" + isRest);
                        setRoundprogressStatus(false);
                        if (isRest) {
                            setPageStatus(4);
                        } else {
                            setPageStatus(3);
                        }
                    } else {
                        repeatTv.setText("休息片刻，马上继续");
                        levelNextTv.setVisibility(View.VISIBLE);
                        roundProgressBar.setProgress(currentProgress / 20);
                        mHandler.sendEmptyMessageDelayed(0, 50);
                    }
                    break;
                case 1://拉伸动作倒计时
                    if (stretchDownTime > 0) {
                        repeatTv.setText("拉伸" + stretchDownTime + "秒");
                        stretchDownTime--;
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        isShouldDone = false;
                        stretchDone();
                        //levelFinishBtn.performClick();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void playNextLight() {
        if (levelsView == null || levelVideoBean.getLight() == null || levelVideoBean.getLight().getActions() == null) {
            return;
        }
        currentProgress = 0;
        currentLevelProgress = 0;
        currentLevel--;
        levelsView.removeAllViews();
        initLevelsView();
        levelsView.invalidate();
        levelNextTv.setVisibility(View.GONE);
        if (currentLevel == 1) {//进入拉伸动作
            isStretch = true;
            currentLevelProgress = 0;
        }
        if (!isStretch) {
            VedioPlayManager.getInstance().setUpVedioMap(videoMap);
            VedioPlayManager.getInstance().startPlay();
            resetLightStatus(0);
            levelTipTv.setText((currentLevelProgress + 1) + " / " + levelVideoBean.getLight().getActions().size());
        } else {
            VedioPlayManager.getInstance().setUpVedioMap(videoStretchMap);
            VedioPlayManager.getInstance().startPlay();
            resetLightStatus(0);
            levelTipTv.setText((currentLevelProgress + 1) + " / " + levelVideoBean.getLight().getStretch().size());
        }
        //setPageStatus(0);
        isRest = true;
        setRoundprogressStatus(true);
    }

    //小灯间的强制休息时间，状态的改变,true为休息倒计时状态，false为正常状态
    private void setRoundprogressStatus(boolean flag) {
        if (flag) {
            addActionEvent(ActionWebService.PARAMS_V5, VedioPlayManager.getInstance().getCurrentUri());
            long time = System.currentTimeMillis() - startTimeSt;
            addActionEvent(ActionWebService.PARAMS_V71, String.valueOf(time));
            levelFinishBtn.setVisibility(View.GONE);
            roundProgressBar.setVisibility(View.VISIBLE);
            roundProgressBar.setProgress(currentProgress);
            roundProgressBar.setType(0);
            mHandler.sendEmptyMessageDelayed(0, 20);
        } else {
            levelFinishBtn.setVisibility(View.VISIBLE);
            roundProgressBar.setVisibility(View.GONE);
            mHandler.removeMessages(0);
        }
    }

    private void initLevelsView() {
        levelsView.setType(1);
        levelsView.removeAllViews();
        levelsView.setCurrentLevel(currentLevel);
        for (int i = 1; i < currentLevel; i++) {
            View beforeView = LayoutInflater.from(this).inflate(R.layout.level_live_item_unselect, levelsView, false);
            levelsView.addView(beforeView, 0);
        }

        for (int i = currentLevel; i <= maxLevel; i++) {
            View afterView = LayoutInflater.from(this).inflate(R.layout.level_live_item, levelsView, false);
            levelsView.addView(afterView, 0);
        }
        levelsView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreDraw() {
                levelsView.getViewTreeObserver().removeOnPreDrawListener(this);

                final int width = levelsView.getWidth();
                for (int i = 0; i < levelsView.getChildCount(); i++) {
                    final View child = levelsView.getChildAt(i);
                    child.setId(i);
                    child.setX((width / maxLevel) * i + ((i * width) / (maxLevel * maxLevel)) - (((float) i / (float) maxLevel) * 5));
                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            APPUtil.showToast(getApplicationContext(), "child.getId() --> " + child.getId());
                        }
                    });
                }
                return false;
            }
        });
    }

    private void startNextLight() {
        if (currentLevel > 2) {//普通动作组
            setRoundprogressStatus(true);
        } else if (currentLevel == 2) {//拉伸动作
//            isStretch = true;
//            currentLevelProgress = 0;
            setRoundprogressStatus(true);
        } else {
            if (!isShouldDone) {
                repeatTv.setText("拉伸0秒");
                return;
            }
            endTime = System.currentTimeMillis();
            tasteTime = endTime - startTime;
            requestLevelDoneDate(tasteTime, levelId);
        }
    }

    private void stretchDone() {
        if (currentLevelProgress < videoStretchMap.size() - 1 && isStretch) {
            addActionEvent(ActionWebService.PARAMS_V1, VedioPlayManager.getInstance().getCurrentUri());
            startTimeSt = System.currentTimeMillis();
            mHandler.removeMessages(1);
            VedioPlayManager.getInstance().startNextPlay();
            startStretchDown();
            resetLightStatus(currentLevelProgress);
            levelNextTv.setVisibility(View.GONE);
        } else {
            startNextLight();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.level_finish_btn:
                if (APPUtil.isFastDoubleClick()) {
                    return;
                }
                if (!isStretch) {
                    isRest = false;//同一组的准备动作与训练动作间没有休息状态
                    if (!isShouldPlayNext) {
                        isRest = true;
                        for (int i = 0; i < preActionIndex.size(); i++) {
                            if (VedioPlayManager.getInstance().getCurrentIndex() == preActionIndex.get(i) + 1) {
                                isRest = false;
                            }
                        }
                        if (isRest) {
                            //Log.d("kkkkkkkk", "该进入休息界面了");
                            currentProgress = 0;
                            VedioPlayManager.getInstance().startNextPlay();
                            setRoundprogressStatus(true);
                            return;
                        }
                    } else {
                        isShouldPlayNext = false;
                    }
                }
                //Log.d("kkkkkkkk", " onClick --> isStretch --> " + isStretch + " pageStatus --> " + pageStatus);
                levelFinishBtn.setEnabled(false);
                if (pageStatus == 3) {
                    playNextLight();
                } else if (currentLevelProgress < videoMap.size() - preActionIndex.size() - 1 && !isStretch) {
                    addActionEvent(ActionWebService.PARAMS_V1, VedioPlayManager.getInstance().getCurrentUri());
                    startTimeSt = System.currentTimeMillis();
                    //VedioPlayManager.getInstance().startNextPlay();
                    setPageStatus(0);
                    resetLightStatus(currentLevelProgress);
                    levelNextTv.setVisibility(View.GONE);
                } else if (currentLevelProgress < videoStretchMap.size() - 1 && isStretch) {
                    addActionEvent(ActionWebService.PARAMS_V1, VedioPlayManager.getInstance().getCurrentUri());
                    startTimeSt = System.currentTimeMillis();
                    mHandler.removeMessages(1);
                    if (isStretchDown) {
                        VedioPlayManager.getInstance().startNextPlay();
                    }
                    isStretchDown = true;
                    startStretchDown();
                    setPageStatus(0);
                    resetLightStatus(currentLevelProgress);
                    levelNextTv.setVisibility(View.GONE);
                } else {
                    if (isStretch) {
                        isShouldDone = true;
                        startNextLight();
                    } else {
                        if (pageStatus == 0) {
                            playNextLight();
                        } else {
                            setPageStatus(0);
                            resetLightStatus(currentLevelProgress);
                            levelNextTv.setVisibility(View.GONE);
                            isShouldPlayNext = true;
                        }
                    }
                }
                levelFinishBtn.setEnabled(true);
                break;
            case R.id.level_bg_audio:
                if (isPlayingAudio) {
                    AudioPlayManager.getInstance().stopSound();
                    isPlayingAudio = false;
                    playAudioBtn.setImageResource(R.mipmap.music_off_default);
                    addActionEvent(ActionWebService.PARAMS_V3, "1");
                } else {
                    AudioPlayManager.getInstance().playSound();
                    isPlayingAudio = true;
                    playAudioBtn.setImageResource(R.mipmap.music_on_default);
                    addActionEvent(ActionWebService.PARAMS_V3, "2");
                }
                break;
            case R.id.action_name_detail_iv:
                actionDetailLayout.setVisibility(View.VISIBLE);
                addActionEvent(ActionWebService.PARAMS_V4, "1");
                break;
            case R.id.action_detail_close:
                actionDetailLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        AudioPlayManager.getInstance().stopSound();
    }

    //切换小灯时，重置文案
    private void resetLightStatus(int currentLevelIndex) {
        if (levelVideoBean == null) {
            return;
        }
        if (!isStretch) {
            if (currentLevelProgress < levelVideoBean.getLight().getActions().size()) {
                actionNameTv.setText(levelVideoBean.getLight().getActions().get(currentLevelIndex).getTipName());
                repeatTv.setText(levelVideoBean.getLight().getActions().get(currentLevelIndex).getTipRepeat());
                actionDetailNameTv.setText(levelVideoBean.getLight().getActions().get(currentLevelIndex).getTipName());
                targetJiRouTipTv.setText(levelVideoBean.getLight().getActions().get(currentLevelIndex).getTipDetail().getJiqun());
                actionYaoLingTipTv.setText(formatActionDetail(levelVideoBean.getLight().getActions().get(currentLevelIndex).getTipDetail().getYaoling()));
                noticeShiXiangTipTv.setText(formatActionDetail(levelVideoBean.getLight().getActions().get(currentLevelIndex).getTipDetail().getZhuyi()));
            }
        } else {
            if (currentLevelProgress < levelVideoBean.getLight().getStretch().size()) {
                actionNameTv.setText(levelVideoBean.getLight().getStretch().get(currentLevelIndex).getTipName());
                //repeatTv.setText(levelVideoBean.getLight().getStretch().get(0).getTipRepeat());
                actionDetailNameTv.setText(levelVideoBean.getLight().getStretch().get(currentLevelIndex).getTipName());
                targetJiRouTipTv.setText(levelVideoBean.getLight().getStretch().get(currentLevelIndex).getTipDetail().getJiqun());
                actionYaoLingTipTv.setText(formatActionDetail(levelVideoBean.getLight().getStretch().get(currentLevelIndex).getTipDetail().getYaoling()));
                noticeShiXiangTipTv.setText(formatActionDetail(levelVideoBean.getLight().getStretch().get(currentLevelIndex).getTipDetail().getZhuyi()));
            }
        }
    }

    //进入拉伸阶段时，开始倒计时
    private void startStretchDown() {
        stretchDownTime = 20;
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onPlayNext() {
        //BBLog.d("kkkkkkkk", "preActionIndex.size() --> " + preActionIndex.size());
        for (int i = 0; i < preActionIndex.size(); i++) {
            //Log.d("kkkkkkkk", "preActionIndex.get(" + i + ") --> " + preActionIndex.get(i) + " VedioPlayManager.getInstance().getCurrentIndex() --> " + VedioPlayManager.getInstance().getCurrentIndex());
            //准备动作和训练动作共享一套文案，所以到准备动作到跳到训练动作时，不切换文案
            if (VedioPlayManager.getInstance().getCurrentIndex() == preActionIndex.get(i) + 2 && !isStretch) {
                return;
            }
            if (VedioPlayManager.getInstance().getCurrentIndex() != preActionIndex.get(i) + 2) {
                isRest = true;
            }
        }

        if (levelVideoBean == null) {
            return;
        }

        currentLevelProgress++;
        //BBLog.d("kkkkkkkk", "currentLevelProgress --> " + currentLevelProgress);
        if (!isStretch) {
            if (currentLevelProgress < levelVideoBean.getLight().getActions().size()) {
                levelTipTv.setText((currentLevelProgress + 1) + " / " + levelVideoBean.getLight().getActions().size());
                actionNameTv.setText(levelVideoBean.getLight().getActions().get(currentLevelProgress).getTipName());
                repeatTv.setText(levelVideoBean.getLight().getActions().get(currentLevelProgress).getTipRepeat());
                actionDetailNameTv.setText(levelVideoBean.getLight().getActions().get(currentLevelProgress).getTipName());
                targetJiRouTipTv.setText(levelVideoBean.getLight().getActions().get(currentLevelProgress).getTipDetail().getJiqun());
                actionYaoLingTipTv.setText(formatActionDetail(levelVideoBean.getLight().getActions().get(currentLevelProgress).getTipDetail().getYaoling()));
                noticeShiXiangTipTv.setText(formatActionDetail(levelVideoBean.getLight().getActions().get(currentLevelProgress).getTipDetail().getZhuyi()));
            }
        } else {
            if (currentLevelProgress < levelVideoBean.getLight().getStretch().size()) {
                levelTipTv.setText((currentLevelProgress + 1) + " / " + levelVideoBean.getLight().getStretch().size());
                actionNameTv.setText(levelVideoBean.getLight().getStretch().get(currentLevelProgress).getTipName());
                //repeatTv.setText(levelVideoBean.getLight().getStretch().get(currentLevelProgress).getTipRepeat());
                actionDetailNameTv.setText(levelVideoBean.getLight().getStretch().get(currentLevelProgress).getTipName());
                targetJiRouTipTv.setText(levelVideoBean.getLight().getStretch().get(currentLevelProgress).getTipDetail().getJiqun());
                actionYaoLingTipTv.setText(formatActionDetail(levelVideoBean.getLight().getStretch().get(currentLevelProgress).getTipDetail().getYaoling()));
                noticeShiXiangTipTv.setText(formatActionDetail(levelVideoBean.getLight().getStretch().get(currentLevelProgress).getTipDetail().getZhuyi()));
            }
        }
    }

    @Override
    public void onPlayOrigin() {

    }

    @Override
    public void onPlayDone() {

    }

    @Override
    public void onPlayEnd() {
        if (isStretch) {
            levelFinishBtn.setEnabled(true);
        }
    }

    @Override
    public void onPlayStart() {
        if (isStretch) {
            levelFinishBtn.setEnabled(false);
        }
    }

    //1身体自然站立 2双腿跳跃下蹲 中间的空格变成换行
    private String formatActionDetail(String string) {
        if (string == null) {
            return "";
        } else {
            return string.replace(" ", "\n");
        }
    }

    //根据文件名,暂定0为播放一次，1为循环播放
    //文件名规则需要看小熊给的图
    private int calculatePlayType(String fileUrl) {
        if (fileUrl != null) {
            String[] strs = fileUrl.split("/");
            String fileName = strs[strs.length - 1];
            if (fileName.endsWith("01.mp4")) {
                return 0;
            } else if (fileName.endsWith("02.mp4")) {
                return 1;
            } else if (fileName.endsWith("04.mp4")) {
                return 2;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void rightListener(View v) {
        super.rightListener(v);
        addActionEvent(ActionWebService.PARAMS_V2, "1");
    }

    @Override
    public void leftListener(View v) {
        addActionEvent(ActionWebService.PARAMS_V7, VedioPlayManager.getInstance().getCurrentUri());
        showExitDialog();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void showExitDialog() {
        if (!isFinishing()) {
            youXinDialog = new YouXinDialog(this, "", "你今天的懈怠，是情敌明天的笑料");
            youXinDialog.show();
            youXinDialog.setLeftButtonText("坚持到底");
            youXinDialog.setRightButtonText("落荒而逃");
            youXinDialog.setCancelable(false);
            youXinDialog.setRightListener(new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            youXinDialog.setLeftListener(new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_LEVEL_DONE;
    }
}
