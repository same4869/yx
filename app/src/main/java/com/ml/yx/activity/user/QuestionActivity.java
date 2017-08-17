package com.ml.yx.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.CommUtil;
import com.ml.yx.comm.DateUtil;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.db.MessageDBHelper;
import com.ml.yx.model.BaseBean;
import com.ml.yx.model.MessageBean;
import com.ml.yx.views.RoundAngleImageView;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.util.Calendar;
import java.util.HashMap;

public class QuestionActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    public static final String PARAM_MODE = "mode";
    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_MSG_ID = "msg_id";

    public static final String RESULT_MESSAGE_BEAN = "result_message_bean";

    public static final int MODE_QUESTION = 0;
    public static final int MODE_REPLY = 1;

    private int mode = MODE_QUESTION;
    private String msgId = "";

    private EditText edtContent;
    private RoundAngleImageView avatarView;
    private TextView titleView;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        btnSubmit = (Button) findViewById(R.id.btn_next);
        btnSubmit.setOnClickListener(this);
        avatarView = (RoundAngleImageView) findViewById(R.id.base_head_img);
        titleView = (TextView) findViewById(R.id.base_head_text);
        edtContent = (EditText) findViewById(R.id.edt_content);
        findViewById(R.id.base_back).setOnClickListener(this);
        edtContent.addTextChangedListener(this);
        mode = getIntent().getIntExtra(PARAM_MODE, MODE_QUESTION);
        if (mode == MODE_REPLY) {
            String question = getIntent().getStringExtra(PARAM_MESSAGE);
            setTextTitle(question);
            msgId = getIntent().getStringExtra(PARAM_MSG_ID);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(DateUtil.getCurWenbaTime());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour >= 22 || hour < 9) {
                setTextTitle(getString(R.string.coach_offline_tip));
            } else {
                setTextTitle(getString(R.string.question_tip));
            }
        }
        avatarView.setImageDrawable(CommUtil.getCoachDrawable(getApplicationContext(),SharedPreferencesUtil.getInstructorId()));
//        if (StringUtil.isNotBlank(coachAvatar)) {
//            WebDataLoader.getInstance(getApplicationContext()).loadImageView(avatarView, coachAvatar);
//        }
    }

    public void setTextTitle(String text) {
        titleView.setText(text);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.head_text_anim);
        titleView.startAnimation(anim);
    }


    public void submitQuestion() {
        final String question = edtContent.getText().toString();
        HashMap<String, String> params = new HashMap<>();
        params.put("question", question);
        WebRequest request = new WebRequest(WebService.ACCOUNT_QA_QUESTION_URL, params, MessageBean.class, new Response.Listener<BaseBean>() {

            @Override
            public void onResponse(BaseBean response) {
                if (!isActivityDestroyed()) {
                    cancelLoadingDialog();
                }
                if (response != null) {
                    if (response.isSuccess()) {
                        MessageBean message = (MessageBean) response;
                        MessageDBHelper dbHelper = MessageDBHelper.getInstance();
                        dbHelper.save(message);
                        APPUtil.showToast(getApplicationContext(), getString(R.string.qa_submit_success));
                        Intent data = new Intent();
                        data.putExtra(RESULT_MESSAGE_BEAN, message);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                APPUtil.showToast(getApplicationContext(), error.getMessage());
                if (!isActivityDestroyed()) {
                    cancelLoadingDialog();
                }
            }

            @Override
            public void onStart() {
                showLoadingDialog();
            }

        });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    public void submitAnswer() {
        HashMap<String, String> params = new HashMap<>();
        params.put("answer", edtContent.getText().toString());
        params.put("msg_id", msgId);
        WebRequest request = new WebRequest(WebService.ACCOUNT_QA_ANSWER_URL, params, MessageBean.class, new Response.Listener<BaseBean>() {

            @Override
            public void onResponse(BaseBean response) {
                if (!isActivityDestroyed()) {
                    cancelLoadingDialog();
                }
                if (response != null) {
                    if (response.isSuccess()) {
                        MessageBean message = (MessageBean) response;
                        MessageDBHelper dbHelper = MessageDBHelper.getInstance();
                        dbHelper.update(message);
                        APPUtil.showToast(getApplicationContext(), getString(R.string.qa_submit_success));
                        Intent data = new Intent();
                        data.putExtra(RESULT_MESSAGE_BEAN, message);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                APPUtil.showToast(getApplicationContext(), error.getMessage());
                if (!isActivityDestroyed()) {
                    cancelLoadingDialog();
                }
            }

            @Override
            public void onStart() {
                showLoadingDialog();
            }

        });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (mode == MODE_QUESTION) {
                    submitQuestion();
                } else {
                    submitAnswer();
                }
                break;
            case R.id.base_back:
                finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        btnSubmit.setEnabled(s.length() > 0);
    }

    @Override
    protected String getPageCode() {
        return null;
    }
}
