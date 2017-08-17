package com.ml.yx.activity.work;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.utils.BitmapBlurHelper;
import com.ml.yx.views.YouXinTitleBar;
import com.ml.yx.web.WebDataLoader;

/**
 * 视角选择页面
 * Created by wangxun on 16/3/30.
 */
public class PreLevelLookActivity extends BaseTitleBarActivity implements View.OnClickListener, YouXinTitleBar.YouXinTitleBarListener {
    private RelativeLayout startBtn;
    private RelativeLayout nomalLook, heartLook;
    private ImageView heartLookIv, normalLookIv;
    private TextView heartTipsTv;
    private ImageView lookSideBtnIv;
    private TextView lookSideBtnTv;
    private LinearLayout heartTipLayout;
    private TextView unLockTipTv;

    private int currentSelect; //选择视角 0正常1心跳
    private Boolean isHeartUnlocked;
    private String levelId;
    private Bitmap heartBitmap;
    private int day;
    private int lockStatus;//为0为解锁关卡，1为明天就解锁的关卡，2为明天以后才解锁的关卡

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_level_look);

        initView();
        initData();
    }

    private void initView() {
        startBtn = (RelativeLayout) findViewById(R.id.look_side_start_btn);
        startBtn.setOnClickListener(this);
        nomalLook = (RelativeLayout) findViewById(R.id.look_side_normal);
        nomalLook.setOnClickListener(this);
        heartLook = (RelativeLayout) findViewById(R.id.look_side_heart);
        heartLook.setOnClickListener(this);
        normalLookIv = (ImageView) findViewById(R.id.look_side_normal_img);
        heartLookIv = (ImageView) findViewById(R.id.look_side_heart_img);
        heartTipsTv = (TextView) findViewById(R.id.heart_tips2);
        lookSideBtnIv = (ImageView) findViewById(R.id.look_side_start_tips1);
        lookSideBtnTv = (TextView) findViewById(R.id.look_side_start_tips2);
        heartTipLayout = (LinearLayout) findViewById(R.id.heart_tip_layout);
        unLockTipTv = (TextView) findViewById(R.id.unlock_tip_tv);
        setTitleBarTitleText("选择视角");
        setLookSide(0);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            levelId = intent.getStringExtra(LevelSelectActivity.LEVEL_ID_KEY);
            isHeartUnlocked = intent.getBooleanExtra(LevelSelectActivity.HEART_LOOK_UNLOCK_KEY, false);
            day = intent.getIntExtra(LevelSelectActivity.LEVEL_DAY, 0);
            lockStatus = intent.getIntExtra(LevelSelectActivity.LOCK_STATUS, 0);
            if (lockStatus == 0) {
                setBtnStatus(0);
                unLockTipTv.setVisibility(View.GONE);
            } else {
                startBtn.setEnabled(false);
                if (lockStatus == 2) {
                    unLockTipTv.setText("先完成之前的关卡");
                } else if (lockStatus == 1) {
                    unLockTipTv.setText("明天解锁");
                }
                setBtnStatus(1);
            }
            String normalImg = intent.getStringExtra(LevelSelectActivity.NORMAL_IMG_KEY);
            final String heartImg = intent.getStringExtra(LevelSelectActivity.HEART_IMG_KEY);
            int heartUnlockDay = intent.getIntExtra(LevelSelectActivity.HEART_LOOK_UNLOCK_DAY_KEY, 0);
            WebDataLoader.getInstance(getApplicationContext()).loadImageView(normalLookIv, normalImg);
            WebDataLoader.getInstance(getApplicationContext()).loadImage(heartImg, new ImageListener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {

                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response != null && response.getBitmap() != null) {
                        heartBitmap = response.getBitmap();
                        if (!isHeartUnlocked && lockStatus != 0) {
                            heartLookIv.setImageBitmap(BitmapBlurHelper.doBlur(getApplicationContext(), heartBitmap, 3));
                            heartTipLayout.setBackgroundColor(Color.parseColor("#88000000"));
                        } else {
                            heartLookIv.setImageBitmap(heartBitmap);
                            heartTipLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        }
                    }
                }
            });
            //WebDataLoader.getInstance(getApplicationContext()).loadImageView(heartLookIv, heartImg);
            if (!isHeartUnlocked && lockStatus != 0) {
                heartTipsTv.setText("完成" + heartUnlockDay + "天的锻炼后解锁");
                //heartLookIv.setImageBitmap(BitmapBlurHelper.doBlur(getApplicationContext(), ((BitmapDrawable) getResources().getDrawable(R.mipmap.suxing)).getBitmap(), 3));
            } else {
                heartTipsTv.setText("");
            }
        }
    }

    private void setLookSide(int currentSelect) {
        this.currentSelect = currentSelect;
        if (currentSelect == 0) {
            Drawable drawable = getResources().getDrawable(R.drawable.pre_level_select_shape);
            nomalLook.setBackgroundDrawable(drawable);
            heartLook.setBackgroundDrawable(null);
        } else {
            heartLook.setBackgroundDrawable(getResources().getDrawable(R.drawable.pre_level_select_shape));
            nomalLook.setBackgroundDrawable(null);
        }
    }

    //type=0为可以进入，1为未解锁
    private void setBtnStatus(int type) {
        switch (type) {
            case 0:
                startBtn.setBackgroundResource(R.drawable.level_btn_bg);
                lookSideBtnIv.setImageResource(R.mipmap.play);
                lookSideBtnTv.setVisibility(View.VISIBLE);
                break;
            case 1:
                startBtn.setBackgroundResource(R.drawable.level_unfinish_btn_shape);
                lookSideBtnIv.setImageResource(R.mipmap.lock);
                lookSideBtnTv.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.look_side_start_btn:
                if (isHeartUnlocked || currentSelect == 0) {
                    Intent intent = new Intent(PreLevelLookActivity.this, LevelVideoActivity.class);
                    intent.putExtra(LevelVideoActivity.LEVEL_ID_KEY, levelId);
                    intent.putExtra(LevelVideoActivity.LOOK_SIDE_KEY, currentSelect);
                    intent.putExtra(LevelSelectActivity.LEVEL_DAY, day);
                    startActivity(intent);
                    addActionEvent(ActionWebService.PARAMS_V1,"1");
                } else {
                    APPUtil.showToast(getApplicationContext(), "心急吃不了热豆腐~");
                }
                break;
            case R.id.look_side_normal:
                currentSelect = 0;
                setLookSide(0);
                if (lockStatus == 0) {
                    setBtnStatus(0);
                } else {
                    setBtnStatus(1);
                }
                break;
            case R.id.look_side_heart:
                currentSelect = 1;
                setLookSide(1);
                if (isHeartUnlocked && lockStatus == 0) {
                    setBtnStatus(0);
                } else {
                    setBtnStatus(1);
                }
                addActionEvent(ActionWebService.PARAMS_V3,"1");
                break;
            default:
                break;
        }
    }

    @Override
    public void rightListener(View v) {
        super.rightListener(v);
        addActionEvent(ActionWebService.PARAMS_V5,"1");
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_LEVEL_PRE;
    }
}
