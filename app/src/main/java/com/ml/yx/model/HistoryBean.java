package com.ml.yx.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lijj on 16/5/19.
 */
public class HistoryBean implements Serializable{
    private int cal;
    private Long timeStamp;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd");

    public HistoryBean(){}

    public HistoryBean(int cal, long timeStamp){
        this.cal = cal;
        this.timeStamp = timeStamp;
    }

    public int getCal() {
        return cal;
    }

    public void setCal(int cal) {
        this.cal = cal;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAxisValue(){
        return sdf.format(new Date(timeStamp * 1000));
    }

}
