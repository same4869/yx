package com.ml.yx.model;

import java.util.List;

/**
 * Created by BTyang on 16/5/4.
 */
public class MessageListBean extends BaseBean {

    List<MessageBean> list;

    public List<MessageBean> getList() {
        return list;
    }

    public void setList(List<MessageBean> list) {
        this.list = list;
    }
}
