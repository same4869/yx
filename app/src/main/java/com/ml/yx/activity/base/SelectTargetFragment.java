package com.ml.yx.activity.base;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ml.yx.R;
import com.ml.yx.activity.BaseFragment;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.ParamHelper;
import com.ml.yx.comm.ScreenUtils;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.useraction.ActionWebService;

import java.util.HashMap;

/**
 * Created by BTyang on 16/4/9.
 */
public class SelectTargetFragment extends BaseFragment implements View.OnClickListener {

    public final static String PARAM_TARGET = "target";
    //锻炼目标
    public final static int TARGET_JIANZI = 1;
    public final static int TARGET_SUXING = 2;
    public final static int TARGET_ZENGJI = 3;

    private View tvJianzi, tvSuxing, tvZengji;

    public SelectTargetFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_target, container, false);
        tvJianzi = rootView.findViewById(R.id.jianzhi_layout);
        tvSuxing = rootView.findViewById(R.id.suxing_layout);
        tvZengji = rootView.findViewById(R.id.zengji_layout);
        tvJianzi.setOnClickListener(this);
        tvSuxing.setOnClickListener(this);
        tvZengji.setOnClickListener(this);
        Drawable drawable = getResources().getDrawable(R.mipmap.jianzhi);
        int viewWidth = ScreenUtils.getScreenWidth(getApplicationContext()) - APPUtil.dpToPx(getApplicationContext(), 30);
        int viewHeight = 0;
        if (drawable.getIntrinsicWidth() != 0) {
            viewHeight = (int) (1.0f * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth() * viewWidth);
        }
        rootView.findViewById(R.id.iv_jianzhi).getLayoutParams().height = viewHeight;
        rootView.findViewById(R.id.iv_suxing).getLayoutParams().height = viewHeight;
        rootView.findViewById(R.id.iv_zengji).getLayoutParams().height = viewHeight;
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int target = TARGET_JIANZI;
        switch (v.getId()) {
            case R.id.jianzhi_layout:
                target = TARGET_JIANZI;
                break;
            case R.id.suxing_layout:
                target = TARGET_SUXING;
                break;
            case R.id.zengji_layout:
                target = TARGET_ZENGJI;
                break;
        }
        HashMap<String, Object> map = ParamHelper.acquireParamsReceiver(BaseMainActivity.class);
        map.put(PARAM_TARGET, target);
        if (getActivity() != null) {
            int nextPageType = SharedPreferencesUtil.getAddNewLevelType();
            if(nextPageType == 1){
                ((BaseMainActivity) getActivity()).goToPage(BaseMainActivity.PAGE_SELECT_DURATION);
            }else if (nextPageType == 2){
                ((BaseMainActivity) getActivity()).goToPage(BaseMainActivity.PAGE_BIRTH_WEIGHT);
            }else {
                ((BaseMainActivity) getActivity()).goToPage(BaseMainActivity.PAGE_SELECT_SEX);
            }
        }
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_TARGET;
    }
}
