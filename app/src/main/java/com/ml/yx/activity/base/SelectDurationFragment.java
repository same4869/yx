package com.ml.yx.activity.base;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ml.yx.R;
import com.ml.yx.activity.BaseFragment;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.DateUtil;
import com.ml.yx.comm.ParamHelper;
import com.ml.yx.useraction.ActionWebService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by BTyang on 16/4/11.
 */
public class SelectDurationFragment extends BaseFragment implements View.OnClickListener {

    private final String tag = SelectDurationFragment.class.getSimpleName();
    public final static String PARAM_DURATION = "duration";
    public final static String PARAM_LOSE_WEIGHT = "lose_weight";
    public final static String PARAM_LOSE_WEIGHT_POSITION = "lose_weight_position";

    private TextView tvDuration7, tvDuration14, tvDuration21, tvDuration28;
    private TextView tvWeight1, tvWeight2, tvWeight3, tvWeight4;
    private View weightLayout, btnNext;
    private int trainingTarget = SelectTargetFragment.TARGET_JIANZI;
    private int selectedDuration = 0;
    private int selectedWeight = 0;
    private int loseWeightPosition = 0;
    private int bodyFat;
    private int sex;

    public SelectDurationFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_select_druation, container, false);
        tvDuration7 = (TextView) rootView.findViewById(R.id.tv_duration_7);
        tvDuration14 = (TextView) rootView.findViewById(R.id.tv_duration_14);
        tvDuration21 = (TextView) rootView.findViewById(R.id.tv_duration_21);
        tvDuration28 = (TextView) rootView.findViewById(R.id.tv_duration_28);
        tvWeight1 = (TextView) rootView.findViewById(R.id.tv_weight_1);
        tvWeight2 = (TextView) rootView.findViewById(R.id.tv_weight_2);
        tvWeight3 = (TextView) rootView.findViewById(R.id.tv_weight_3);
        tvWeight4 = (TextView) rootView.findViewById(R.id.tv_weight_4);
        weightLayout = rootView.findViewById(R.id.lose_weight_layout);
        btnNext = rootView.findViewById(R.id.btn_next);
        tvDuration7.setOnClickListener(this);
        tvDuration14.setOnClickListener(this);
        tvDuration21.setOnClickListener(this);
        tvDuration28.setOnClickListener(this);
        tvWeight1.setOnClickListener(this);
        tvWeight2.setOnClickListener(this);
        tvWeight3.setOnClickListener(this);
        tvWeight4.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        HashMap<String, Object> map = ParamHelper.acceptParams(BaseMainActivity.class, false);
        sex = SelectSexFragment.SEX_FEMALE;
        try {
            trainingTarget = (int) map.get(SelectTargetFragment.PARAM_TARGET);
            int height = (int) map.get(BirthAndWeightFragment.PARAM_HEIGHT);
            int weight = (int) map.get(BirthAndWeightFragment.PARAM_WEIGHT);
            sex = (int) map.get(SelectSexFragment.PARAM_SEX);
            Calendar birthCal = null;
            if (map.get(BirthAndWeightFragment.PARAM_BIRTH) != null) {
                birthCal = (Calendar) map.get(BirthAndWeightFragment.PARAM_BIRTH);
            }
            bodyFat = getBodyFatRate(birthCal, height, weight, sex);
            //BBLog call replaced
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (map.containsKey(PARAM_DURATION)) {
            selectedDuration = (int) map.get(PARAM_DURATION);
            View selectedDurationView = null;
            if (selectedDuration == 7) {
                selectedDurationView = tvDuration7;
            } else if (selectedDuration == 14) {
                selectedDurationView = tvDuration14;
            } else if (selectedDuration == 21) {
                selectedDurationView = tvDuration21;
            } else if (selectedDuration == 28) {
                selectedDurationView = tvDuration28;
            }
            if (selectedDurationView != null) {
                selectedDurationView.setSelected(true);
                selectedDuration = Integer.parseInt((String) selectedDurationView.getTag());
            }
            if (trainingTarget == SelectTargetFragment.TARGET_JIANZI && map.containsKey(PARAM_LOSE_WEIGHT_POSITION)) {
                weightLayout.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
                if (selectedDurationView != null) {
                    onClick(selectedDurationView);
                }
                int loseWeightPosition = (int) map.get(PARAM_LOSE_WEIGHT_POSITION);
                if (loseWeightPosition == 1) {
                    onClick(tvWeight1);
                } else if (loseWeightPosition == 2) {
                    onClick(tvWeight2);
                } else if (loseWeightPosition == 3) {
                    onClick(tvWeight3);
                } else if (loseWeightPosition == 4) {
                    onClick(tvWeight4);
                }
            }
        }

        return rootView;
    }

    private int getBodyFatRate(Calendar birth, int height, int weight, int sex) {
        if (birth == null || height == 0 || weight == 0) {
            return 0;
        }
        Calendar curDate = Calendar.getInstance(Locale.CHINA);
        curDate.setTimeInMillis(DateUtil.getCurWenbaTime());
        int age = curDate.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        float bodyFat = 1.2f * weight / (height * height / 10000f) + 0.23f * age - 5.4f - 10.8f * sex;
        //BBLog call replaced
        return (int)Math.ceil(bodyFat);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_next) {
            if (selectedDuration == 0 || selectedWeight == 0) {
                APPUtil.showToast(getActivity(), getResources().getString(R.string.duration_unselected_error));
                return;
            }
        } else {
            int position = 0;
            switch (v.getId()) {
                case R.id.tv_duration_7:
                case R.id.tv_duration_14:
                case R.id.tv_duration_21:
                case R.id.tv_duration_28:
                    tvDuration7.setSelected(false);
                    tvDuration14.setSelected(false);
                    tvDuration21.setSelected(false);
                    tvDuration28.setSelected(false);
                    v.setSelected(true);
                    selectedDuration = Integer.parseInt((String) v.getTag());
                    if (trainingTarget == SelectTargetFragment.TARGET_JIANZI) {
                        weightLayout.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.VISIBLE);
                        refreshWeightView(selectedDuration);
                        showBubbleText(R.string.duration_tip2);
                        return;
                    }
                    break;
                case R.id.tv_weight_4:
                    position++;
                case R.id.tv_weight_3:
                    position++;
                case R.id.tv_weight_2:
                    position++;
                case R.id.tv_weight_1:
                    position++;
                    if ((boolean) v.getTag(R.id.lose_weight_layout)) {
                        loseWeightPosition = position;
                        tvWeight1.setSelected(false);
                        tvWeight2.setSelected(false);
                        tvWeight3.setSelected(false);
                        tvWeight4.setSelected(false);
                        v.setSelected(true);
                        selectedWeight = (int) v.getTag();
                        showBubbleText(R.string.duration_tip2);
                    } else {
                        showBubbleText(R.string.duration_tip3);
                       // APPUtil.showToast(getActivity(), getString(R.string.duration_weight_beyond_error));
                    }
                    return;
            }
        }
        HashMap<String, Object> map = ParamHelper.acquireParamsReceiver(BaseMainActivity.class);
        map.put(PARAM_DURATION, selectedDuration);
        map.put(PARAM_LOSE_WEIGHT, selectedWeight);
        //因为减重选项可变,根据选择的减重目标定位到哪个选项比较繁琐,故记录当前选择的减重目标position,以供回填.
        if (loseWeightPosition > 0) {
            map.put(PARAM_LOSE_WEIGHT_POSITION, loseWeightPosition);
        }
        if (getActivity() != null) {
            ((BaseMainActivity) getActivity()).goToPage(BaseMainActivity.PAGE_SELECT_BODY_PART);
        }
    }

    private void refreshWeightView(int days) {
        String[] weightNames = getResources().getStringArray(R.array.weight_name);
        int[] weightCodes = getResources().getIntArray(R.array.weight_code);
        int level1;
        int level2;
        int level3;
        //体脂标准
        if (sex == SelectSexFragment.SEX_MALE) {
            level1 = 13;
            level2 = 18;
            level3 = 25;
        } else {
            level1 = 20;
            level2 = 25;
            level3 = 32;
        }
        enableWeight(tvWeight1, false);
        enableWeight(tvWeight2, false);
        enableWeight(tvWeight3, false);
        enableWeight(tvWeight4, false);
        if (bodyFat >= level3 && days == 28) {
            tvWeight1.setText(weightNames[1]);
            tvWeight2.setText(weightNames[2]);
            tvWeight3.setText(weightNames[3]);
            tvWeight4.setText(weightNames[4]);
            tvWeight1.setTag(weightCodes[1]);
            tvWeight2.setTag(weightCodes[2]);
            tvWeight3.setTag(weightCodes[3]);
            tvWeight4.setTag(weightCodes[4]);
        } else {
            tvWeight1.setText(weightNames[0]);
            tvWeight2.setText(weightNames[1]);
            tvWeight3.setText(weightNames[2]);
            tvWeight4.setText(weightNames[3]);
            tvWeight1.setTag(weightCodes[0]);
            tvWeight2.setTag(weightCodes[1]);
            tvWeight3.setTag(weightCodes[2]);
            tvWeight4.setTag(weightCodes[3]);
        }
        if (bodyFat < level1) {
            enableWeight(tvWeight1, true);
        } else if (bodyFat < level2) {
            enableWeight(tvWeight1, true);
            if (days > 14) {
                enableWeight(tvWeight2, true);
            }
        } else if (bodyFat < level3) {
            enableWeight(tvWeight1, true);
            if (days > 7) {
                enableWeight(tvWeight2, true);
            }
            if (days > 21) {
                enableWeight(tvWeight3, true);
            }
        } else {
            enableWeight(tvWeight1, true);
            if (days > 7) {
                enableWeight(tvWeight2, true);
                enableWeight(tvWeight3, true);
            }
            if (days > 14) {
                enableWeight(tvWeight3, true);
                enableWeight(tvWeight4, true);
            }
        }
        onClick(tvWeight1);
    }

    private void enableWeight(TextView view, boolean enabled) {
        if (enabled) {
            view.setBackgroundResource(R.drawable.comm_square_selector);
            view.setTextColor(getResources().getColor(R.color.white));
            view.setTag(R.id.lose_weight_layout, true);
        } else {
            view.setBackgroundResource(R.drawable.comm_square_bg);
            view.setTextColor(getResources().getColor(R.color.text_color_gray));
            view.setTag(R.id.lose_weight_layout, false);
        }
    }

    private void showBubbleText(int resId){
        String text = getString(resId);
        if (getActivity() != null) {
            ((BaseMainActivity) getActivity()).setTextTitle(text);
        }
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_DURATION;
    }

}
