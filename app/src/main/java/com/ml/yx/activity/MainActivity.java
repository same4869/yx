package com.ml.yx.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ml.yx.R;
import com.ml.yx.activity.base.BaseMainActivity;
import com.ml.yx.activity.base.LoginActivity;
import com.ml.yx.activity.user.UserActivity;
import com.ml.yx.activity.work.LevelSelectActivity;
import com.ml.yx.comm.Constants;
import com.ml.yx.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity extends Activity implements View.OnClickListener {
    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mReceiver;

    private Button wxLoginBtn;

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        regToWx();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WXEntryActivity.ACTION_WX_LOGIN_STATUS);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.x call replaced
                if (intent.getAction().equals(WXEntryActivity.ACTION_WX_LOGIN_STATUS)) {
                    wxLoginBtn.setText(intent.getStringExtra("nickname"));
                }
            }
        };
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);

        initView();
    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
    }

    private void initView() {
        findViewById(R.id.test_huaxian).setOnClickListener(this);
        findViewById(R.id.test_base).setOnClickListener(this);
        wxLoginBtn = (Button) findViewById(R.id.test_weixin_login);
        wxLoginBtn.setOnClickListener(this);
        findViewById(R.id.test_guide).setOnClickListener(this);
        findViewById(R.id.test_user).setOnClickListener(this);
        findViewById(R.id.test_login).setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test_huaxian: {
                Intent intent = new Intent(MainActivity.this, LevelSelectActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.test_base: {
                Intent intent = new Intent(MainActivity.this, BaseMainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.test_weixin_login:
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "youxin_app";
                api.sendReq(req);
                break;
            case R.id.test_guide: {
                Intent intent = new Intent(MainActivity.this, GuideActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.test_user: {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
                break;
            }case R.id.test_login: {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

}