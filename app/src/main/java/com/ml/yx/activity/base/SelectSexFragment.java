package com.ml.yx.activity.base;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ml.yx.R;
import com.ml.yx.activity.BaseFragment;
import com.ml.yx.comm.ParamHelper;

import java.util.HashMap;

/**
 * Created by BTyang on 16/4/9.
 */
public class SelectSexFragment extends BaseFragment implements View.OnClickListener {

    public final static String PARAM_SEX = "sex";

    public final static int SEX_MALE = 1;
    public final static int SEX_FEMALE = 0;
    private View maleView,femaleView;

    public SelectSexFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_sex, container, false);
        maleView = rootView.findViewById(R.id.male_layout);
        maleView.setOnClickListener(this);
        femaleView = rootView.findViewById(R.id.female_layout);
        femaleView.setOnClickListener(this);

        HashMap<String, Object> map =  ParamHelper.acquireParamsReceiver(BaseMainActivity.class);
        if(map != null){
            Object obj = map.get(PARAM_SEX);
            if(obj != null){
                int sex = (int) obj;
                if(sex == SEX_MALE){
                    maleView.setSelected(true);
                }else if(sex == SEX_FEMALE){
                    femaleView.setSelected(true);
                }
            }
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int sex = SEX_MALE;
        switch (v.getId()){
            case R.id.male_layout:
                sex = SEX_MALE;
                break;
            case R.id.female_layout:
                sex = SEX_FEMALE;
                break;
            default:
                break;
        }
        HashMap<String, Object> map =  ParamHelper.acquireParamsReceiver(BaseMainActivity.class);
        map.put(PARAM_SEX,sex);
        if (getActivity() != null){
            ((BaseMainActivity)getActivity()).goToPage(BaseMainActivity.PAGE_BIRTH_WEIGHT);
        }
    }

    @Override
    protected String getPageCode() {
        return null;
    }
}
