package com.ml.yx.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ml.yx.api.FlowIndicator;
import com.ml.yx.R;

/**
 * 
 * @Author:Lijj
 * @Date:2014-5-19上午11:22:42
 * @Todo:TODO
 */
public class CircleFlowIndicator extends View implements FlowIndicator {
	private static final int STYLE_STROKE = 0;
	private static final int STYLE_FILL = 1;
	private float radius = 4;
	private float circleSeparation = 2 * radius;
	private int currentIndex = 0;
	private float centeringOffset = 0;
	private final Paint mPaintInactive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mPaintActive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private ViewGroup viewGroup = null;
	private int count = 0;
	private boolean onlyOneShow = true;

	public CircleFlowIndicator(Context context) {
		this(context, null);
	}

	public CircleFlowIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleFlowIndicator);

		int activeType = a.getInt(R.styleable.CircleFlowIndicator_activeType, STYLE_FILL);

		int activeDefaultColor = 0xFFFFFFFF;
		int activeColor = a.getColor(R.styleable.CircleFlowIndicator_activeColor, activeDefaultColor);

		int inactiveType = a.getInt(R.styleable.CircleFlowIndicator_inactiveType, STYLE_STROKE);

		int inactiveDefaultColor = 0x44FFFFFF;
		int inactiveColor = a.getColor(R.styleable.CircleFlowIndicator_inactiveColor, inactiveDefaultColor);

		radius = a.getDimension(R.styleable.CircleFlowIndicator_radius, radius);
		onlyOneShow = a.getBoolean(R.styleable.CircleFlowIndicator_onlyOneShow, true);

		a.recycle();
		initColors(activeColor, inactiveColor, activeType, inactiveType);
		
		circleSeparation = 2 * radius;

	}

	private void initColors(int activeColor, int inactiveColor, int activeType, int inactiveType) {
		switch (inactiveType) {
		case STYLE_FILL:
			mPaintInactive.setStyle(Style.FILL);
			break;
		default:
			mPaintInactive.setStyle(Style.STROKE);
		}
		mPaintInactive.setColor(inactiveColor);

		switch (activeType) {
		case STYLE_STROKE:
			mPaintActive.setStyle(Style.STROKE);
			break;
		default:
			mPaintActive.setStyle(Style.FILL);
		}
		mPaintActive.setColor(activeColor);
	}

	public void setActiveColor(int activeColor) {
		mPaintActive.setColor(activeColor);
	}

	public void setInactiveColor(int inactiveColor) {
		mPaintInactive.setColor(inactiveColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (viewGroup != null) {
			count = viewGroup.getChildCount();
		}
		if(count <= 1 && !onlyOneShow){
			return;
		}

		int leftPadding = getPaddingLeft();

		for (int iLoop = 0; iLoop < count; iLoop++) {
			canvas.drawCircle(leftPadding + radius + (iLoop * (radius * 2 + circleSeparation)) + centeringOffset, getPaddingTop() + radius, radius, mPaintInactive);
		}
		canvas.drawCircle(leftPadding + radius + (currentIndex * (radius * 2 + circleSeparation)) + centeringOffset, getPaddingTop() + radius, radius, mPaintActive);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			if (viewGroup != null) {
				count = viewGroup.getChildCount();
			}
			result = (int) (getPaddingLeft() + getPaddingRight() + (count * 2 * radius) + (count - 1) * circleSeparation + 1);
		}
		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int) (2 * radius + getPaddingTop() + getPaddingBottom() + 1);
		}
		return result;
	}

	@Override
	public void onSwitched(View view, int position) {
		currentIndex = position;
		postInvalidate();
	}

	@Override
	public void setViewFlow(ViewGroup viewGroup, int count) {
		if (!(viewGroup instanceof ViewPager)) {
			// this.viewGroup = viewGroup;
		}
		this.count = count;
		postInvalidate();
	}

	public int getCount() {
		return count;
	}
}