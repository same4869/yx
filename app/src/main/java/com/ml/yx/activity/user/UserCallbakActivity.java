package com.ml.yx.activity.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.model.WelcomeBean;
import com.ml.yx.views.RoundAngleImageView;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.util.HashMap;

/**
 * Created by Lijj on 16/3/23.
 */
public class UserCallbakActivity extends BaseActivity implements View.OnClickListener {
    private static final String tag = UserCallbakActivity.class.getSimpleName();

    private RoundAngleImageView avatarView;
    private TextView titleView;
    private EditText contentEt,wxEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_callback);

        avatarView = (RoundAngleImageView) findViewById(R.id.callback_head_img);
        titleView = (TextView) findViewById(R.id.callback_head_text);
        contentEt = (EditText) findViewById(R.id.callback_content);
        wxEt = (EditText) findViewById(R.id.callback_wxnum);

        findViewById(R.id.callback_back).setOnClickListener(this);
        findViewById(R.id.callback_submit).setOnClickListener(this);

        titleView.setText("优形CEO会细读每一条反馈");

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callback_back:
                onBackPressed();
                break;
            case R.id.callback_submit:
                String content = contentEt.getText().toString();
                String wxNum = wxEt.getText().toString();
                if(TextUtils.isEmpty(content)){
                    APPUtil.showToast(getApplicationContext(),"请输入你的意见");
                    return;
                }
                if(TextUtils.isEmpty(wxNum)){
                    APPUtil.showToast(getApplicationContext(),"请输入你的微信号");
                    return;
                }
                requestSubmitCallback(content,wxNum);
                break;
        }
    }

    @Override
    protected String getPageCode() {
        return null;
    }

    private void requestSubmitCallback(String feedback,String wechatAcc){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("feedback",feedback);
        params.put("wechatAcc",wechatAcc);
        WebRequest request = new WebRequest(WebService.ACCOUNT_FEEDBACK_QUESTION_URL, params, WelcomeBean.class,
                new Response.Listener<WelcomeBean>() {

                    @Override
                    public void onResponse(WelcomeBean response) {
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if(response.isSuccess()) {
                            APPUtil.showToast(getApplicationContext(), "怒骂成功");
                            finish();
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
