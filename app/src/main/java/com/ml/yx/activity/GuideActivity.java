package com.ml.yx.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.ml.yx.R;
import com.ml.yx.activity.base.LoginActivity;
import com.ml.yx.activity.base.SelectCoachActivity;
import com.ml.yx.activity.work.LevelSelectActivity;
import com.ml.yx.comm.AppInfoUtils;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.location.WenbaLocationClient;
import com.ml.yx.useraction.ActionWebService;

/**
 * Created by BTyang on 16/4/3.
 */
public class GuideActivity extends BaseActivity implements View.OnClickListener {


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.RECEIVER_START_WORK)) {
                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.RECEIVER_START_WORK);
        registerReceiver(mReceiver, filter);

        findViewById(R.id.tv_login).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);

        if("baidu".equals(AppInfoUtils.getChannelByMeta(getApplicationContext()))){
            findViewById(R.id.logo_layout).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.logo_layout).setVisibility(View.GONE);
        }

        ActionWebService.requestAction(getApplicationContext(), ActionWebService.BOOT_URL, null);

        // 位置定位
        new WenbaLocationClient(getApplicationContext()).requestLocation(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String token = SharedPreferencesUtil.getUserToken();
                if (token != null) {
                    startActivity(new Intent(GuideActivity.this, LevelSelectActivity.class));
                    finish();
                } else {
                    findViewById(R.id.logo_layout).setVisibility(View.GONE);
                    findViewById(R.id.buttons_layout).setVisibility(View.VISIBLE);
                }
            }
        }, 2000L);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                Intent intent = new Intent(GuideActivity.this, SelectCoachActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_login:
                startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected String getPageCode() {
        return null;
    }
}
