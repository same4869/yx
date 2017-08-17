package com.ml.yx.activity.work;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.model.UserBean;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.utils.TimeUtil;
import com.ml.yx.views.WaterWaveView;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.util.HashMap;

/**
 * Created by xunwang on 16/4/14.
 */
public class LevelDoneActivity extends BaseTitleBarActivity implements View.OnClickListener {
    private final String tag = LevelSelectActivity.class.getSimpleName();
    private Button levelDoneBtn;
    private TextView levelDoneTv2, levelDoneTv3;
    private WaterWaveView originWaterWaveView, targetWaterWaveView;
    private TextView arrowTv;
    private ImageView targetIv;

    private long tasteTime;
    private int precent, targetPrecent, levelPrecent;
    private int sex, subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_done);

        initView();
        initData();
    }

    private void initView() {
        levelDoneBtn = (Button) findViewById(R.id.level_done_btn);
        levelDoneBtn.setOnClickListener(this);
        levelDoneTv2 = (TextView) findViewById(R.id.kcal_title_tv);
        levelDoneTv3 = (TextView) findViewById(R.id.time_title_tv);
        originWaterWaveView = (WaterWaveView) findViewById(R.id.water_origin);
        targetWaterWaveView = (WaterWaveView) findViewById(R.id.water_target);
        initWaterWave(originWaterWaveView);
        initWaterWave(targetWaterWaveView);
        arrowTv = (TextView) findViewById(R.id.level_done_arrow_tv);
        targetIv = (ImageView) findViewById(R.id.water_target_iv);

        originWaterWaveView.setHeightOffsetByProgress(100);
        targetWaterWaveView.setHeightOffsetByProgress(0);

        setTitleBarTitleText("优形");

    }

    //根据百分数来显示动画
    private void startWaveAnim(int precent) {
        targetPrecent = precent;
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            precent++;
            if (precent <= targetPrecent) {
                originWaterWaveView.setHeightOffsetByProgress(100 - precent);
                targetWaterWaveView.setHeightOffsetByProgress(8 + precent);
                arrowTv.setText(precent + "%");
                handler.sendEmptyMessageDelayed(0, 30);
            } else {
                handler.removeMessages(0);
            }
        }
    };

    private void initWaterWave(WaterWaveView waterWaveView) {
        waterWaveView.setOmegaByProgress(22);
        waterWaveView.setMoveSpeedByProgress(25);
        waterWaveView.setWaveHeightByProgress(6);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            tasteTime = intent.getLongExtra(LevelVideoActivity.TASTE_TIME_KEY, 0);
            String tipCal = intent.getStringExtra(LevelVideoActivity.TIP_CAL_KEY);
            int day = intent.getIntExtra(LevelVideoActivity.DAY_KEY, 1);
            int totleDay = SharedPreferencesUtil.getTotleLevel();
            levelPrecent = (int)(((float)day / totleDay) * 100);
            //Log.d("kkkkkkkk", "day --> " + day + " totleDay --> " + totleDay + " levelPrecent --> " + levelPrecent);

            levelDoneTv2.setText(tipCal);
            levelDoneTv3.setText(TimeUtil.formatTime(tasteTime));

            sex = SharedPreferencesUtil.getSex();
            subject = SharedPreferencesUtil.getSubject();
            if (sex != -1) {//不为-1则本地已经保存了用户性别和项目信息，不然需要重新拉取
                setAnimStatus(sex, subject);
                startWaveAnim(levelPrecent);
            } else {
                requestUserDetail();
            }

        }
    }

    /**
     * 减脂   胖变瘦
     * 增肌塑形  瘦变壮
     * 再分个男女
     *
     * @param sex
     * @param subject
     */
    private void setAnimStatus(int sex, int subject) {
        if (sex == 0 && subject == 1) {
            targetIv.setImageResource(R.mipmap.level_done_female_target);
        } else if (sex == 0 && (subject == 2 || subject == 3)) {
            targetIv.setImageResource(R.mipmap.level_done_female_zhuang);
        } else if (sex == 1 && subject == 1) {
            targetIv.setImageResource(R.mipmap.level_done_male_target);
        } else if (sex == 0 && (subject == 2 || subject == 3)) {
            targetIv.setImageResource(R.mipmap.level_done_male_zhuang);
        }
    }

    private void requestUserDetail() {
        HashMap<String, String> params = new HashMap<String, String>();
        WebRequest request = new WebRequest(WebService.ACCOUNT_PROFILE_DETAIL_URL, params, UserBean.class,
                new Response.Listener<UserBean>() {

                    @Override
                    public void onResponse(UserBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            setAnimStatus(response.getSex(), response.getSubject());
                            SharedPreferencesUtil.setSex(response.getSex());
                            SharedPreferencesUtil.setSubject(response.getSubject());
                            startWaveAnim(levelPrecent);
                        } else {
                            APPUtil.showToast(getApplicationContext(), response.getMsg());
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //BBLog call replaced
                        cancelLoadingDialog();
                        APPUtil.showToast(getApplicationContext(), error.getMessage());
                        finish();
                    }

                    @Override
                    public void onStart() {
                        if (isActivityDestroyed()) {
                            return;
                        }
                        showLoadingDialog("稍等");
                    }

                });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.level_done_btn:
                Intent intent = new Intent(LevelDoneActivity.this, LevelSelectActivity.class);
                intent.putExtra(LevelSelectActivity.COME_IN_DONE_KEY, true);
                startActivity(intent);
                addActionEvent(ActionWebService.PARAMS_V1,"1");
                finish();
                break;
        }
    }

    @Override
    public void rightListener(View v) {
        super.rightListener(v);
        addActionEvent(ActionWebService.PARAMS_V3,"1");
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_LEVEL_RESULT;
    }
}
