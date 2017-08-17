package com.ml.yx.activity.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.activity.base.BaseMainActivity;
import com.ml.yx.activity.base.BirthAndWeightFragment;
import com.ml.yx.activity.base.SelectBodyPartFragment;
import com.ml.yx.activity.base.SelectCoachActivity;
import com.ml.yx.activity.base.SelectDurationFragment;
import com.ml.yx.activity.base.SelectSexFragment;
import com.ml.yx.activity.base.SelectTargetFragment;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.ParamHelper;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.comm.StringUtil;
import com.ml.yx.model.AccountBean;
import com.ml.yx.model.HistoryBean;
import com.ml.yx.model.TrainBean;
import com.ml.yx.model.UserBean;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.views.ChartViews;
import com.ml.yx.views.RoundAngleImageView;
import com.ml.yx.views.YouXinDialog;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lijj on 16/3/30.
 */
public class UserActivity extends BaseTitleBarActivity implements View.OnClickListener{
    private final String tag = UserActivity.class.getSimpleName();

    private static final int EDIT_USER_INFO_REQUEST = 1;

    private RoundAngleImageView avatarView;
    private ChartViews chartView;
    private EditText nickNameET;
    private TextView totalCalorieTV,trainTimeTV,targetMassTV,statureTV,massTV,sexTV,brithdayTV,periodTV,targetTV,instructorNameTV;

    private UserBean curUserBean;

    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int type = v.getId();
            youXinDialog = new YouXinDialog(UserActivity.this,null,"修改信息会放弃已有计划，真的要修改吗？");
            youXinDialog.show();

