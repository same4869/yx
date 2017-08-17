package com.ml.yx.model;

/**
 * Created by xunwang on 16/4/21.
 */
public class LevelDoneBean extends BaseBean {
    private String tipCal;
    private boolean isStageFinish;

    public String getTipCal() {
        return tipCal;
    }

    public void setTipCal(String tipCal) {
        this.tipCal = tipCal;
    }

    public boolean isStageFinish() {
        return isStageFinish;
    }

    public void setStageFinish(boolean stageFinish) {
        isStageFinish = stageFinish;
    }
}
