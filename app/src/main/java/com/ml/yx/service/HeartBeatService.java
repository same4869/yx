package com.ml.yx.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.ml.yx.R;
import com.ml.yx.activity.CommonInvokerActivity;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.BBLog;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.DateUtil;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.db.MessageDBHelper;
import com.ml.yx.event.TitleBarRedPointEvent;
import com.ml.yx.model.BaseBean;
import com.ml.yx.model.MessageBean;
import com.ml.yx.model.MessageListBean;
import com.ml.yx.web.WebDataLoader;
import com.ml.yx.web.WebRequest;
import com.ml.yx.web.WebService;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

@SuppressLint({"UseSparseArrays", "SimpleDateFormat", "HandlerLeak"})
public class HeartBeatService extends BaseSerivce {
    private static final String tag = HeartBeatService.class.getSimpleName();

    private static final long commSleep = 10000L;

    private static final int REQUEST_MESSAGE_COUNT = 30;// 5分钟

    private int appsCount = 0;

    private boolean lastStatus = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private HearBeatTask hearBeatTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class HearBeatTask extends Thread {
        private boolean isRunning = false;

        public HearBeatTask(){
        }

        @Override
        public void run() {
            while (isRunning) {
                commAccess();
                try {
                    Thread.sleep(commSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void startTask() {
            if (isRunning) {
                return;
            }
            isRunning = true;
            this.start();
        }

        public void stopTask() {
            isRunning = false;
        }
    }

    public void onCreate() {
        super.onCreate();

        if (hearBeatTask == null) {
            hearBeatTask = new HearBeatTask();
        }
        hearBeatTask.startTask();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public boolean commAccess() {
        boolean isBackground = isRunningBackground();

        if (!isBackground) {
            //BBLog call replaced
            if (lastStatus != isBackground) {

            }
        } else {
            //BBLog call replaced
            if (lastStatus != isBackground) {

            }
        }

        appsCount++;

        if (appsCount >= REQUEST_MESSAGE_COUNT) {
            appsCount = 0;
            if (SharedPreferencesUtil.getMessageLastUpdateTime() == 0) {
                requestMessages();
            } else {
                checkNewMessage();
            }
        }

        lastStatus = isBackground;

        return true;
    }

    private boolean isRunningBackground() {
        String topAPP = APPUtil.getTopApplication(getApplicationContext());
        //BBLog call replaced
        return !getPackageName().equals(topAPP);
    }

    public void onDestroy() {
        super.onDestroy();
        //BBLog call replaced
        if (hearBeatTask != null) {
            hearBeatTask.stopTask();
            hearBeatTask = null;
        }
    }

    public void requestMessages() {
        HashMap<String, String> params = new HashMap<>();
        params.put("msg_id", "0");
        WebRequest request = new WebRequest(WebService.ACCOUNT_QA_LIST_URL, params, MessageListBean.class, new Listener<BaseBean>() {

            @Override
            public void onResponse(BaseBean response) {
                if (response != null) {
                    if (response.isSuccess()) {
                        SharedPreferencesUtil.setMessageLastUpdateTime(DateUtil.getCurWenbaTime() / 1000);
                        MessageListBean messageListBean = (MessageListBean) response;
                        List<MessageBean> messageList = messageListBean.getList();

                        MessageDBHelper dbHelper = MessageDBHelper.getInstance();
                        dbHelper.deleteAll();
                        if (messageList != null && messageList.size() > 0) {
                            for (MessageBean message : messageList) {
                                dbHelper.save(message);
                            }
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onStart() {
            }

        });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    public void checkNewMessage() {
        HashMap<String, String> params = new HashMap<>();
        params.put("last_update_time", String.valueOf(SharedPreferencesUtil.getMessageLastUpdateTime()));
        WebRequest request = new WebRequest(WebService.ACCOUNT_QA_CHECK_NEW_URL, params, MessageListBean.class, new Listener<BaseBean>() {

            @Override
            public void onResponse(BaseBean response) {
                if (response != null) {
                    if (response.isSuccess()) {
                        SharedPreferencesUtil.setMessageLastUpdateTime(DateUtil.getCurWenbaTime() / 1000);
                        MessageListBean messageListBean = (MessageListBean) response;
                        List<MessageBean> messageList = messageListBean.getList();

                        MessageDBHelper dbHelper = MessageDBHelper.getInstance();
                        if (messageList != null && messageList.size() > 0) {
                            if (isRunningBackground()) {
                                APPUtil.generateNotification(getApplicationContext(), APPUtil.getString(R.string.app_name), getString(R.string.new_message_notification_text), new Intent(
                                        Intent.ACTION_VIEW, Uri.parse(Constants.SCHEME_VIEW + CommonInvokerActivity.SCHEME_MESSAGE)), 1, Notification.FLAG_AUTO_CANCEL, true);
                            } else {
                                SharedPreferencesUtil.setNewMessage(true);
                                EventBus.getDefault().post(new TitleBarRedPointEvent(true));
                            }
                            for (MessageBean message : messageList) {
                                MessageBean oldMessage = dbHelper.find(message.getMsg_id());
                                if (oldMessage == null) {
                                    //新消息,设置问题为未读并存入数据库
                                    message.setQuestionRead(false);
                                    dbHelper.save(message);
                                } else {
                                    //有新回复,设置回复为未读并更新数据库
                                    message.setAnswerRead(false);
                                    dbHelper.update(message);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onStart() {
            }

        });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

}
