package com.ml.yx.model;

/**
 * Created by BTyang on 16/5/5.
 */
public class MessageBean extends BaseBean {
    String msg_id;     //消息ID
    String question;     //消息内容
    String questioner_id;     //提问者ID
    String questioner_name;     //提问者名字
    String answer;     //回复内容
    String answerer_id;     //回复人ID
    String answerer_name;     //回复人名字
    boolean replyable;     //1为可回复，0为不可回复
    long createTime;    //时间戳，这条消息的创建时间
    long updateTime;    //时间戳，这条消息的更新时间
    boolean isQuestionRead = true;//问题是否已读
    boolean isAnswerRead = true;//回复是否已读

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestioner_id() {
        return questioner_id;
    }

    public void setQuestioner_id(String questioner_id) {
        this.questioner_id = questioner_id;
    }

    public String getQuestioner_name() {
        return questioner_name;
    }

    public void setQuestioner_name(String questioner_name) {
        this.questioner_name = questioner_name;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerer_id() {
        return answerer_id;
    }

    public void setAnswerer_id(String answerer_id) {
        this.answerer_id = answerer_id;
    }

    public String getAnswerer_name() {
        return answerer_name;
    }

    public void setAnswerer_name(String answerer_name) {
        this.answerer_name = answerer_name;
    }

    public boolean isReplyable() {
        return replyable;
    }

    public void setReplyable(boolean replyable) {
        this.replyable = replyable;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isQuestionRead() {
        return isQuestionRead;
    }

    public void setQuestionRead(boolean questionRead) {
        isQuestionRead = questionRead;
    }

    public boolean isAnswerRead() {
        return isAnswerRead;
    }

    public void setAnswerRead(boolean answerRead) {
        isAnswerRead = answerRead;
    }
}
