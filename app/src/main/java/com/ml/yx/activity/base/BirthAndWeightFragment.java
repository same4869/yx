package com.ml.yx.activity.base;


import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.ml.yx.R;
import com.ml.yx.activity.BaseFragment;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.ParamHelper;
import com.ml.yx.comm.StringUtil;
import com.ml.yx.useraction.ActionWebService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by BTyang on 16/4/9.
 */
public class BirthAndWeightFragment extends BaseFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener,TextWatcher {

    private final String tag = BirthAndWeightFragment.class.getSimpleName();

    public final static String PARAM_BIRTH = "birth";
    public final static String PARAM_HEIGHT = "height";
    public final static String PARAM_WEIGHT = "weight";

    private TextView tvYear, tvMonth, tvDay;
    private EditText edtHeight, edtWeight;
    private Calendar birthDate;
    private Button btnNext;

    public BirthAndWeightFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_birth_and_weight, container, false);
        btnNext = (Button) rootView.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        tvYear = (TextView) rootView.findViewById(R.id.tv_year);
        tvYear.setOnClickListener(this);
        tvMonth = (TextView) rootView.findViewById(R.id.tv_month);
        tvMonth.setOnClickListener(this);
        tvDay = (TextView) rootView.findViewById(R.id.tv_day);
        tvDay.setOnClickListener(this);
        edtHeight = (EditText) rootView.findViewById(R.id.edt_height);
        edtWeight = (EditText) rootView.findViewById(R.id.edt_weight);
        edtHeight.addTextChangedListener(this);
        edtWeight.addTextChangedListener(this);
        HashMap<String, Object> map = ParamHelper.acceptParams(BaseMainActivity.class, false);
        String stature = String.valueOf(map.get(BirthAndWeightFragment.PARAM_HEIGHT));
        String mass = String.valueOf(map.get(BirthAndWeightFragment.PARAM_WEIGHT));
        if (map.get(BirthAndWeightFragment.PARAM_BIRTH) != null) {
            birthDate = (Calendar) map.get(BirthAndWeightFragment.PARAM_BIRTH);
        }
        if (birthDate != null) {
            tvYear.setText(String.valueOf(birthDate.get(Calendar.YEAR)));
            tvMonth.setText(String.valueOf(birthDate.get(Calendar.MONTH) + 1));
            tvDay.setText(String.valueOf(birthDate.get(Calendar.DAY_OF_MONTH)));
        }
        if (StringUtil.isNotBlank(stature)) {
            edtHeight.setText(stature);
        }
        if (StringUtil.isNotBlank(mass)) {
            edtWeight.setText(mass);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                String height = edtHeight.getText().toString();
                String weight = edtWeight.getText().toString();
                HashMap<String, Object> map = ParamHelper.acquireParamsReceiver(BaseMainActivity.class);
//                if (birthDate == null) {
//                    APPUtil.showToast(getActivity(),getString(R.string.birth_null_error));
//                    return;
//                }
//                if (StringUtil.isBlank(height)) {
//                    APPUtil.showToast(getActivity(),getString(R.string.height_null_error));
//                    return;
//                }
//                if (StringUtil.isBlank(weight)) {
//                    APPUtil.showToast(getActivity(),getString(R.string.weight_null_error));
//                    return;
//                }
                int heightValue = Integer.valueOf(height);
                if (heightValue < 140 || heightValue > 210) {
                    APPUtil.showToast(getActivity(),getString(R.string.height_beyond_error));
                    return;
                }
                int weightValue = Integer.valueOf(weight);
                if (weightValue < 40 || weightValue > 150) {
                    APPUtil.showToast(getActivity(),getString(R.string.weight_beyond_error));
                    return;
                }
                map.put(PARAM_BIRTH, birthDate);
                try {
                    map.put(PARAM_HEIGHT, heightValue);
                    map.put(PARAM_WEIGHT, weightValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity() != null) {
                    ((BaseMainActivity) getActivity()).goToPage(BaseMainActivity.PAGE_SELECT_DURATION);
                }
                break;
            case R.id.tv_year:
            case R.id.tv_month:
            case R.id.tv_day:
                int defaultYear = 1990;
                int defaultMonth = 0;
                int defaultDay = 1;
                if (birthDate != null) {
                    defaultYear = birthDate.get(Calendar.YEAR);
                    defaultMonth = birthDate.get(Calendar.MONTH);
                    defaultDay = birthDate.get(Calendar.DAY_OF_MONTH);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, defaultYear, defaultMonth, defaultDay);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Calendar tempDate = Calendar.getInstance(Locale.CHINA);
                    tempDate.set(1950, 0, 1);
                    datePickerDialog.getDatePicker().setMinDate(tempDate.getTimeInMillis());
                    tempDate.set(2009, 11, 31);
                    datePickerDialog.getDatePicker().setMaxDate(tempDate.getTimeInMillis());
                }
                datePickerDialog.show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        tvYear.setText(String.valueOf(year));
        tvMonth.setText(String.valueOf(monthOfYear + 1));
        tvDay.setText(String.valueOf(dayOfMonth));
        birthDate = Calendar.getInstance(Locale.CHINA);
        birthDate.set(year, monthOfYear, dayOfMonth);
        checkInput();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkInput();
    }

    private void checkInput(){
        String height = edtHeight.getText().toString();
        String weight = edtWeight.getText().toString();
        btnNext.setEnabled(birthDate != null && StringUtil.isNotBlank(height) && StringUtil.isNotBlank(weight));
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_BASE_INFO;
    }
}
