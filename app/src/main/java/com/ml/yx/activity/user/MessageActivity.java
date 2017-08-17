package com.ml.yx.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ml.yx.R;
import com.ml.yx.activity.BaseTitleBarActivity;
import com.ml.yx.adapter.MessageAdapter;
import com.ml.yx.comm.APPUtil;
import com.ml.yx.comm.DateUtil;
import com.ml.yx.comm.MultiThreadAsyncTask;
import com.ml.yx.comm.MyThreadPool;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends BaseTitleBarActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private final static int PAGE_SIZE = 30;
    public final static int WHAT_REPLY = 0;

    private final static int REQUEST_CODE_QUESTION = 10001;
    private final static int REQUEST_CODE_REPLY = 10002;

    private PullToRefreshListView pullToRefreshListView;
    private ListView mListView;
    private List<MessageBean> messageList = new ArrayList<>();
    private MessageAdapter mAdapter;
    /**
     * 本地数据是否已全部加载
     */
    private boolean localAllLoaded = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_REPLY:
                    int position = msg.arg1;
                    MessageBean message = messageList.get(position);
                    Intent intent = new Intent(MessageActivity.this, QuestionActivity.class);
                    intent.putExtra(QuestionActivity.PARAM_MODE, QuestionActivity.MODE_REPLY);
                    intent.putExtra(QuestionActivity.PARAM_MSG_ID, message.getMsg_id());
                    intent.putExtra(QuestionActivity.PARAM_MESSAGE, message.getQuestion());
                    startActivityForResult(intent, REQUEST_CODE_REPLY);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.message_list);
        mListView = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setOnRefreshListener(this);
        findViewById(R.id.btn_ask_coach).setOnClickListener(this);
        loadLocalMessages();
        SharedPreferencesUtil.setNewMessage(false);
        EventBus.getDefault().post(new TitleBarRedPointEvent(false));
        youXinTitleBar.setTitleText(SharedPreferencesUtil.getInstructorName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ask_coach:
                Intent intent = new Intent(MessageActivity.this, QuestionActivity.class);
                intent.putExtra(QuestionActivity.PARAM_MODE, QuestionActivity.MODE_QUESTION);
                startActivityForResult(intent, REQUEST_CODE_QUESTION);
                break;
        }
    }

    private void loadLocalMessages() {
        new MultiThreadAsyncTask<Void, Void, List<MessageBean>>() {

            protected void onPreExecute() {

            }

            @Override
            protected List<MessageBean> doInBackground(Void... params) {
                messageList = MessageDBHelper.getInstance().getData(0, PAGE_SIZE);
                return messageList;
            }

            @Override
            protected void onPostExecute(List<MessageBean> result) {
                refreshView();
                if (result == null || result.size() == 0){
                    requestMessages();
                }
            }

        }.executeMultiThread();

    }

    private void refreshView() {
        if (messageList != null && messageList.size() > 0) {
            pullToRefreshListView.setVisibility(View.VISIBLE);
            if (mAdapter == null) {
                mAdapter = new MessageAdapter(MessageActivity.this, messageList, mHandler);
                mListView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            pullToRefreshListView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            MessageBean message = (MessageBean) data.getSerializableExtra(QuestionActivity.RESULT_MESSAGE_BEAN);
            if (requestCode == REQUEST_CODE_QUESTION) {
                messageList.add(0, message);
            } else if (requestCode == REQUEST_CODE_REPLY) {
                for (MessageBean item : messageList) {
                    if (item.getMsg_id().equals(message.getMsg_id())) {
                        item.setAnswer(message.getAnswer());
                        item.setAnswerer_id(message.getAnswerer_id());
                        item.setAnswerer_name(message.getAnswerer_name());
                        item.setReplyable(message.isReplyable());
                    }
                }
            }
            refreshView();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        if (localAllLoaded) {
            loadMoreFromNet(messageList.get(messageList.size() - 1).getMsg_id());
        } else {
            loadMoreFromLocal(messageList.get(messageList.size() - 1).getCreateTime());
        }
    }

    private void loadMoreFromLocal(final long createTime) {
        new MultiThreadAsyncTask<Void, Void, List<MessageBean>>() {

            protected void onPreExecute() {

            }

            @Override
            protected List<MessageBean> doInBackground(Void... params) {
                List<MessageBean> oldMessageList = MessageDBHelper.getInstance().getDataBefore(createTime, PAGE_SIZE);
                return oldMessageList;
            }

            @Override
            protected void onPostExecute(List<MessageBean> result) {
                if (result != null && result.size() > 0) {
                    messageList.addAll(result);
                    mAdapter.notifyDataSetChanged();
                } else {
                    localAllLoaded = true;
                    loadMoreFromNet(messageList.get(messageList.size() - 1).getMsg_id());
                }
                pullToRefreshListView.onRefreshComplete();
            }

        }.executeMultiThread();
    }

    public void loadMoreFromNet(String msgId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("msg_id", msgId);
        WebRequest request = new WebRequest(WebService.ACCOUNT_QA_LIST_URL, params, MessageListBean.class, new Response.Listener<BaseBean>() {

            @Override
            public void onResponse(BaseBean response) {
                if (response != null) {
                    if (response.isSuccess()) {
                        pullToRefreshListView.onRefreshComplete();
                        MessageListBean messageListBean = (MessageListBean) response;
                        List<MessageBean> messages = messageListBean.getList();

                        MessageDBHelper dbHelper = MessageDBHelper.getInstance();
                        if (messages != null && messages.size() > 0) {
                            for (MessageBean message : messages) {
                                dbHelper.save(message);
                            }
                            messageList.addAll(messages);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            APPUtil.showToast(MessageActivity.this, getString(R.string.load_more_complete));
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                APPUtil.showToast(getApplicationContext(), error.getMessage());
            }

            @Override
            public void onStart() {
            }

        });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    @Override
    public void finish() {
        setMessagesRead();
        super.finish();
    }

    /**
     * 设置所有消息为已读
     */
    private void setMessagesRead() {
        MyThreadPool.poolExecute(new Runnable() {
            @Override
            public void run() {
                MessageDBHelper dbHelper = MessageDBHelper.getInstance();
                for (MessageBean message :
                        messageList) {
                    if (!message.isQuestionRead() || !message.isAnswerRead()) {
                        message.setQuestionRead(true);
                        message.setAnswerRead(true);
                        dbHelper.update(message);
                    }
                }
            }
        });

    }

    public void requestMessages() {
        HashMap<String, String> params = new HashMap<>();
        params.put("msg_id", "0");
        WebRequest request = new WebRequest(WebService.ACCOUNT_QA_LIST_URL, params, MessageListBean.class, new Response.Listener<BaseBean>() {

            @Override
            public void onResponse(BaseBean response) {
                if (!isActivityDestroyed()) {
                    cancelLoadingDialog();
                }
                if (response != null) {
                    if (response.isSuccess()) {
                        SharedPreferencesUtil.setMessageLastUpdateTime(DateUtil.getCurWenbaTime() / 1000);
                        MessageListBean messageListBean = (MessageListBean) response;
                        List<MessageBean> messages = messageListBean.getList();

                        MessageDBHelper dbHelper = MessageDBHelper.getInstance();
                        dbHelper.deleteAll();
                        if (messages != null && messages.size() > 0) {
                            for (MessageBean message : messages) {
                                dbHelper.save(message);
                            }
                            if (!isActivityDestroyed()) {
                                messageList.addAll(messages);
                                refreshView();
                            }
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                APPUtil.showToast(getApplicationContext(), error.getMessage());
                if (!isActivityDestroyed()) {
                    cancelLoadingDialog();
                }
            }

            @Override
            public void onStart() {
                showLoadingDialog();
            }

        });
        WebDataLoader.getInstance(getApplicationContext()).startHttpLoader(request);
    }

    @Override
    protected String getPageCode() {
        return null;
    }
}
