package com.ml.yx.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xunwang on 16/4/21.
 */
public class LevelSelectBean extends BaseBean {
    private String bannerUrl;
    private boolean isBannerOpen;
    private int currentLevel;
    private String normalImg;
    private String heartImg;
    private List<LevelBean> levels;

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public boolean isBannerOpen() {
        return isBannerOpen;
    }

    public void setBannerOpen(boolean bannerOpen) {
        isBannerOpen = bannerOpen;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getNormalImg() {
        return normalImg;
    }

    public void setNormalImg(String normalImg) {
        this.normalImg = normalImg;
    }

    public String getHeartImg() {
        return heartImg;
    }

    public void setHeartImg(String heartImg) {
        this.heartImg = heartImg;
    }

    public List<LevelBean> getLevels() {
        return levels;
    }

    public void setLevels(List<LevelBean> levels) {
        this.levels = levels;
    }

    public class LevelBean implements Serializable {
        private int levelId;
        private int day;
        private int lockStatus;
        private String forkUrl;
        private boolean isHeartLookUnLocked;
        private int heartUnLockedDay;
        private List<String> normalVideoUrl;
        private List<String> heartVideoUrl;

        public int getLevelId() {
            return levelId;
        }

        public void setLevelId(int levelId) {
            this.levelId = levelId;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getLockStatus() {
            return lockStatus;
        }

        public void setLockStatus(int lockStatus) {
            this.lockStatus = lockStatus;
        }

        public String getForkUrl() {
            return forkUrl;
        }

        public void setForkUrl(String forkUrl) {
            this.forkUrl = forkUrl;
        }

        public boolean isHeartLookUnLocked() {
            return isHeartLookUnLocked;
        }

        public void setHeartLookUnLocked(boolean heartLookUnLocked) {
            isHeartLookUnLocked = heartLookUnLocked;
        }

        public int getHeartUnLockedDay() {
            return heartUnLockedDay;
        }

        public void setHeartUnLockedDay(int heartUnLockedDay) {
            this.heartUnLockedDay = heartUnLockedDay;
        }

        public List<String> getNormalVideoUrl() {
            return normalVideoUrl;
        }

        public void setNormalVideoUrl(List<String> normalVideoUrl) {
            this.normalVideoUrl = normalVideoUrl;
        }

        public List<String> getHeartVideoUrl() {
            return heartVideoUrl;
        }

        public void setHeartVideoUrl(List<String> heartVideoUrl) {
            this.heartVideoUrl = heartVideoUrl;
        }
    }
}
