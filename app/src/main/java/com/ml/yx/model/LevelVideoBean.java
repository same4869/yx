package com.ml.yx.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xunwang on 16/4/21.
 */
public class LevelVideoBean extends BaseBean {
    private LightBean light;

    public LightBean getLight() {
        return light;
    }

    public void setLight(LightBean light) {
        this.light = light;
    }

    public class LightBean implements Serializable {
        private int repeatTimes;
        private List<ActionBean> actions;
        private List<ActionBean> stretch;

        public int getRepeatTimes() {
            return repeatTimes;
        }

        public void setRepeatTimes(int repeatTimes) {
            this.repeatTimes = repeatTimes;
        }

        public List<ActionBean> getActions() {
            return actions;
        }

        public void setActions(List<ActionBean> actions) {
            this.actions = actions;
        }

        public List<ActionBean> getStretch() {
            return stretch;
        }

        public void setStretch(List<ActionBean> stretch) {
            this.stretch = stretch;
        }
    }

    public class ActionBean implements Serializable {
        private String preVideoUrl;
        private String videoUrl;
        private String tipName;
        private String tipRepeat;
        private TipDetailBean tipDetail;

        public String getPreVideoUrl() {
            return preVideoUrl;
        }

        public void setPreVideoUrl(String preVideoUrl) {
            this.preVideoUrl = preVideoUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getTipName() {
            return tipName;
        }

        public void setTipName(String tipName) {
            this.tipName = tipName;
        }

        public String getTipRepeat() {
            return tipRepeat;
        }

        public void setTipRepeat(String tipRepeat) {
            this.tipRepeat = tipRepeat;
        }

        public TipDetailBean getTipDetail() {
            return tipDetail;
        }

        public void setTipDetail(TipDetailBean tipDetail) {
            this.tipDetail = tipDetail;
        }
    }

    public class TipDetailBean implements Serializable {
        private String mudi;
        private String jiqun;
        private String nandu;
        private String qiangdu;
        private String yaoling;
        private String zhuyi;
        private String huxi;

        public String getMudi() {
            return mudi;
        }

        public void setMudi(String mudi) {
            this.mudi = mudi;
        }

        public String getJiqun() {
            return jiqun;
        }

        public void setJiqun(String jiqun) {
            this.jiqun = jiqun;
        }

        public String getNandu() {
            return nandu;
        }

        public void setNandu(String nandu) {
            this.nandu = nandu;
        }

        public String getQiangdu() {
            return qiangdu;
        }

        public void setQiangdu(String qiangdu) {
            this.qiangdu = qiangdu;
        }

        public String getYaoling() {
            return yaoling;
        }

        public void setYaoling(String yaoling) {
            this.yaoling = yaoling;
        }

        public String getZhuyi() {
            return zhuyi;
        }

        public void setZhuyi(String zhuyi) {
            this.zhuyi = zhuyi;
        }

        public String getHuxi() {
            return huxi;
        }

        public void setHuxi(String huxi) {
            this.huxi = huxi;
        }
    }

}
