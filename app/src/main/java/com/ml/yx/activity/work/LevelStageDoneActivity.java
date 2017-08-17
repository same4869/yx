package com.ml.yx.activity.work;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.activity.base.BaseMainActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.model.BaseBean;
import com.ml.yx.model.StageDoneBean;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.utils.TimeUtil;
import com.ml.yx.views.ChartViews;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xunwang on 16/5/10.
 */
public class LevelStageDoneActivity extends BaseTitleBarActivity implements View.OnClickListener {
    private final String tag = LevelSelectActivity.class.getSimpleName();

    private TextView stageDoneTitleTv;
    private ChartViews mChartViews;
    private TextView totleTimeTv, targetWeightTv, totalCalorieTV;
    private Button startNewStageBtn;
    private TextView dialogView;

    private List<ChartViews.ChartData> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_stage_done);

        initView();
        initData();
    }

    private void initView() {
        stageDoneTitleTv = (TextView) findViewById(R.id.stage_done_title);
        mChartViews = (ChartViews) findViewById(R.id.user_stage_chart);
        totleTimeTv = (TextView) findViewById(R.id.totle_time_tv);
        targetWeightTv = (TextView) findViewById(R.id.target_weight_tv);
        totalCalorieTV = (TextView) findViewById(R.id.user_total_calorie);
        startNewStageBtn = (Button) findViewById(R.id.level_stage_done_btn);
        startNewStageBtn.setOnClickListener(this);
        dialogView = (TextView) findViewById(R.id.dialog_head_text);
        setDialogTextTitle("效果满意吗？");
        setTitleBarTitleText("优形");
    }

    private void initData() {
        requestStageDoneInfo();
    }

    private void updateUI(StageDoneBean stageDoneBean) {
        totleTimeTv.setText(TimeUtil.formatTime(Long.parseLong(stageDoneBean.getTotleTime()) * 1000));
        targetWeightTv.setText(stageDoneBean.getTargetWeight() + "斤");
        totalCalorieTV.setText(stageDoneBean.getTotleCals() + "Kcal");
        //int[] cals = new int[stageDoneBean.getTipCals().size()];
        for (int i = 0; i < stageDoneBean.getTipCals().size(); i++) {
            //cals[i] = stageDoneBean.getTipCals().get(i).getTipCal();
            ChartViews.ChartData data = new ChartViews.ChartData(String.valueOf(i + 1), stageDoneBean.getTipCals().get(i).getTipCal());
            datas.add(data);
        }
        mChartViews.setDatas(datas);
    }

    private void requestStageDoneInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
        WebRequest request = new WebRequest(WebService.ACCOUNT_LEVEL_CURRENTSTAT_URL, params, StageDoneBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            StageDoneBean stageDoneBean = (StageDoneBean) response;
                            updateUI(stageDoneBean);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.level_stage_done_btn:
                Intent intent = new Intent(LevelStageDoneActivity.this, BaseMainActivity.class);
                intent.putExtra(BaseMainActivity.TARGET_PAGE, BaseMainActivity.PAGE_SELECT_TARGET);
                SharedPreferencesUtil.setAddNewLevelType(1);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void leftListener(View v) {
        super.leftListener(v);
        Intent intent = new Intent(LevelStageDoneActivity.this, LevelSelectActivity.class);
        intent.putExtra(LevelSelectActivity.COME_IN_NEW_STAGE_KEY, true);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(LevelStageDoneActivity.this, LevelSelectActivity.class);
            intent.putExtra(LevelSelectActivity.COME_IN_NEW_STAGE_KEY, true);
            startActivity(intent);
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void rightListener(View v) {
        super.rightListener(v);
        addActionEvent(ActionWebService.PARAMS_V2,"1");
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_LEVEL_STAGE_DONE;
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
}
