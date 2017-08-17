package com.ml.yx.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author Mr.Yang
 *
 */
public class GridViewInScrollView extends GridView {

    /**
     * @param context
     */
    public GridViewInScrollView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public GridViewInScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public GridViewInScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    };

}
