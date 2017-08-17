package com.ml.yx.model;

import java.util.List;

/**
 * Created by BTyang on 16/4/11.
 */
public class CoachListBean extends BaseBean {

    private List<CoachBean> response;

    public List<CoachBean> getResponse() {
        return response;
    }

    public void setResponse(List<CoachBean> response) {
        this.response = response;
    }

    public static class CoachBean extends  BaseBean{
        int instructorId;
        String name;
        String avatar;
        boolean unlock;

        public int getInstructorId() {
            return instructorId;
        }

        public void setInstructorId(int instructorId) {
            this.instructorId = instructorId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public boolean isUnlock() {
            return unlock;
        }

        public void setUnlock(boolean unlock) {
            this.unlock = unlock;
        }
    }
}
