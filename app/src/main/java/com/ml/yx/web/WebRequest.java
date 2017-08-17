package com.ml.yx.web;

import android.content.Context;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonParseException;
import com.ml.yx.R;
import com.ml.yx.YouXinApplication;
import com.ml.yx.activity.work.LevelSelectActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.AppInfoUtils;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.DateUtil;
import com.ml.yx.location.BBLocation;
import com.ml.yx.model.BaseBean;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Author:Lijj
 * @Todo:TODO
 */

@SuppressWarnings("rawtypes")
public class WebRequest extends Request<BaseBean> {
    public static final String TAG = "yx_req";

    private static String userToken = null;

    private Map<String, String> reqMap;
    private FileParam fileParam;
    private Class clas;

    public static void setToken(String token) {
        userToken = token;
    }

    private Map<String, String> addSignParam(Map<String, String> params) {
        params.put("unixTime",String.valueOf(System.currentTimeMillis()));

        List<String> keyList = new ArrayList<String>(params.keySet());
        Collections.sort(keyList , new Comparator<String>() {

            @Override
            public int compare(String lhs, String rhs) {
                if(lhs == null){
                    return -1;
                }
                if(rhs == null){
                    return 1;
                }
                return lhs.compareTo(rhs);
            }
        });
        StringBuilder sb = new StringBuilder();
        for(String key : keyList){
            String value = params.get(key);
            sb.append(key).append("=").append(value).append("&");
        }
        String sign = sb.toString();
        sign = sign.substring(0,sign.length() - 1);
        //BBLog call replaced

        return params;
    }

    private Map<String, String> commonParams(Map<String, String> params) {
        Context context = YouXinApplication.getInstance();
        params.put("platform", "0");
        params.put("deviceId", AppInfoUtils.getMobileDeviceInfo(context));
        params.put("version", String.valueOf(AppInfoUtils.getCurrentVersionCode(context)));
        if (userToken != null) {
            params.put("token", userToken);
        }
		params.put("channel", AppInfoUtils.getChannelByMeta(context));
		params.put("model", APPUtil.getPhoneType());
		params.put("os", android.os.Build.VERSION.RELEASE);
        BBLocation location = BBLocation.getLocation(YouXinApplication.getInstance());
        if(location != null && location.getCityName() != null) {
            params.put("city", location.getCityName());
        }

//        addSignParam(params);

        return params;
    }

    @SuppressWarnings("unchecked")
    public WebRequest(String url, Map<String, String> params, Class clas, Listener listener) {
        super(Method.POST, url, listener);
        if (Constants.IS_DEBUG) {
            printRequestParams(params, null);
        }
        reqMap = commonParams(params);
        this.clas = clas;
    }

    @SuppressWarnings("unchecked")
    public WebRequest(String url, int requestType, Map<String, String> params, Class clas, Listener listener) {
        super(requestType, url, listener);
        if (Constants.IS_DEBUG) {
            printRequestParams(params, null);
        }

        reqMap = commonParams(params);
        this.clas = clas;
    }

    public WebRequest(String url, Class clas, Listener listener) {
        this(url, new HashMap<String, String>(), clas, listener);
    }

    public WebRequest(String url, HashMap<String, String> params, FileParam fileParam, Class clas, Listener<BaseBean> listener) {
        super(Method.POST_FILE, url, listener);
        if (Constants.IS_DEBUG) {
            printRequestParams(params, fileParam);
        }

        reqMap = commonParams(params);
        this.fileParam = fileParam;
        this.clas = clas;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> customizedHeader = new HashMap<String, String>();
        customizedHeader.put("Accept-Encoding", "gzip");
        customizedHeader.put("Connection", "Keep-Alive");
        return customizedHeader;
    }

    private void printRequestParams(Map<String, String> params, FileParam fileParam) {
        if (params == null) {
            return;
        }
        StringBuffer strParams = new StringBuffer();
        for (Entry<String, String> entry : params.entrySet()) {
            strParams.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }

        if (fileParam != null) {
            strParams.append("file=").append(fileParam.getFilePath());
        }

        //BBLog call replaced
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = super.getBody();

        if (Constants.IS_DEBUG && body != null) {
            //BBLog call replaced
        }
        return body;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response<BaseBean> parseNetworkResponse(NetworkResponse response) {
        try {
            BaseBean bean = null;
            if (response.data != null) {
                long time = HttpHeaderParser.getHeaderDate(response.headers);
                if (time != 0) {
                    DateUtil.setWenbaTime(time);
                }

                String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                //BBLog call replaced

                if (responseString != null && !"".equals(responseString)) {
                    bean = (BaseBean) JsonToBeanHandler.getInstance().fromJsonString(responseString, clas);
                }

                Context context = YouXinApplication.getInstance();

                if (bean != null && bean.getStatus() == 1000) {
                    if (context != null) {
                        Intent intent = new Intent(context, LevelSelectActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(LevelSelectActivity.USER_LOGOUT, true);
                        context.startActivity(intent);
                    }
                }
            }
            return Response.success(bean, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonParseException e) {
            e.printStackTrace();
            return Response.error(new ParseError(APPUtil.getString(R.string.error_response_data)));
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return Response.error(new ParseError(APPUtil.getString(R.string.error_wenba)));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(APPUtil.getString(R.string.error_wenba)));
        }
    }


    public Map<String, String> getParams() throws AuthFailureError {
        return reqMap;
    }

    public FileParam getFileParam() {
        return fileParam;
    }

    @Override
    public void deliverResponse(BaseBean response) {
        mListener.onResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        Throwable throwable = volleyError.getCause();
        if (throwable instanceof ConnectTimeoutException) {
            return new VolleyError(APPUtil.getString(R.string.error_network), throwable);
        } else if (throwable instanceof UnknownHostException) {
            return new VolleyError(APPUtil.getString(R.string.error_network), throwable);
        } else if (throwable instanceof IOException) {
            return new VolleyError(APPUtil.getString(R.string.error_wenba), throwable);
        }
        return super.parseNetworkError(volleyError);
    }

}
