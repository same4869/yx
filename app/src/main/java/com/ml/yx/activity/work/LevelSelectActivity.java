package com.ml.yx.activity.work;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.activity.CommonInvokerActivity;
import com.ml.yx.activity.base.BaseMainActivity;
import com.ml.yx.activity.base.BaseWebActivity;
import com.ml.yx.activity.base.LoginActivity;
import com.ml.yx.activity.user.UserActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.comm.StringUtil;
import com.ml.yx.manager.DownloadVideoManager;
import com.ml.yx.model.BaseBean;
import com.ml.yx.model.LevelSelectBean;
import com.ml.yx.model.UserBean;
import com.ml.yx.service.HeartBeatService;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.views.LevelsLayout;
import com.ml.yx.views.YouXinTitleBar;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LevelSelectActivity extends BaseTitleBarActivity implements LevelsLayout.LevelsInterface, YouXinTitleBar.YouXinTitleBarListener, View.OnClickListener {
    private final String tag = LevelSelectActivity.class.getSimpleName();
    public static final String LEVEL_ID_KEY = "level_id_key";
    public static final String LEVEL_DAY = "level_day";
    public static final String NORMAL_IMG_KEY = "normal_img_key";
    public static final String HEART_IMG_KEY = "heart_img_key";
    public static final String HEART_LOOK_UNLOCK_KEY = "heart_look_unlock_key";
    public static final String HEART_LOOK_UNLOCK_DAY_KEY = "heart_look_unlock_day_key";
    public static final String COME_IN_NEW_STAGE_KEY = "come_in_new_stage_key";//标识从阶段完成页面过来，是否需要制定新阶段
    public static final String COME_IN_DONE_KEY = "come_in_done_key";//标识从关卡完成页面过来，是否需要刷新列表
    public static final String LOCK_STATUS = "lock_status";
    public static final String ADD_NEW_STAGE_KEY = "add_new_stage_key"; //添加新阶段
    public static final int USER_ACTIVITY = 1;

    public static final String USER_LOGOUT = "user_logout";

    public static boolean inited = false;// 当前页面是否已经创建标记

    private LevelsLayout levelsView;
    private ScrollView levelsScroll;
    private RelativeLayout levelBannerLayout;
    private WebView levelBannerWebview;
    private ImageView levelBannerClose;
    private TextView dialogView;

    private LevelSelectBean levelSelectBean;
    private int maxLevel = 7;
    private int currentLevel = 6;
    private float scrollY;
    private List<List<String>> allVideoList = new ArrayList<>();
    private boolean isShouldNewStage; //标识是否显示添加新阶段状态
    private Boolean is2CallBack = false;// 是否双击退出

    private void userLogout() {
        SharedPreferencesUtil.setUserToken(null);
        Intent loginIntent = new Intent(LevelSelectActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        boolean userLogout = getIntent().getBooleanExtra(USER_LOGOUT, false);
        if (userLogout) {
            userLogout();
            return;
        }

        sendBroadcast(new Intent(Constants.RECEIVER_START_WORK));
        startService(new Intent(this, HeartBeatService.class));

        initView();
        initData();
    }

    private void initView() {
        levelsView = (LevelsLayout) findViewById(R.id.levels_layout);
        levelsScroll = (ScrollView) findViewById(R.id.levels_scroll);
        youXinTitleBar.getLeftView().setImageResource(R.mipmap.me);
        setTitleBarTitleText("U-thin");
        levelBannerLayout = (RelativeLayout) findViewById(R.id.level_banner_layout);
        levelBannerWebview = (WebView) findViewById(R.id.level_banner_webview);
        levelBannerClose = (ImageView) findViewById(R.id.level_banner_close);
        dialogView = (TextView) findViewById(R.id.dialog_head_text);

        levelBannerWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        levelsView.setLevelsInterface(this);
        levelBannerClose.setOnClickListener(this);

        setDialogTextTitle("有问题可以来问我");
        nextPage();
    }

    private void initData() {
        requestLevelList();
        requestUserDetail();
    }


    @Override
    public void onLevelClick(int i) {
        if (isShouldNewStage && i > levelSelectBean.getLevels().size()) {
            //APPUtil.showToast(getApplicationContext(), "添加新阶段");
            Intent intent = new Intent(LevelSelectActivity.this, BaseMainActivity.class);
            intent.putExtra(BaseMainActivity.TARGET_PAGE, BaseMainActivity.PAGE_SELECT_TARGET);
            SharedPreferencesUtil.setAddNewLevelType(2);
            startActivity(intent);
            return;
        }
        if (levelSelectBean.getLevels().get(i - 1).getLockStatus() == 2 && StringUtil.isNotBlank(levelSelectBean.getLevels().get(i - 1).getForkUrl())) {
            Intent intent = new Intent(LevelSelectActivity.this, BaseWebActivity.class);
            intent.putExtra(BaseWebActivity.WEB_URL_KEY, levelSelectBean.getLevels().get(i - 1).getForkUrl());
            startActivity(intent);
            addActionEvent(ActionWebService.PARAMS_V4, String.valueOf(levelSelectBean.getLevels().get(i).getLevelId()));
            return;
        }
        if (levelSelectBean.getLevels().get(i - 1).getLockStatus() == 2 && StringUtil.isBlank(levelSelectBean.getLevels().get(i - 1).getForkUrl())) {
            APPUtil.showToast(getApplicationContext(), "今天为身体恢复期,好好休息吧");
            return;
        }

        if (i == currentLevel + 1) {
            addActionEvent(ActionWebService.PARAMS_V1, String.valueOf(levelSelectBean.getLevels().get(i - 1).getLevelId()));
        } else {
            addActionEvent(ActionWebService.PARAMS_V2, String.valueOf(levelSelectBean.getLevels().get(i - 1).getLevelId()));
        }

//        if (i <= currentLevel + 1) {
        Intent intent = new Intent(LevelSelectActivity.this, PreLevelLookActivity.class);
        intent.putExtra(LEVEL_ID_KEY, String.valueOf(levelSelectBean.getLevels().get(i - 1).getLevelId()));
        intent.putExtra(LEVEL_DAY, levelSelectBean.getLevels().get(i - 1).getDay());
        intent.putExtra(NORMAL_IMG_KEY, levelSelectBean.getNormalImg());
        intent.putExtra(HEART_IMG_KEY, levelSelectBean.getHeartImg());
        intent.putExtra(HEART_LOOK_UNLOCK_KEY, levelSelectBean.getLevels().get(i - 1).isHeartLookUnLocked());
        intent.putExtra(HEART_LOOK_UNLOCK_DAY_KEY, levelSelectBean.getLevels().get(i - 1).getHeartUnLockedDay());
        int status;//为0为解锁关卡，1为明天就解锁的关卡，2为明天以后才解锁的关卡，下个页面会用到
        if (i <= currentLevel + 1) {
            status = 0;
        } else if (i == currentLevel + 2) {
            status = 1;
        } else {
            status = 2;
        }
        intent.putExtra(LOCK_STATUS, status);
        startActivity(intent);
//        }
    }

    @Override
    public void onForkClick(int i) {
//        if (i <= currentLevel + 1) {
        Intent intent = new Intent(LevelSelectActivity.this, BaseWebActivity.class);
        intent.putExtra(BaseWebActivity.WEB_URL_KEY, levelSelectBean.getLevels().get(i - 1).getForkUrl());
        startActivity(intent);
        addActionEvent(ActionWebService.PARAMS_V4, String.valueOf(levelSelectBean.getLevels().get(i - 1).getLevelId()));
        //BBLog.d("kkkkkkkk", "levelSelectBean.getLevels().get(i - 1).getForkUrl() --> " + levelSelectBean.getLevels().get(i - 1).getForkUrl());
//        }
    }

    @Override
    public boolean isForkLeft(int i) {
        if (i % 7 < 4) {
            return false;
        } else {
            return true;
        }
    }

    //type区分小叉子的样式，0为已过关卡，1为当前关卡，2为未过关卡
    private LinearLayout addAndCalculateForkView(View levelView, int id, int type, final String forkUrl) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ImageView forkView = new ImageView(this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        param.setMargins(APPUtil.dpToPx(getApplicationContext(), 10), APPUtil.dpToPx(getApplicationContext(), 25), APPUtil.dpToPx(getApplicationContext(), 10), APPUtil.dpToPx(getApplicationContext(), 25));
        if (type == 0) {
            forkView.setImageDrawable(getResources().getDrawable(R.mipmap.recipe_3));
        } else if (type == 1) {
            forkView.setImageDrawable(getResources().getDrawable(R.mipmap.recipe_2));
        } else if (type == 2) {
            forkView.setImageDrawable(getResources().getDrawable(R.mipmap.recipe_1));
        }
        if (id % 7 < 3 || id % 7 == 6) {
            layout.addView(levelView, param);
            if (StringUtil.isBlank(forkUrl)) {
                forkView.setVisibility(View.INVISIBLE);
            }
            layout.addView(forkView, param);
        } else {
            if (StringUtil.isBlank(forkUrl)) {
                forkView.setVisibility(View.INVISIBLE);
            }
            layout.addView(forkView, param);
            layout.addView(levelView, param);
        }

        return layout;
    }

    private int calculateChildPosition(int id) {
        switch (id % 7) {
            case 0:
                return APPUtil.dpToPx(getApplicationContext(), 0);
            case 1:
                return APPUtil.dpToPx(getApplicationContext(), -65);
            case 2:
                return APPUtil.dpToPx(getApplicationContext(), -75);
            case 3:
                return APPUtil.dpToPx(getApplicationContext(), -45);
            case 4:
                return APPUtil.dpToPx(getApplicationContext(), 40);
            case 5:
                return APPUtil.dpToPx(getApplicationContext(), 80);
            case 6:
                return APPUtil.dpToPx(getApplicationContext(), 60);
            default:
                return 0;
        }
    }

    @Override
    public void rightListener(View v) {
        super.rightListener(v);
        addActionEvent(ActionWebService.PARAMS_V5, "1");
    }

    @Override
    public void leftListener(View v) {
        Intent intent = new Intent(LevelSelectActivity.this, UserActivity.class);
        startActivityForResult(intent, USER_ACTIVITY);
        addActionEvent(ActionWebService.PARAMS_V6, "1");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.level_banner_close:
                levelBannerLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void updateUIAndData(LevelSelectBean levelSelectBean) {
        if (levelSelectBean == null || levelSelectBean.getLevels() == null) {
            return;
        }

        this.levelSelectBean = levelSelectBean;
        DownloadVideoManager.getInstance().resetAll();

        DownloadVideoManager.getInstance().setUnlockedLevel(levelSelectBean.getCurrentLevel() + 1);

        if (levelSelectBean.isBannerOpen()) {
            levelBannerLayout.setVisibility(View.VISIBLE);
            levelBannerWebview.loadUrl(levelSelectBean.getBannerUrl());
        }
        currentLevel = levelSelectBean.getCurrentLevel();
        maxLevel = levelSelectBean.getLevels().size();

        levelsView.removeAllViews();
        if (isShouldNewStage) {//添加新阶段UI逻辑
            currentLevel++;
            maxLevel++;
        }
        levelsView.setCurrentLevel(currentLevel);

        for (int i = 0; i < currentLevel; i++) {
            View beforeView = LayoutInflater.from(this).inflate(R.layout.item_level, levelsView, false);
            ((TextView) beforeView.findViewById(R.id.level)).setText(String.valueOf(levelSelectBean.getLevels().get(i).getDay()));
            if (levelSelectBean.getLevels().get(i).getLockStatus() == 2) {
                beforeView.findViewById(R.id.level_day).setVisibility(View.GONE);
                ((TextView) beforeView.findViewById(R.id.level)).setText("休息");
            }
            levelsView.addView(addAndCalculateForkView(beforeView, i, 0, levelSelectBean.getLevels().get(i).getForkUrl()), 0);
        }
        if (!isShouldNewStage) {
            View currentView = LayoutInflater.from(this).inflate(R.layout.item_level_current, levelsView, false);
            if (levelSelectBean.getLevels().get(currentLevel).getLockStatus() == 2) {
                currentView.findViewById(R.id.level_day).setVisibility(View.GONE);
                ((TextView) currentView.findViewById(R.id.level)).setText("休息");
            }
            if (currentLevel == maxLevel - 1) {
                currentView.findViewById(R.id.level_day).setVisibility(View.GONE);
                ((TextView) currentView.findViewById(R.id.level)).setText("");
                Drawable drawable = getResources().getDrawable(R.mipmap.crown_default);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                ((TextView) currentView.findViewById(R.id.level)).setCompoundDrawables(drawable, null, null, null);
            }
            ((TextView) currentView.findViewById(R.id.level)).setText(String.valueOf(levelSelectBean.getLevels().get(currentLevel).getDay()));
            levelsView.addView(addAndCalculateForkView(currentView, currentLevel, 1, levelSelectBean.getLevels().get(currentLevel).getForkUrl()), 0);
        }

        for (int i = currentLevel + 1; i < maxLevel; i++) {
            if (i < maxLevel) {
                View afterView = LayoutInflater.from(this).inflate(R.layout.item_level_unreach, levelsView, false);
                ((TextView) afterView.findViewById(R.id.level)).setText(String.valueOf(levelSelectBean.getLevels().get(i).getDay()));
                if (levelSelectBean.getLevels().get(i).getLockStatus() == 2) {
                    afterView.findViewById(R.id.level_day).setVisibility(View.GONE);
                    ((TextView) afterView.findViewById(R.id.level)).setText("休息");
                }
                //最后一关需要显示皇冠
                if (i == maxLevel - 1) {
                    afterView.findViewById(R.id.level_day).setVisibility(View.GONE);
                    ((TextView) afterView.findViewById(R.id.level)).setText("");
                    Drawable drawable = getResources().getDrawable(R.mipmap.crown_disable);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    ((TextView) afterView.findViewById(R.id.level)).setCompoundDrawables(drawable, null, null, null);
                }
                levelsView.addView(addAndCalculateForkView(afterView, i, 2, levelSelectBean.getLevels().get(i).getForkUrl()), 0);
            }
        }

        if (isShouldNewStage) {
            View newStageView = LayoutInflater.from(this).inflate(R.layout.item_level_current, levelsView, false);
            newStageView.findViewById(R.id.level_day).setVisibility(View.GONE);
            ((TextView) newStageView.findViewById(R.id.level)).setText("+");
            levelsView.addView(addAndCalculateForkView(newStageView, currentLevel, 1, ""), 0);
        }

        // 水平随机排列
        levelsView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreDraw() {
                levelsView.getViewTreeObserver().removeOnPreDrawListener(this);

                final int width = levelsView.getWidth();
                for (int i = 0; i < levelsView.getChildCount(); i++) {
                    final View child = levelsView.getChildAt(i);
                    child.setId(maxLevel - i);
                    child.setX((float) ((width - child.getWidth()) / 2 + calculateChildPosition(child.getId())));
                    if (i == currentLevel) {
                        scrollY = levelsView.getHeight() - child.getY();
                    }
                }
                return false;
            }
        });

        levelsView.invalidate();

        levelsScroll.post(new Runnable() {
            @Override
            public void run() {
                if (scrollY != 0) {
                    levelsScroll.scrollTo(0, (int) scrollY);
                }
            }
        });

        setupVideoList(levelSelectBean);
        if (allVideoList.size() != 0) {
            startDownLoadVideo(allVideoList);
        }
    }

    private void setupVideoList(LevelSelectBean levelSelectBean) {
        allVideoList.clear();
        for (int i = 0; i < levelSelectBean.getLevels().size(); i++) {
            if (levelSelectBean.getLevels().get(i).isHeartLookUnLocked()) {
                levelSelectBean.getLevels().get(i).getNormalVideoUrl().addAll(levelSelectBean.getLevels().get(i).getHeartVideoUrl());
            }
            allVideoList.add(levelSelectBean.getLevels().get(i).getNormalVideoUrl());
        }
    }

    private void startDownLoadVideo(List<List<String>> allVideoList) {
        DownloadVideoManager.getInstance().setCurrentLevel(currentLevel + 1);
        DownloadVideoManager.getInstance().setLevelVideoList(allVideoList);
        DownloadVideoManager.getInstance().startDownload(getApplicationContext());
    }

    private void requestLevelList() {
        HashMap<String, String> params = new HashMap<String, String>();
        WebRequest request = new WebRequest(WebService.ACCOUNT_LEVEL_LIST_URL, params, LevelSelectBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            LevelSelectBean levelSelectBean = (LevelSelectBean) response;
                            updateUIAndData(levelSelectBean);
                            if (levelSelectBean.getLevels() != null) {
                                SharedPreferencesUtil.setTotleLevel(levelSelectBean.getLevels().size());
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

    private void setDialogTextTitle(String text) {
        dialogView.setText(text);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.head_text_anim);
        final Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.select_level_dialog_anim);
        dialogView.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dialogView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogView.clearAnimation();
                        dialogView.startAnimation(anim2);
                    }
                }, 3000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dialogView.clearAnimation();
                dialogView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isShouldReload = intent.getBooleanExtra(COME_IN_DONE_KEY, false);
            if (isShouldReload) {
                isShouldNewStage = false;
                requestLevelList();
            }
            boolean isNewStage = intent.getBooleanExtra(COME_IN_NEW_STAGE_KEY, false);
            if (isNewStage) {
                shouldAddLevel();
            }

            boolean userLogout = intent.getBooleanExtra(USER_LOGOUT, false);
            if (userLogout) {
                userLogout();
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        inited = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inited = false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //在这个页面点击back，相当于退出了应用，把下载器内容重置
            DownloadVideoManager.getInstance().resetAll();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_LEVEL_SELECT;
    }

    private void nextPage() {
        String schemeUrl = SharedPreferencesUtil.getScheme();
        if (schemeUrl != null) {
            SharedPreferencesUtil.setScheme(null);
            Uri uri = Uri.parse(schemeUrl);
            CommonInvokerActivity.switchActivity(uri, LevelSelectActivity.this);
        }
    }

    //如果从阶段完成界面过来，需要进入这个状态，可以添加新的训练阶段
    private void shouldAddLevel() {
        isShouldNewStage = true;
        requestLevelList();
    }

    /**
     * 获取用户信息
     */
    private void requestUserDetail() {
        HashMap<String, String> params = new HashMap<String, String>();
        WebRequest request = new WebRequest(WebService.ACCOUNT_PROFILE_DETAIL_URL, params, UserBean.class,
                new Response.Listener<UserBean>() {

                    @Override
                    public void onResponse(UserBean response) {
                        if (response.isSuccess()) {
                            SharedPreferencesUtil.setInstructorName(response.getInstructorName());
                            SharedPreferencesUtil.setInstructorId(response.getInstructorId());
                            SharedPreferencesUtil.setUserId(response.getAccountId());
                            youXinTitleBar.setInstructorAvatar(response.getInstructorId());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                    @Override
                    public void onStart() {
                    }

                });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //用户在用户界面修改过课程，返回这个页面时需要重新刷新
        if (requestCode == USER_ACTIVITY && resultCode == RESULT_OK) {
            requestLevelList();
            requestUserDetail();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!is2CallBack) {
                is2CallBack = true;
                APPUtil.showToast(getApplicationContext(), "再按一次退出优形");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        is2CallBack = false;
                    }
                }, 2500);

            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
        return true;
    }
}