            youXinDialog.setLeftButtonText("取消");
            youXinDialog.setRightButtonText("修改");
            youXinDialog.setRightListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    updateUserInfo(type);
                }
            });

        }
    };

    private void updateUserInfo(int type){
        String pageIndex = null;
        switch(type) {
            case R.id.user_target_item:
                pageIndex = BaseMainActivity.PAGE_SELECT_TARGET;
                break;
            case R.id.user_sex_item:
                pageIndex = BaseMainActivity.PAGE_SELECT_SEX;
                break;
            case R.id.user_brithday_item:
                pageIndex = BaseMainActivity.PAGE_BIRTH_WEIGHT;
                break;
            case R.id.user_stature_item:
                pageIndex = BaseMainActivity.PAGE_BIRTH_WEIGHT;
                break;
            case R.id.user_mass_item:
                pageIndex = BaseMainActivity.PAGE_BIRTH_WEIGHT;
                break;
            case R.id.user_period_item:
                pageIndex = BaseMainActivity.PAGE_SELECT_DURATION;
                break;
        }

        HashMap<String, Object> map =  ParamHelper.acquireParamsReceiver(BaseMainActivity.class);
        map.clear();
        map.put(SelectSexFragment.PARAM_SEX,curUserBean.getSex());
        map.put(BirthAndWeightFragment.PARAM_HEIGHT,curUserBean.getStature());
        map.put(BirthAndWeightFragment.PARAM_WEIGHT,curUserBean.getMass());

        Calendar calendar =  Calendar.getInstance();
        if(StringUtil.isNotBlank(curUserBean.getBirthday())){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(curUserBean.getBirthday());
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        map.put(BirthAndWeightFragment.PARAM_BIRTH,calendar);

        map.put(SelectTargetFragment.PARAM_TARGET,curUserBean.getSubject());
        map.put(SelectDurationFragment.PARAM_DURATION,curUserBean.getDays());
        map.put(SelectDurationFragment.PARAM_LOSE_WEIGHT,curUserBean.getLosefat());
        map.put(SelectBodyPartFragment.PARAM_BODY_PART,curUserBean.getPart());

        Intent intent = new Intent(UserActivity.this, BaseMainActivity.class);
        intent.putExtra(BaseMainActivity.TARGET_PAGE,pageIndex);
        intent.putExtra(BaseMainActivity.TO_WELCOME,false);
        intent.putExtra(BaseMainActivity.COACH_ID,SharedPreferencesUtil.getInstructorId());
        startActivityForResult(intent,EDIT_USER_INFO_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        avatarView = (RoundAngleImageView) findViewById(R.id.user_head_img);

        chartView = (ChartViews) findViewById(R.id.user_chart);

        nickNameET = (EditText) findViewById(R.id.user_nickname_edt);

        totalCalorieTV = (TextView) findViewById(R.id.user_total_calorie);
        trainTimeTV = (TextView) findViewById(R.id.user_trainning_time);
        targetMassTV = (TextView) findViewById(R.id.user_target_mass);
        statureTV = (TextView) findViewById(R.id.user_stature);
        massTV = (TextView) findViewById(R.id.user_mass);
        sexTV = (TextView) findViewById(R.id.user_sex);
        targetTV = (TextView) findViewById(R.id.user_target);
        instructorNameTV = (TextView) findViewById(R.id.user_instructor_name);
        brithdayTV = (TextView) findViewById(R.id.user_brithday);
        periodTV = (TextView) findViewById(R.id.user_period);

        nickNameET.setOnClickListener(this);

        findViewById(R.id.user_instructor_item).setOnClickListener(this);
        findViewById(R.id.user_callback_item).setOnClickListener(this);
        findViewById(R.id.user_appraise_item).setOnClickListener(this);

        findViewById(R.id.user_target_item).setOnClickListener(itemClickListener);
        findViewById(R.id.user_sex_item).setOnClickListener(itemClickListener);
        findViewById(R.id.user_brithday_item).setOnClickListener(itemClickListener);
        findViewById(R.id.user_stature_item).setOnClickListener(itemClickListener);
        findViewById(R.id.user_mass_item).setOnClickListener(itemClickListener);
        findViewById(R.id.user_period_item).setOnClickListener(itemClickListener);

        setTitleBarTitleText("我的");
        setRightViewVisibility(View.GONE);

        requestUserDetail();

    }

    private void showViews(UserBean userBean){
        if(userBean == null){
            return;
        }
        curUserBean = userBean;

//        WebDataLoader.getInstance(getApplicationContext()).loadImageView(avatarView,userBean.getInstructorAvatar());

        nickNameET.setText(userBean.getNickname());

        TrainBean trainBean = userBean.getTrainning();
        if(trainBean != null){
            int hour = trainBean.getTotalTime() / 60 / 60;
            int minute = trainBean.getTotalTime() / 60;
            trainTimeTV.setText(Html.fromHtml(hour+"小时"+minute+"分钟"));
            targetMassTV.setText(Html.fromHtml(trainBean.getGoal()+"公斤&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));

            totalCalorieTV.setText(trainBean.getTotalCalorie()+"Kcal");

            List<HistoryBean> historyData = trainBean.getHistory();

            if(historyData != null && !historyData.isEmpty()) {
                if (historyData.size() == 1) {
                    HistoryBean bean = historyData.get(0);
                    if (bean != null) {
                        historyData.add(new HistoryBean(0, bean.getTimeStamp() * 1000 + 1000 * 60 * 60 * 24));
                    }
                }

                List<ChartViews.ChartData> chartData = new ArrayList<ChartViews.ChartData>(historyData.size());
                for(HistoryBean bean : historyData){
                    chartData.add(new ChartViews.ChartData(bean.getAxisValue(),bean.getCal()));
                }

                chartView.setDatas(chartData);
            }

            boolean flag = false;
            if(historyData != null && !historyData.isEmpty()){
                for(HistoryBean bean : historyData){
                    if(bean != null && bean.getCal() > 0){
                        flag = true;
                        break;
                    }
                }
            }
            if(flag){
                findViewById(R.id.user_no_chart).setVisibility(View.GONE);
                chartView.setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.user_no_chart).setVisibility(View.VISIBLE);
                chartView.setVisibility(View.INVISIBLE);
            }

        }
        statureTV.setText(userBean.getStature()+"厘米");
        massTV.setText(userBean.getMass()+" 公斤");
        sexTV.setText(userBean.getSex()==0?"女":"男");
        SharedPreferencesUtil.setSex(userBean.getSex());
        brithdayTV.setText(userBean.getBirthday());


        String subject = null;
        StringBuffer periodSB = new StringBuffer();
        periodSB.append(userBean.getDays()).append("天");
        SharedPreferencesUtil.setSubject(userBean.getSubject());
        switch(userBean.getSubject()){
            case 1:
                subject = "减脂";
                String[] weightNames = getResources().getStringArray(R.array.weight_name);
                periodSB.append(" ").append(weightNames[userBean.getLosefat()]);

                findViewById(R.id.user_line).setVisibility(View.VISIBLE);
                findViewById(R.id.user_target_mass_layout).setVisibility(View.VISIBLE);
                break;
            case 2:
                subject = "塑形";
                findViewById(R.id.user_line).setVisibility(View.GONE);
                findViewById(R.id.user_target_mass_layout).setVisibility(View.GONE);
                break;
            case 3:
                subject = "增肌";
                findViewById(R.id.user_line).setVisibility(View.GONE);
                findViewById(R.id.user_target_mass_layout).setVisibility(View.GONE);
                break;
        }

        targetTV.setText(subject);
        periodTV.setText(periodSB.toString());

        instructorNameTV.setText(userBean.getInstructorName());
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.user_nickname_edt: {
                nickNameET.setFocusable(true);
                nickNameET.setFocusableInTouchMode(true);
                nickNameET.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nickNameET.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(nickNameET, InputMethodManager.SHOW_FORCED);
                    }
                }, 200L);

                addActionEvent(ActionWebService.PARAMS_V1,"1");
                break;
            }
            case R.id.user_instructor_item:{
                Intent intent = new Intent(UserActivity.this, SelectCoachActivity.class);
                intent.putExtra(SelectCoachActivity.UPDATE_USER,true);
                startActivityForResult(intent,EDIT_USER_INFO_REQUEST);

                addActionEvent(ActionWebService.PARAMS_V2,"1");
                break;
            }
            case R.id.user_callback_item:{
                Intent intent = new Intent(UserActivity.this, UserCallbakActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.user_appraise_item:{
                toScore();
                break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        if(curUserBean != null) {
            String tempNickName = nickNameET.getText().toString();
            if (tempNickName != null && (!tempNickName.equals(curUserBean.getNickname()))) {
                requestEdit(tempNickName);
                return;
            }
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nickNameET.getWindowToken(), 0); //强制隐藏键盘
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDIT_USER_INFO_REQUEST && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            requestUserDetail();
        }
    }

    public void toScore() {
        try {
            Uri uri = Uri.parse("market://details?id=com.ml.yx");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            APPUtil.showToast(getApplicationContext(), "先安装应用市场再来支持优形吧！");
        }
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_USER;
    }

    private void requestUserDetail(){
        HashMap<String, String> params = new HashMap<String, String>();
        WebRequest request = new WebRequest(WebService.ACCOUNT_PROFILE_DETAIL_URL, params, UserBean.class,
                new Response.Listener<UserBean>() {

                    @Override
                    public void onResponse(UserBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if(response.isSuccess()){
                            showViews(response);
                        }else{
                            APPUtil.showToast(getApplicationContext(),response.getMsg());
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

    private void requestEdit(String nickName){
        HashMap<String, String> params = new HashMap<String, String>();
        if(nickName != null) {
            params.put("nickname", nickName);
        }
        WebRequest request = new WebRequest(WebService.ACCOUNT_PROFILE_EDIT_URL, params, AccountBean.class,
                new Response.Listener<AccountBean>() {

                    @Override
                    public void onResponse(AccountBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if(response.isSuccess()){
                            finish();
                        }else{
                            APPUtil.showToast(getApplicationContext(),response.getMsg());
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
                        showLoadingDialog("稍等");
                    }

                });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

}
