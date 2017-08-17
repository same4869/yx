package com.ml.yx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ml.yx.R;
import com.ml.yx.activity.user.MessageActivity;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.event.TitleBarRedPointEvent;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.views.YouXinTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Lijj on 16/3/23.
 */
public abstract class BaseTitleBarActivity extends BaseActivity implements YouXinTitleBar.YouXinTitleBarListener {
    protected YouXinTitleBar youXinTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        youXinTitleBar = (YouXinTitleBar) findViewById(R.id.youxin_titlebar);
        if (youXinTitleBar != null) {
            youXinTitleBar.setYouXinTitleBarListener(this);
            if (SharedPreferencesUtil.hasNewMessage()){
                youXinTitleBar.showRedPoint();
            }
        }
    }

    protected void setTitleBarTitleText(String text) {
        if (youXinTitleBar != null) {
            youXinTitleBar.setTitleText(text);
        }
    }

    protected void setRightViewVisibility(int visibility) {
        if (youXinTitleBar != null) {
            youXinTitleBar.setRightViewVisibility(visibility);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void leftListener(View v) {
        onBackPressed();
    }

    @Override
    public void rightListener(View v) {
        Intent intent = new Intent(BaseTitleBarActivity.this, MessageActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(TitleBarRedPointEvent event) {
        if (event != null) {
            if (event.isVisible()) {
                if (youXinTitleBar != null) {
                    youXinTitleBar.showRedPoint();
                }
            } else {
                if (youXinTitleBar != null) {
                    youXinTitleBar.hideRedPoint();
                }
            }
        }
    }
}
