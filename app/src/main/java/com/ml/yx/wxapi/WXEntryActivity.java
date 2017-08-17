package com.ml.yx.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HTTPSTrustManager;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.activity.base.SelectCoachActivity;
import com.ml.yx.activity.work.LevelSelectActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.model.AccountWxBean;
import com.ml.yx.model.WXLoginBean;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;


public class WXEntryActivity extends BaseTitleBarActivity implements IWXAPIEventHandler {
    protected static final int RETURN_OPENID_ACCESSTOKEN = 0;// 返回openid，accessToken消息码
    protected static final int RETURN_NICKNAME_UID = 1; // 返回昵称，uid消息码
    public static final String ACTION_WX_LOGIN_STATUS = "action_wx_login_status";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RETURN_OPENID_ACCESSTOKEN:
                    Bundle bundle1 = (Bundle) msg.obj;
                    String accessToken = bundle1.getString("access_token");
                    String openId = bundle1.getString("openid");
                    String expiresIn = bundle1.getString("expires_in");
                    String refreshToken = bundle1.getString("refresh_token");
                    String scope = bundle1.getString("scope");

                    requestLoginWith(accessToken, expiresIn, refreshToken, openId, scope);

                    //getUID(openId, accessToken);
                    break;

                case RETURN_NICKNAME_UID:
                    Bundle bundle2 = (Bundle) msg.obj;
                    String nickname = bundle2.getString("nickname");
                    String uid = bundle2.getString("unionid");
//                    textView.setText("uid:" + uid);
//                    loginBtn.setText("昵称：" + nickname);
                    Intent loginSucessIntent = new Intent();
                    loginSucessIntent.setAction(ACTION_WX_LOGIN_STATUS);
                    loginSucessIntent.putExtra("nickname", nickname);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(loginSucessIntent);
                    finish();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    private void requestLoginWith(String access_token, String expirse_in, String refresh_token, String openid, String scope) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", access_token);
        params.put("expirse_in", expirse_in);
        params.put("refresh_token", refresh_token);
        params.put("openid", openid);
        params.put("scope", scope);
        WebRequest request = new WebRequest(WebService.ACCOUNT_LOGIN_USERWX_URL, params, AccountWxBean.class,
                new Response.Listener<AccountWxBean>() {

                    @Override
                    public void onResponse(AccountWxBean response) {
                        cancelLoadingDialog();
                        if (response.isSuccess()) {
                            APPUtil.showToast(getApplicationContext(), "登录成功");
                            SharedPreferencesUtil.setUserToken(response.getToken());
                            WebRequest.setToken(response.getToken());
                            SharedPreferencesUtil.setUserId(response.getAccountId());
                            if (response.isComplete()) {
                                startActivity(new Intent(WXEntryActivity.this, LevelSelectActivity.class));
                            } else {
                                startActivity(new Intent(WXEntryActivity.this, SelectCoachActivity.class));
                            }
                            Intent loginSucessIntent = new Intent(ACTION_WX_LOGIN_STATUS);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(loginSucessIntent);
                            finish();
                        } else {
                            APPUtil.showToast(getApplicationContext(), response.getMsg());
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
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
    public void onResp(BaseResp resp) {
        String result;

        boolean flag = false;

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) resp).code;
                result = "发送成功 " + code;
                flag = true;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                break;
            default:
                result = "发送返回";
                break;
        }

        if(flag) {
            SendAuth.Resp sendAuthResp = (SendAuth.Resp) resp;// 用于分享时不要有这个，不能强转
            String code = sendAuthResp.code;
            getResult(code);
        }else{
            APPUtil.showToast(getApplicationContext(), result);
            finish();
        }
    }

    private void getResult(final String code) {
        HTTPSTrustManager.allowAllSSL();
        String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + Constants.APP_ID
                + "&secret="
                + Constants.APP_SECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";
//        APPUtil.showToast(getApplicationContext(), "getResult path --> " + path);
        //Log.x call replaced
        WebRequest request = new WebRequest(path, Request.Method.GET, new HashMap<String, String>(), WXLoginBean.class, new Response.Listener<WXLoginBean>() {

            @Override
            public void onResponse(WXLoginBean response) {
//                Log.d("kkkkkkkk", "getResult onResponse");
                if (response != null) {
//                    Log.d("kkkkkkkk", "response.getOpenid() --> " + response.getOpenid());
//                    Log.d("kkkkkkkk", "access_token --> " + response.getAccess_token());
//                    Log.d("kkkkkkkk", "expires_in --> " + response.getExpires_in());
//                    Log.d("kkkkkkkk", "refresh_token --> " + response.getRefresh_token());
//                    Log.d("kkkkkkkk", "unionid --> " + response.getUnionid());
//                    Log.d("kkkkkkkk", "scope --> " + response.getScope());

                    Message msg = handler.obtainMessage();
                    msg.what = RETURN_OPENID_ACCESSTOKEN;
                    Bundle bundle = new Bundle();
                    bundle.putString("openid", response.getOpenid());
                    bundle.putString("access_token", response.getAccess_token());
                    bundle.putString("expires_in", response.getExpires_in());
                    bundle.putString("scope", response.getScope());
                    bundle.putString("refresh_token", response.getRefresh_token());
                    msg.obj = bundle;
                    handler.sendMessage(msg);
                }else{
                    finish();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("kkkkkkkk", "getResult onErrorResponse");
                APPUtil.showToast(getApplicationContext(), error.getMessage());
                finish();
            }

            @Override
            public void onStart() {
//                Log.d("kkkkkkkk", "getResult onStart");
            }

        });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

//    private void getUID(final String openId, final String accessToken) {
//        HTTPSTrustManager.allowAllSSL();
//        String path = "https://api.weixin.qq.com/sns/userinfo?access_token="
//                + accessToken + "&openid=" + openId + "&lang=zh_CN";
//        Log.d("kkkkkkkk", "getUID path --> " + path);
//        WebRequest request = new WebRequest(path, Request.Method.GET, new HashMap<String, String>(), WXLoginUserBean.class, new Response.Listener<WXLoginUserBean>() {
//
//            @Override
//            public void onResponse(WXLoginUserBean response) {
//                if (response != null) {
//                    Log.d("kkkkkkkk", "response.getNickname() --> " + response.getNickname());
//                    Log.d("kkkkkkkk", "response.getUnionid() --> " + response.getUnionid());
//                    Log.d("kkkkkkkk", "response.getSex() --> " + response.getSex());
//                    Log.d("kkkkkkkk", "response.getHeadimgurl() --> " + response.getHeadimgurl());
//                    Log.d("kkkkkkkk", "response.getCity() --> " + response.getCity());
//                    Log.d("kkkkkkkk", "response.getMsg() --> " + response.getMsg());
//
//                    Message msg = handler.obtainMessage();
//                    msg.what = RETURN_NICKNAME_UID;
//                    Bundle bundle = new Bundle();
//                    bundle.putString("nickname", response.getNickname());
//                    bundle.putString("unionid", response.getUnionid());
//                    msg.obj = bundle;
//                    handler.sendMessage(msg);
//
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                APPUtil.showToast(getApplicationContext(), error.getMessage());
//            }
//
//            @Override
//            public void onStart() {
//            }
//
//        });
//        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
//
//    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    protected String getPageCode() {
        return null;
    }

}