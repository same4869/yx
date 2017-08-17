package com.ml.yx.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseFragment;
import com.ml.yx.activity.user.UserWelcomeActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.AppInfoUtils;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.MyCountDownTimer;
import com.ml.yx.comm.ParamHelper;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.comm.StringUtil;
import com.ml.yx.model.AccountBean;
import com.ml.yx.model.BaseBean;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;
import com.ml.yx.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Lijj on 16/3/27.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener,TextWatcher {
    private final String tag = RegisterFragment.class.getSimpleName();

    private EditText phoneET, verfifyET;
    private Button sendVerfifyBtn, registerSubmitBtn;

    private MyCountDownTimer countDownTimer = null;
    private IWXAPI api;

    private int coachId;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.x call replaced
            if (intent.getAction().equals(WXEntryActivity.ACTION_WX_LOGIN_STATUS)) {
                if(getActivity() != null) {
                   sendBroadcast(new Intent(Constants.RECEIVER_FULL_USER_INFO));
                }
            }
        }
    };

    public RegisterFragment(){}

    public RegisterFragment(int coachId){
        this.coachId = coachId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WXEntryActivity.ACTION_WX_LOGIN_STATUS);
        registerReceiver(mReceiver, filter);

        regToWx();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_register, null);

        phoneET = (EditText) rootView.findViewById(R.id.register_phone);
        verfifyET = (EditText) rootView.findViewById(R.id.register_verfify);
        sendVerfifyBtn = (Button) rootView.findViewById(R.id.send_verfify_btn);
        registerSubmitBtn = (Button) rootView.findViewById(R.id.register_submit);

        sendVerfifyBtn.setOnClickListener(this);
        registerSubmitBtn.setOnClickListener(this);
        rootView.findViewById(R.id.wechat_login).setOnClickListener(this);

        phoneET.addTextChangedListener(this);
        verfifyET.addTextChangedListener(this);

        registerSubmitBtn.setEnabled(false);

        return rootView;

    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(getActivity(), Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        sendVerfifyBtn.setEnabled(false);

        countDownTimer = new MyCountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                String text = String.format("%d秒", millisUntilFinished / 1000);
                sendVerfifyBtn.setText(text);
            }

            @Override
            public void onFinish() {
                countDownTimer = null;
                sendVerfifyBtn.setEnabled(true);
                sendVerfifyBtn.setText("重发验证码");

            }
        };
        countDownTimer.start();
    }

    private void checkSubmitBtn(){
        String phone = phoneET.getText().toString();
        String verfify = verfifyET.getText().toString();

        boolean flag = false;
        if(phone != null && verfify != null){
            if(phone.length() >= 11 && verfify.length() >= 4){
                flag = true;
            }
        }
        if(flag){
            registerSubmitBtn.setEnabled(true);
        }else{
            registerSubmitBtn.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkSubmitBtn();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_verfify_btn: {
                String phoneNo = phoneET.getText().toString();
                if (!StringUtil.checkMobile(phoneNo)) {
                    APPUtil.showToast(getApplicationContext(), R.string.error_invalid_phone);
                    return;
                }
                requestRegisterSendSMS(phoneNo);

                addActionEvent(ActionWebService.PARAMS_V1,"1");
                break;
            }
            case R.id.register_submit: {
                HashMap<String, Object> map = ParamHelper.acceptParams(BaseMainActivity.class, false);
                String phoneNo = phoneET.getText().toString();
                String verfify = verfifyET.getText().toString();
                if (!StringUtil.checkMobile(phoneNo)) {
                    APPUtil.showToast(getApplicationContext(), R.string.error_invalid_phone);
                    return;
                }
                if (!StringUtil.checkVerfify(verfify)) {
                    APPUtil.showToast(getApplicationContext(), R.string.error_invalid_verfify);
                    return;
                }

                String instructorId = String.valueOf(map.get(SelectCoachActivity.PARAM_COACH));
                String sex = String.valueOf(map.get(SelectSexFragment.PARAM_SEX));
                String stature = String.valueOf(map.get(BirthAndWeightFragment.PARAM_HEIGHT));
                String mass = String.valueOf(map.get(BirthAndWeightFragment.PARAM_WEIGHT));
                String subject = String.valueOf(map.get(SelectTargetFragment.PARAM_TARGET));
                String days = String.valueOf(map.get(SelectDurationFragment.PARAM_DURATION));
                String part = String.valueOf(map.get(SelectBodyPartFragment.PARAM_BODY_PART));
                String losefat = String.valueOf(map.get(SelectDurationFragment.PARAM_LOSE_WEIGHT));
                String birthday = null;
                if (map.get(BirthAndWeightFragment.PARAM_BIRTH) != null) {
                    Calendar birthCal = (Calendar) map.get(BirthAndWeightFragment.PARAM_BIRTH);
                    Date birthDate = new Date(birthCal.getTimeInMillis());
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                    birthday = sf.format(birthDate);
                }
                if (phoneNo == null) {
                    APPUtil.showToast(getApplicationContext(), "请输入手机号码");
                    return;
                }
                if (verfify == null) {
                    APPUtil.showToast(getApplicationContext(), "请输入验证码");
                    return;
                }
                if (instructorId == null) {
                    APPUtil.showToast(getApplicationContext(), "请选择教练");
                    return;
                }
                if (sex == null) {
                    APPUtil.showToast(getApplicationContext(), "请选择性别");
                    return;
                }
                if (birthday == null) {
                    APPUtil.showToast(getApplicationContext(), "请填写出生日期");
                    return;
                }
                if (stature == null) {
                    APPUtil.showToast(getApplicationContext(), "请填写身高");
                    return;
                }
                if (mass == null) {
                    APPUtil.showToast(getApplicationContext(), "请填写体重");
                    return;
                }
                if (subject == null) {
                    APPUtil.showToast(getApplicationContext(), "请选择锻炼类型");
                    return;
                }
                if (days == null) {
                    APPUtil.showToast(getApplicationContext(), "请选择锻炼周期");
                    return;
                }
                if (part == null) {
                    APPUtil.showToast(getApplicationContext(), "请选择锻炼部位");
                    return;
                }
                if (subject.equals("1") && losefat == null) {
                    APPUtil.showToast(getApplicationContext(), "请选择减肥重量");
                    return;
                }

                requestRegister(phoneNo, verfify, instructorId, sex, birthday, stature, mass, subject, days, part, losefat);
                break;
            }
            case R.id.wechat_login: {
                if (!AppInfoUtils.isAppInstalled(getApplicationContext(), "com.tencent.mm")) {
                    Toast.makeText(getApplicationContext(), "检测到未安装微信", Toast.LENGTH_SHORT).show();
                    return;
                }
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "youxin_app";
                api.sendReq(req);
                break;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_REGISTER;
    }

    private void requestRegisterSendSMS(String phoneNo) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phoneNo", phoneNo);
        WebRequest request = new WebRequest(WebService.ACCOUNT_REGISTER_SENDSMS_URL, params, BaseBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            startTimer();
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
                        showLoadingDialog("发送中");
                    }

                });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    private void requestRegister(String phoneNo, String captcha, String instructorId, String sex, String birthday, String stature, String mass, String subject, String days, String part, String losefat) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phoneNo", phoneNo);
        params.put("captcha", captcha);
        params.put("instructorId", instructorId);
        params.put("sex", sex);
        params.put("birthday", birthday);
        params.put("stature", stature);
        params.put("mass", mass);
        params.put("subject", subject);
        params.put("days", days);
        params.put("part", part);
        params.put("losefat", losefat);
        WebRequest request = new WebRequest(WebService.ACCOUNT_REGISTER_USESMS_URL, params, AccountBean.class,
                new Response.Listener<AccountBean>() {

                    @Override
                    public void onResponse(AccountBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            APPUtil.showToast(getApplicationContext(), "注册成功");
                            SharedPreferencesUtil.setUserToken(response.getToken());
                            SharedPreferencesUtil.setInstructorId(coachId);
                            WebRequest.setToken(response.getToken());
                            startActivity(new Intent(getActivity(), UserWelcomeActivity.class));
                            getActivity().finish();
                            //移除注册参数
                            ParamHelper.acceptParams(RegisterFragment.class);
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
                        showLoadingDialog("稍等");
                    }

                });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

}
