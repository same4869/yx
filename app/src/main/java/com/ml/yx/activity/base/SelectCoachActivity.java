package com.ml.yx.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.adapter.CoachListAdapter;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.ParamHelper;
import com.ml.yx.model.BaseBean;
import com.ml.yx.model.CoachListBean;
import com.ml.yx.useraction.ActionWebService;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by BTyang on 16/4/9.
 */
public class SelectCoachActivity extends BaseTitleBarActivity implements AdapterView.OnItemClickListener {
    private final String tag = SelectCoachActivity.class.getSimpleName();

    public static final String UPDATE_USER = "update_user";

    public final static String PARAM_COACH = "coach";

    private CoachListAdapter mAdapter;
    private GridView coachGrid;
    private List<CoachListBean.CoachBean> coachList;

    private boolean updateUser = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.RECEIVER_START_WORK)) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_coach);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.RECEIVER_START_WORK);
        registerReceiver(mReceiver, filter);

        updateUser = getIntent().getBooleanExtra(UPDATE_USER, false);

        coachGrid = (GridView) findViewById(R.id.grid_coach);
        coachGrid.setOnItemClickListener(this);
        requestCoachList();
    }

    private void requestCoachList() {
        HashMap<String, String> params = new HashMap<String, String>();
        WebRequest request = new WebRequest(WebService.ACCOUNT_REGISTER_COACH_LIST_URL, params, CoachListBean.class,
                new Response.Listener<BaseBean>() {

                    @Override
                    public void onResponse(BaseBean response) {
                        cancelLoadingDialog();
                        CoachListBean coachListBean = (CoachListBean) response;
                        //BBLog call replaced
                        if (response.isSuccess()) {
                            coachList = coachListBean.getResponse();
                            mAdapter = new CoachListAdapter(SelectCoachActivity.this, coachList);
                            coachGrid.setAdapter(mAdapter);
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
                        showLoadingDialog();
                    }

                });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < coachList.size()) {
            CoachListBean.CoachBean coach = coachList.get(position);
            if (!coach.isUnlock()) {
                APPUtil.showToast(SelectCoachActivity.this, getString(R.string.select_coach_locked_error));
                return;
            }
            addActionEvent(ActionWebService.PARAMS_V1, String.valueOf(position + 1));
            if (updateUser) {
                requestUpdateInfo(coach.getInstructorId());
            } else {
                HashMap<String, Object> map = ParamHelper.acquireParamsReceiver(BaseMainActivity.class);
                map.put(PARAM_COACH, coach.getInstructorId());
                Intent intent = new Intent(SelectCoachActivity.this, BaseMainActivity.class);
                intent.putExtra(BaseMainActivity.TARGET_PAGE, BaseMainActivity.PAGE_SELECT_TARGET);
                intent.putExtra(BaseMainActivity.COACH_ID, coach.getInstructorId());
                startActivity(intent);
            }
        }
    }

    private void requestUpdateInfo(int instructorId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("instructorId", String.valueOf(instructorId));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected String getPageCode() {
        return ActionWebService.EVENT_COACH;
    }
}
