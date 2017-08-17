package com.ml.yx.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseActivity;
import com.ml.yx.activity.BaseFragment;
import com.ml.yx.activity.user.UserWelcomeActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.CommUtil;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.ParamHelper;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.comm.StringUtil;
import com.ml.yx.model.BaseBean;
import com.ml.yx.views.RoundAngleImageView;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Lijj on 16/3/23.
 */
public class BaseMainActivity extends BaseActivity implements View.OnClickListener {
    private static final String tag = BaseMainActivity.class.getSimpleName();

    public final static String TARGET_PAGE = "target_page";
    public final static String COACH_ID = "coach_id";
    public final static String TO_WELCOME = "to_welcome";

    public final static String PAGE_SELECT_TARGET = SelectTargetFragment.class.getSimpleName();
    public final static String PAGE_SELECT_SEX = SelectSexFragment.class.getSimpleName();
    public final static String PAGE_BIRTH_WEIGHT = BirthAndWeightFragment.class.getSimpleName();
    public final static String PAGE_SELECT_DURATION = SelectDurationFragment.class.getSimpleName();
    public final static String PAGE_SELECT_BODY_PART = SelectBodyPartFragment.class.getSimpleName();
    public final static String PAGE_REGISTER = RegisterFragment.class.getSimpleName();

    private View backBtn;
    private RoundAngleImageView avatarView;
    private TextView titleView;

    private boolean toWelcome = true;

    private int coachId;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.RECEIVER_FULL_USER_INFO)) {
                checkUserLoginWithWx();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.RECEIVER_FULL_USER_INFO);
        registerReceiver(mReceiver, filter);

        backBtn = findViewById(R.id.base_back);
        avatarView = (RoundAngleImageView) findViewById(R.id.base_head_img);
        titleView = (TextView) findViewById(R.id.base_head_text);
        backBtn.setOnClickListener(this);

        String targetPage = getIntent().getStringExtra(TARGET_PAGE);
        coachId = getIntent().getIntExtra(COACH_ID,0);
        toWelcome = getIntent().getBooleanExtra(TO_WELCOME, true);

        avatarView.setImageDrawable(CommUtil.getCoachDrawable(getApplicationContext(),coachId));
