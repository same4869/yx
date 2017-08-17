package com.ml.yx.event;

/**
 * Created by BTyang on 16/5/9.
 */
public class TitleBarRedPointEvent {
    private boolean isVisible; //titlebar右侧头像上的小红点是否可见

    public TitleBarRedPointEvent(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
