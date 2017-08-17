package com.ml.yx.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ml.yx.R;
import com.ml.yx.activity.user.MessageActivity;
import com.ml.yx.comm.DateUtil;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.comm.StringUtil;
import com.ml.yx.model.MessageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BTyang on 16/5/1.
 */
public class MessageAdapter extends BaseAdapter {

    private final static String DATE_TEMPLATE = "yyyy.MM.dd";

    private Context context;
    private Handler handler;
    private List<MessageBean> messageList = new ArrayList<>();

    public MessageAdapter(Context context, List<MessageBean> messageList, Handler handler) {
        this.context = context;
        this.messageList = messageList;
        this.handler = handler;
    }


    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, null);
            viewHolder.questionerName = (TextView) convertView.findViewById(R.id.tv_questioner_name);
            viewHolder.answererName = (TextView) convertView.findViewById(R.id.tv_answerer_name);
            viewHolder.questionContent = (TextView) convertView.findViewById(R.id.tv_question);
            viewHolder.answerContent = (TextView) convertView.findViewById(R.id.tv_answer);
            viewHolder.date = (TextView) convertView.findViewById(R.id.tv_date);
            viewHolder.questionRedPoint = convertView.findViewById(R.id.questioner_red_point);
            viewHolder.answerRedPoint = convertView.findViewById(R.id.answerer_red_point);
            viewHolder.divider = convertView.findViewById(R.id.divider);
            viewHolder.btnReply = convertView.findViewById(R.id.btn_reply);
            viewHolder.answerLayout = convertView.findViewById(R.id.answer_layout);
            convertView.setTag(viewHolder);
            viewHolder.btnReply.setOnClickListener(onReplyClickListener);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MessageBean message = messageList.get(position);
        String userId = SharedPreferencesUtil.getUserId();
        if (userId != null && userId.equals(message.getQuestioner_id())) {
            viewHolder.questionerName.setText(context.getString(R.string.message_from_myself));
        } else {
            viewHolder.questionerName.setText(message.getQuestioner_name());
        }
        viewHolder.questionContent.setText(message.getQuestion());
        viewHolder.questionRedPoint.setVisibility(message.isQuestionRead() ? View.GONE : View.VISIBLE);
        if (StringUtil.isBlank(message.getAnswer())) {
            viewHolder.answerLayout.setVisibility(View.GONE);
            if (message.isReplyable()) {
                viewHolder.divider.setVisibility(View.VISIBLE);
                viewHolder.btnReply.setVisibility(View.VISIBLE);
            } else {
                viewHolder.divider.setVisibility(View.GONE);
                viewHolder.btnReply.setVisibility(View.GONE);
            }
        } else {
            viewHolder.answerLayout.setVisibility(View.VISIBLE);
            viewHolder.answererName.setText(message.getAnswerer_name());
            viewHolder.answerContent.setText(message.getAnswer());
            viewHolder.divider.setVisibility(View.VISIBLE);
            viewHolder.btnReply.setVisibility(View.GONE);
            viewHolder.answerRedPoint.setVisibility(message.isAnswerRead() ? View.GONE : View.VISIBLE);
            if (userId != null && userId.equals(message.getAnswerer_id())) {
                viewHolder.answererName.setText(context.getString(R.string.message_from_myself));
            } else {
                viewHolder.answererName.setText(message.getAnswerer_name());
            }
        }
        viewHolder.date.setText(DateUtil.getDateString(context, message.getCreateTime() * 1000, DATE_TEMPLATE));
        viewHolder.btnReply.setTag(R.id.btn_reply, position);
        return convertView;
    }

    class ViewHolder {
        TextView questionerName;
        TextView answererName;
        TextView questionContent;
        TextView answerContent;
        TextView date;
        View questionRedPoint;
        View answerRedPoint;
        View divider;
        View btnReply;
        View answerLayout;
    }

    private View.OnClickListener onReplyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Message msg = new Message();
            msg.what = MessageActivity.WHAT_REPLY;
            msg.arg1 = (int) v.getTag(R.id.btn_reply);
            handler.handleMessage(msg);
        }
    };
}
