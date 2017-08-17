package com.ml.yx.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xunwang on 16/4/21.
 */
public class StageDoneBean extends BaseBean {
    private int stage;
    private String totleCals;
    private String totleTime;
    private String targetWeight;
    private List<TipCalBean> tipCals;

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public String getTotleCals() {
        return totleCals;
    }

    public void setTotleCals(String totleCals) {
        this.totleCals = totleCals;
    }

    public String getTotleTime() {
        return totleTime;
    }

    public void setTotleTime(String totleTime) {
        this.totleTime = totleTime;
    }

    public String getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(String targetWeight) {
        this.targetWeight = targetWeight;
    }

    public List<TipCalBean> getTipCals() {
        return tipCals;
    }

    public void setTipCals(List<TipCalBean> tipCals) {
        this.tipCals = tipCals;
    }

    public class TipCalBean implements Serializable {
        private int day;
        private int tipCal;

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getTipCal() {
            return tipCal;
        }

        public void setTipCal(int tipCal) {
            this.tipCal = tipCal;
        }
    }
}
