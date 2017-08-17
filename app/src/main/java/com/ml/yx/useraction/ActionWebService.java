package com.ml.yx.useraction;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.YouXinApplication;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.location.BBLocation;
import com.ml.yx.model.BaseBean;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author:Lijj
 * @Todo:TODO
 */
public class ActionWebService {

    private static final String TEST_URL = "http://test-tongji.u-thin.com/";// 开发环境
    private static final String PRODUCT_URL = "http://tongji.u-thin.com/";// 开发环境
    private static final String BASE_URL = Constants.IS_TEST ? TEST_URL : PRODUCT_URL;
    public static final String USER_ACTION_URL = BASE_URL + "userAction";
    public static final String BOOT_URL = BASE_URL + "boot";

    public static final String EVENT_USER = "1000028";
    public static final String EVENT_COACH = "100002";
    public static final String EVENT_TARGET = "100003";
    public static final String EVENT_BASE_INFO = "100005";
    public static final String EVENT_BODY_PART = "100006";
    public static final String EVENT_DURATION = "100007";
    public static final String EVENT_REGISTER = "100008";
    public static final String EVENT_LOGIN = "100009";
    public static final String EVENT_LEVEL_SELECT = "1000021";
    public static final String EVENT_LEVEL_PRE = "1000022";
    public static final String EVENT_LEVEL_DONE = "1000023";
    public static final String EVENT_LEVEL_RESULT = "1000024";
    public static final String EVENT_LEVEL_STAGE_DONE = "1000025";



    public static final String PARAMS_EVENTID = "eventId";
    public static final String PARAMS_V70 = "v70";
    public static final String PARAMS_V71 = "v71";
    public static final String PARAMS_V1 = "v1";
    public static final String PARAMS_V2 = "v2";
    public static final String PARAMS_V3 = "v3";
    public static final String PARAMS_V4 = "v4";
    public static final String PARAMS_V5 = "v5";
    public static final String PARAMS_V6 = "v6";
    public static final String PARAMS_V7 = "v7";


    public static void requestAction(Context context,String url, Map<String,String> params){
        HashMap<String, String> requestParams = new HashMap<String, String>();
        if(params != null){
            Set<Map.Entry<String,String>> sets = params.entrySet();
            for(Map.Entry<String,String> entry : sets){
                if(entry.getKey() != null && entry.getValue() != null){
                    requestParams.put(entry.getKey(),entry.getValue());
                }
            }
        }

        BBLocation location = BBLocation.getLocation(YouXinApplication.getInstance());
        if(location != null) {
            if(location.getCityName() != null) {
                requestParams.put("province", location.getCityName());
            }
            if(location.getLongitude() != 0) {
                requestParams.put("longitude", String.valueOf(location.getLongitude()));
            }
            if(location.getLatitude() != 0) {
                requestParams.put("longitude", String.valueOf(location.getLatitude()));
            }
        }

        String uid = SharedPreferencesUtil.getUserId();
        if(!TextUtils.isEmpty(uid)){
            requestParams.put("uid", uid);
        }

        WebRequest request = new WebRequest(url, requestParams, BaseBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                    @Override
                    public void onStart() {
                    }

                });
        WebDataLoader.getInstance(context).startHttpLoader(request);
    }

}
