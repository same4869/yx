package com.ml.yx.activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.ml.yx.activity.user.MessageActivity;
import com.ml.yx.activity.work.LevelSelectActivity;
import com.ml.yx.comm.SharedPreferencesUtil;

public class CommonInvokerActivity extends BaseActivity {
    private static final String VIEW = "view";

    public static final String SCHEME_HOME = "home";
    public static final String SCHEME_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String view = uri.getQueryParameter(VIEW);
            if (LevelSelectActivity.inited) {
                boolean flag = switchActivity(uri, this);
                if (flag) {
                    finish();
                    return;
                }
            } else {
                SharedPreferencesUtil.setScheme(uri.toString());
            }
        }
        startActivity(new Intent(CommonInvokerActivity.this, GuideActivity.class));
        finish();
    }

	public static boolean switchActivity(Uri uri, Context context) {
		String view = uri.getQueryParameter(VIEW);
		if (view != null) {
			if (SCHEME_HOME.equals(view)) {
				return true;
			} else if (SCHEME_MESSAGE.equals(view)) {
				Intent intent = new Intent(context, MessageActivity.class);
				context.startActivity(intent);
				return true;
			}
		}
		return false;
	}


    @Override
    protected String getPageCode() {
        return null;
    }
}
