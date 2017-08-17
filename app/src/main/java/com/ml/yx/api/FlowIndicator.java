package com.ml.yx.api;

import android.view.ViewGroup;

/**
 * 浮动切换接口类
 *
 * @author junjie.Li
 */
public interface FlowIndicator extends ViewSwitchListener {
    public void setViewFlow(ViewGroup viewGroup, int count);
}
