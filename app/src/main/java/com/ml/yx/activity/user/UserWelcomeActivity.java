package com.ml.yx.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseActivity;
import com.ml.yx.activity.work.LevelSelectActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.CommUtil;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.model.WelcomeBean;
import com.ml.yx.views.RoundAngleImageView;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.util.HashMap;

/**
 * Created by Lijj on 16/3/23.
 */
public class UserWelcomeActivity extends BaseActivity implements View.OnClickListener{
    private static final String tag = UserWelcomeActivity.class.getSimpleName();

    private RoundAngleImageView avatarView;
    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_welcome);

        sendBroadcast(new Intent(Constants.RECEIVER_START_WORK));

        avatarView = (RoundAngleImageView) findViewById(R.id.user_head_img);
        titleView = (TextView) findViewById(R.id.user_head_text);

        findViewById(R.id.user_start_btn).setOnClickListener(this);

        requestWelcome();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_start_btn:
                startActivity(new Intent(UserWelcomeActivity.this, LevelSelectActivity.class));
                finish();
                break;
        }
    }

    @Override
    protected String getPageCode() {
        return null;
    }

    private void showViews(WelcomeBean bean){
//        WebDataLoader.getInstance(getApplicationContext()).loadImageView(avatarView,bean.getAvatar());
        avatarView.setImageDrawable(CommUtil.getCoachDrawable(getApplicationContext(), SharedPreferencesUtil.getInstructorId()));
        titleView.setText(bean.getSummary());
    }

    private void requestWelcome(){
        HashMap<String, String> params = new HashMap<String, String>();
        WebRequest request = new WebRequest(WebService.ACCOUNT_REGISTER_WELCOME_URL, params, WelcomeBean.class,
                new Response.Listener<WelcomeBean>() {

                    @Override
                    public void onResponse(WelcomeBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if(response.isSuccess()) {
                            showViews(response);
                        }else{
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
