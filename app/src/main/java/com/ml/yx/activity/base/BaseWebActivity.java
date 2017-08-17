package com.ml.yx.activity.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;

/**
 * Created by xunwang on 16/5/9.
 */
public class BaseWebActivity extends BaseTitleBarActivity {
    public static final String WEB_URL_KEY = "web_url_key";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_webview);
        initView();
        initData();
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.base_webview);
        setWebSetting(webView.getSettings());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        setTitleBarTitleText("每日食谱");
    }

    private void initData() {
        Intent intent = getIntent();
        if(intent != null){
            String url = intent.getStringExtra(WEB_URL_KEY);
            webView.loadUrl(url);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    protected void setWebSetting(WebSettings setting) {
        setting.setJavaScriptEnabled(true);
        setting.setAppCacheEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setAppCacheEnabled(false);
        setting.setBuiltInZoomControls(false);
        setting.setSupportZoom(false);
        setting.setDomStorageEnabled(true);
        setting.setUseWideViewPort(true);
    }

    @Override
    protected String getPageCode() {
        return null;
    }

}