//      WebDataLoader.getInstance(getApplicationContext()).loadImageView(avatarView, coachAvatar);
        goToPage(targetPage);
    }

    public void goToPage(String targetPage) {
        if (StringUtil.isEmpty(targetPage)) {
            targetPage = PAGE_REGISTER;
        }
        BaseFragment targetFragment = null;
        if (PAGE_REGISTER.equals(targetPage)) {
            targetFragment = new RegisterFragment(coachId);
        } else if (PAGE_SELECT_TARGET.equals(targetPage)) {
            targetFragment = new SelectTargetFragment();
        } else if (PAGE_SELECT_SEX.equals(targetPage)) {
            targetFragment = new SelectSexFragment();
        } else if (PAGE_BIRTH_WEIGHT.equals(targetPage)) {
            targetFragment = new BirthAndWeightFragment();
        } else if (PAGE_SELECT_DURATION.equals(targetPage)) {
            targetFragment = new SelectDurationFragment();
        } else if (PAGE_SELECT_BODY_PART.equals(targetPage)) {
            targetFragment = new SelectBodyPartFragment();
        }
        switchTitle(targetPage);
        if (targetFragment != null) {
            showNextFragment(targetFragment);
        }
    }

    private void switchTitle(String targetPage) {
        String title = null;
        if (PAGE_REGISTER.equals(targetPage)) {
            title = getResources().getString(R.string.register_tip);
        } else if (PAGE_SELECT_TARGET.equals(targetPage)) {
            title = getResources().getString(R.string.target_tip);
        } else if (PAGE_SELECT_SEX.equals(targetPage)) {
            title = getResources().getString(R.string.sex_tip);
        } else if (PAGE_BIRTH_WEIGHT.equals(targetPage)) {
            title = getResources().getString(R.string.birth_tip);
        } else if (PAGE_SELECT_DURATION.equals(targetPage)) {
            title = getResources().getString(R.string.duration_tip);
        } else if (PAGE_SELECT_BODY_PART.equals(targetPage)) {
            title = getResources().getString(R.string.body_part_tip);
        }
        setTextTitle(title);
    }

    public void setTextTitle(String text) {
        if (text != null && text.equals(titleView.getText().toString())) {
            return;
        }
        titleView.setText(text);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.head_text_anim);
        titleView.startAnimation(anim);
    }

    private void showNextFragment(BaseFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_pop_enter, R.anim.fragment_pop_exit);
        ft.replace(R.id.fragment_content, fragment, fragment.getTagText());
        ft.addToBackStack(fragment.getTagText());
        ft.commitAllowingStateLoss();
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!handleBackPressed()) {
            finish();
        }
    }

    private boolean handleBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 1) {
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(count - 1);
            if (count >= 2) {
                FragmentManager.BackStackEntry newEntry = getSupportFragmentManager().getBackStackEntryAt(count - 2);
                switchTitle(newEntry.getName());
            }
            //BBLog call replaced
            getSupportFragmentManager().popBackStack(entry.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyUp(keyCode, event);
    }

    //两种情况 1.用户在这个页面之前已经登录，也就是从loginActivity那里登录的 2.用户在这个页面里点击用微信登录
    //第一种情况在页面开始判断，然后直接调用更新数据接口
    private void checkUserLoginWithWx() {
        if (SharedPreferencesUtil.getUserToken() != null) {
            HashMap<String, Object> map = ParamHelper.acceptParams(BaseMainActivity.class);
            String instructorId = String.valueOf(map.get(SelectCoachActivity.PARAM_COACH));
            String sex = String.valueOf(map.get(SelectSexFragment.PARAM_SEX));
            String stature = String.valueOf(map.get(BirthAndWeightFragment.PARAM_HEIGHT));
            String mass = String.valueOf(map.get(BirthAndWeightFragment.PARAM_WEIGHT));
            String subject = String.valueOf(map.get(SelectTargetFragment.PARAM_TARGET));
            String days = String.valueOf(map.get(SelectDurationFragment.PARAM_DURATION));
            String part = String.valueOf(map.get(SelectBodyPartFragment.PARAM_BODY_PART));
            String losefat = String.valueOf(map.get(SelectDurationFragment.PARAM_LOSE_WEIGHT));
            String birthday = null;
            if (map.get(BirthAndWeightFragment.PARAM_BIRTH) != null) {
                Calendar birthCal = (Calendar) map.get(BirthAndWeightFragment.PARAM_BIRTH);
                Date birthDate = new Date(birthCal.getTimeInMillis());
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                birthday = sf.format(birthDate);
            }
            requestUpdateInfoWithWx(instructorId, sex, birthday, stature, mass, subject, days, part, losefat, SharedPreferencesUtil.getUserToken());
        }
    }

    private void requestUpdateInfoWithWx(String instructorId, String sex, String birthday, String stature, String mass, String subject, String days, String part, String losefat, String token) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (StringUtil.isNotBlank(instructorId)) {
            params.put("instructorId", instructorId);
        }
        if (StringUtil.isNotBlank(sex)) {
            params.put("sex", sex);
        }
        if (StringUtil.isNotBlank(birthday)) {
            params.put("birthday", birthday);
        }
        if (StringUtil.isNotBlank(stature)) {
            params.put("stature", stature);
        }
        if (StringUtil.isNotBlank(mass)) {
            params.put("mass", mass);
        }
        if (StringUtil.isNotBlank(subject)) {
            params.put("subject", subject);
        }
        if (StringUtil.isNotBlank(days)) {
            params.put("days", days);
        }
        if (StringUtil.isNotBlank(part)) {
            params.put("part", part);
        }
        if (StringUtil.isNotBlank(losefat)) {
            params.put("losefat", losefat);
        }
        if (StringUtil.isNotBlank(token)) {
            params.put("token", token);
        }
        WebRequest request = new WebRequest(WebService.ACCOUNT_PROFILE_EDIT_URL, params, BaseBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                        if (isActivityDestroyed()) {
                            return;
                        }
                        cancelLoadingDialog();
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            //移除注册参数
                            ParamHelper.acceptParams(RegisterFragment.class);

                            if (toWelcome) {
                                startActivity(new Intent(BaseMainActivity.this, UserWelcomeActivity.class));
                            }
                            setResult(RESULT_OK);
                            finish();
                        } else {
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
