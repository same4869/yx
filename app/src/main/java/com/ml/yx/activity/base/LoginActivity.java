package com.ml.yx.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.activity.work.LevelSelectActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.AppInfoUtils;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.MyCountDownTimer;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.comm.StringUtil;
import com.ml.yx.model.AccountBean;
import com.ml.yx.model.BaseBean;
import com.ml.yx.service.HeartBeatService;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;
import com.ml.yx.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;

/**
 * Created by Lijj on 16/4/6.
 */
public class LoginActivity extends BaseTitleBarActivity implements View.OnClickListener,TextWatcher {
    private final String tag = LoginActivity.class.getSimpleName();

    private EditText phoneET, verfifyET;
    private Button sendVerfifyBtn, loginSubmitBtn;

    private MyCountDownTimer countDownTimer = null;

    private IWXAPI api;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d("kkkkkkkk", "intent.getAction() --> " + intent.getAction() + " intent.getStringExtra(\"nickname\") --> " + intent.getStringExtra("nickname"));
            if (intent.getAction().equals(WXEntryActivity.ACTION_WX_LOGIN_STATUS)) {
//                APPUtil.showToast(getApplicationContext(), intent.getStringExtra("nickname"));
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WXEntryActivity.ACTION_WX_LOGIN_STATUS);
        registerReceiver(mReceiver, filter);

        phoneET = (EditText) findViewById(R.id.login_phone);
        verfifyET = (EditText) findViewById(R.id.login_verfify);
        sendVerfifyBtn = (Button) findViewById(R.id.send_verfify_btn);
        loginSubmitBtn = (Button) findViewById(R.id.login_submit);

        sendVerfifyBtn.setOnClickListener(this);
        loginSubmitBtn.setOnClickListener(this);
        findViewById(R.id.wechat_login).setOnClickListener(this);

        phoneET.addTextChangedListener(this);
        verfifyET.addTextChangedListener(this);

        loginSubmitBtn.setEnabled(false);

        setTitleBarTitleText("登录");
        setRightViewVisibility(View.GONE);

        regToWx();
    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
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
            loginSubmitBtn.setEnabled(true);
        }else{
            loginSubmitBtn.setEnabled(false);
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
            case R.id.login_submit: {
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

                requestLogin(phoneNo, verfify);
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_LOGIN;
    }

    private void requestRegisterSendSMS(String phoneNo) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phoneNo", phoneNo);
        WebRequest request = new WebRequest(WebService.ACCOUNT_LOGIN_SENDSMS_URL, params, BaseBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            APPUtil.showToast(getApplicationContext(), "发送成功");
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

    private void requestLogin(String phoneNo, String vifify) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phoneNo", phoneNo);
        params.put("captcha", vifify);
        WebRequest request = new WebRequest(WebService.ACCOUNT_LOGIN_USESMS_URL, params, AccountBean.class,
                new Response.Listener<AccountBean>() {

                    @Override
                    public void onResponse(AccountBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            APPUtil.showToast(getApplicationContext(), "登录成功");
                            SharedPreferencesUtil.setUserToken(response.getToken());
                            SharedPreferencesUtil.setUserId(response.getAccountId());
                            WebRequest.setToken(response.getToken());
                            startActivity(new Intent(LoginActivity.this, LevelSelectActivity.class));
                            startService(new Intent(LoginActivity.this, HeartBeatService.class));
                            finish();
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
