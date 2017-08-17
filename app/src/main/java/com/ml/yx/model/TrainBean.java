package com.ml.yx.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lijj on 16/4/13.
 */
public class TrainBean implements Serializable{

    private int totalCalorie;
    private int totalTime;
    private int goal;
    private List<HistoryBean> history;

    public int getTotalCalorie() {
        return totalCalorie;
    }

    public void setTotalCalorie(int totalCalorie) {
        this.totalCalorie = totalCalorie;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public List<HistoryBean> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryBean> history) {
        this.history = history;
    }
}
