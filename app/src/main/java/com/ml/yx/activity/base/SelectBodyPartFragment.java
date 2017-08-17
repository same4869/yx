package com.ml.yx.activity.base;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseFragment;
import com.ml.yx.activity.work.LevelSelectActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.ParamHelper;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.model.BaseBean;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by BTyang on 16/4/12.
 */
public class SelectBodyPartFragment extends BaseFragment implements View.OnClickListener {

    public final static String PARAM_BODY_PART = "body_part";
    private ImageView ivPart1, ivPart2, ivPart3, ivPart4, ivPart5, ivPart6;
    private TextView tvPart1, tvPart2, tvPart3, tvPart4, tvPart5, tvPart6;
    private View part1, part2, part3, part4, part5, part6;
    private Button btnNext;
    private List<Integer> selectedParts = new ArrayList<>();
    private final int PART_CODE1 = 1;
    private final int PART_CODE2 = 2;
    private final int PART_CODE3 = 4;
    private final int PART_CODE4 = 8;
    private final int PART_CODE5 = 16;
    private final int PART_CODE6 = 32;

    public SelectBodyPartFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_body_part, container, false);
        ivPart1 = (ImageView) rootView.findViewById(R.id.iv_image1);
        ivPart2 = (ImageView) rootView.findViewById(R.id.iv_image2);
        ivPart3 = (ImageView) rootView.findViewById(R.id.iv_image3);
        ivPart4 = (ImageView) rootView.findViewById(R.id.iv_image4);
        ivPart5 = (ImageView) rootView.findViewById(R.id.iv_image5);
        ivPart6 = (ImageView) rootView.findViewById(R.id.iv_image6);
        tvPart1 = (TextView) rootView.findViewById(R.id.tv_name1);
        tvPart2 = (TextView) rootView.findViewById(R.id.tv_name2);
        tvPart3 = (TextView) rootView.findViewById(R.id.tv_name3);
        tvPart4 = (TextView) rootView.findViewById(R.id.tv_name4);
        tvPart5 = (TextView) rootView.findViewById(R.id.tv_name5);
        tvPart6 = (TextView) rootView.findViewById(R.id.tv_name6);
        part1 = rootView.findViewById(R.id.body_part1);
        part2 = rootView.findViewById(R.id.body_part2);
        part3 = rootView.findViewById(R.id.body_part3);
        part4 = rootView.findViewById(R.id.body_part4);
        part5 = rootView.findViewById(R.id.body_part5);
        part6 = rootView.findViewById(R.id.body_part6);
        part1.setOnClickListener(this);
        part2.setOnClickListener(this);
        part3.setOnClickListener(this);
        part4.setOnClickListener(this);
        part5.setOnClickListener(this);
        part6.setOnClickListener(this);
        btnNext = (Button) rootView.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

        HashMap<String, Object> map = ParamHelper.acceptParams(BaseMainActivity.class, false);
        int sex = SelectSexFragment.SEX_MALE;
        if (map.containsKey(SelectSexFragment.PARAM_SEX)) {
            sex = (int) map.get(SelectSexFragment.PARAM_SEX);
        }
        if (sex == SelectSexFragment.SEX_MALE) {
            ivPart1.setImageResource(R.mipmap.body_part_male_xiong);
            tvPart1.setText(getString(R.string.body_part_part_1));
            part1.setTag(PART_CODE1);
            ivPart2.setImageResource(R.mipmap.body_part_male_bei);
            tvPart2.setText(getString(R.string.body_part_part_2));
            part2.setTag(PART_CODE2);
            ivPart3.setImageResource(R.mipmap.body_part_male_fu);
            tvPart3.setText(getString(R.string.body_part_part_3));
            part3.setTag(PART_CODE3);
            ivPart4.setImageResource(R.mipmap.body_part_male_bi);
            tvPart4.setText(getString(R.string.body_part_part_4));
            part4.setTag(PART_CODE4);
            ivPart5.setImageResource(R.mipmap.body_part_male_tui);
            tvPart5.setText(getString(R.string.body_part_part_5));
            part5.setTag(PART_CODE5);
            ivPart6.setImageResource(R.mipmap.body_part_male_tun);
            tvPart6.setText(getString(R.string.body_part_part_6));
            part6.setTag(PART_CODE6);
        } else {
            ivPart1.setImageResource(R.mipmap.body_part_female_tui);
            tvPart1.setText(getString(R.string.body_part_part_5));
            part1.setTag(PART_CODE5);
            ivPart2.setImageResource(R.mipmap.body_part_female_fu);
            tvPart2.setText(getString(R.string.body_part_part_3));
            part2.setTag(PART_CODE3);
            ivPart3.setImageResource(R.mipmap.body_part_female_bi);
            tvPart3.setText(getString(R.string.body_part_part_4));
            part3.setTag(PART_CODE4);
            ivPart4.setImageResource(R.mipmap.body_part_female_tun);
            tvPart4.setText(getString(R.string.body_part_part_6));
            part4.setTag(PART_CODE6);
            ivPart5.setImageResource(R.mipmap.body_part_female_xiong);
            tvPart5.setText(getString(R.string.body_part_part_1));
            part5.setTag(PART_CODE1);
            ivPart6.setImageResource(R.mipmap.body_part_female_bei);
            tvPart6.setText(getString(R.string.body_part_part_2));
            part6.setTag(PART_CODE2);
        }

        if (selectedParts == null){
            selectedParts = new ArrayList<>();
        }
        selectedParts.clear();
        if (map.containsKey(PARAM_BODY_PART)) {
            int selected = (int) map.get(PARAM_BODY_PART);
            if ((selected & (int)part1.getTag()) > 0) {
                onClick(part1);
            }
            if ((selected & (int)part2.getTag()) > 0) {
                onClick(part2);
            }
            if ((selected & (int)part3.getTag()) > 0) {
                onClick(part3);
            }
            if ((selected & (int)part4.getTag()) > 0) {
                onClick(part4);
            }
            if ((selected & (int)part5.getTag()) > 0) {
                onClick(part5);
            }
            if ((selected & (int)part6.getTag()) > 0) {
                onClick(part6);
            }
        }
        return rootView;
    }

    private void preStartNewStage(){
        HashMap<String, Object> map = ParamHelper.acceptParams(BaseMainActivity.class, false);
        String stature = String.valueOf(map.get(BirthAndWeightFragment.PARAM_HEIGHT));
        String mass = String.valueOf(map.get(BirthAndWeightFragment.PARAM_WEIGHT));
        String subject = String.valueOf(map.get(SelectTargetFragment.PARAM_TARGET));
        String days = String.valueOf(map.get(SelectDurationFragment.PARAM_DURATION));
        String part = String.valueOf(map.get(SelectBodyPartFragment.PARAM_BODY_PART));
        String losefat = String.valueOf(map.get(SelectDurationFragment.PARAM_LOSE_WEIGHT));
        String birthday = "";
        if (map.get(BirthAndWeightFragment.PARAM_BIRTH) != null) {
            Calendar birthCal = (Calendar) map.get(BirthAndWeightFragment.PARAM_BIRTH);
            Date birthDate = new Date(birthCal.getTimeInMillis());
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            birthday = sf.format(birthDate);
        }
        if (birthday == null) {
            APPUtil.showToast(getApplicationContext(), "请填写出生日期");
            return;
        }
        if (stature == null) {
            APPUtil.showToast(getApplicationContext(), "请填写身高");
            return;
        }
        if (mass == null) {
            APPUtil.showToast(getApplicationContext(), "请填写体重");
            return;
        }
        if (subject == null) {
            APPUtil.showToast(getApplicationContext(), "请选择锻炼类型");
            return;
        }
        if (days == null) {
            APPUtil.showToast(getApplicationContext(), "请选择锻炼周期");
            return;
        }
        if (part == null) {
            APPUtil.showToast(getApplicationContext(), "请选择锻炼部位");
            return;
        }
        if (subject.equals("1") && losefat == null) {
            APPUtil.showToast(getApplicationContext(), "请选择减肥重量");
            return;
        }

        String token = SharedPreferencesUtil.getUserToken();

        startNewStage(birthday, stature, mass, subject, days, part, losefat ,token);
    }

    private void startNewStage(String birthday, String stature, String mass, String subject, String days, String part, String losefat , final String token){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("birthday", birthday);
        params.put("stature", stature);
        params.put("mass", mass);
        params.put("subject", subject);
        params.put("days", days);
        params.put("part", part);
        params.put("losefat", losefat);
        params.put("token", token);
        WebRequest request = new WebRequest(WebService.ACCOUNT_LEVEL_RESET_URL, params, BaseBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                        cancelLoadingDialog();
                        if (response.isSuccess()) {
                            Intent intent = new Intent(getActivity(), LevelSelectActivity.class);
                            intent.putExtra(LevelSelectActivity.COME_IN_DONE_KEY, true);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            APPUtil.showToast(getApplicationContext(), response.getMsg());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelLoadingDialog();
                        APPUtil.showToast(getApplicationContext(), error.getMessage());
                    }

                    @Override
                    public void onStart() {
                        if (isActivityDestroyed()) {
                            return;
                        }
                    }

                });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (selectedParts.size() == 0) {
                    APPUtil.showToast(getActivity(), "你还没选呢");
                    return;
                }
                int selected = 0;
                for (int code : selectedParts) {
                    //BBLog call replaced
                    selected = selected | code;
                }
                HashMap<String, Object> map = ParamHelper.acquireParamsReceiver(BaseMainActivity.class);
                map.put(PARAM_BODY_PART, selected);

                addActionEvent(ActionWebService.PARAMS_V1,String.valueOf(selected));

                if(SharedPreferencesUtil.getAddNewLevelType() != 0){
                    preStartNewStage();
                    SharedPreferencesUtil.setAddNewLevelType(0);
                    return;
                }

                if (SharedPreferencesUtil.getUserToken() != null) {
                    sendBroadcast(new Intent(Constants.RECEIVER_FULL_USER_INFO));
                } else {
                    if (getActivity() != null) {
                        ((BaseMainActivity) getActivity()).goToPage(BaseMainActivity.PAGE_REGISTER);
                    }
                }
                break;
            case R.id.body_part1:
            case R.id.body_part2:
            case R.id.body_part3:
            case R.id.body_part4:
            case R.id.body_part5:
            case R.id.body_part6:
                int partCode = (int) v.getTag();
                if (selectedParts.contains(partCode)) {
                    selectedParts.remove((Integer) partCode);
                } else {
                    selectedParts.add(partCode);
                }
                v.setSelected(!v.isSelected());
                btnNext.setEnabled(!selectedParts.isEmpty());
                break;

        }
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_BODY_PART;
    }
}
