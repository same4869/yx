package com.ml.yx.activity.work;

import android.os.Bundle;

import com.ml.yx.R;
import com.ml.yx.activity.BaseActivity;

/**
 * Created by Lijj on 16/3/23.
 */
public class WorkBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected String getPageCode() {
        return null;
    }
}
