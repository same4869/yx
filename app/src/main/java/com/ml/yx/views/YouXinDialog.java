package com.ml.yx.views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ml.yx.R;
import com.ml.yx.comm.ScreenUtils;
import com.ml.yx.comm.StringUtil;

public class YouXinDialog extends Dialog implements OnClickListener {

	private View rootView;
	private TextView titleView;
	private TextView messageView;
	private OnClickListener rightListener, leftListener;
	private Button rightBtn, leftBtn;
	private String title, message;
	private Context mContext;


	public void setRightListener(OnClickListener rightListener) {
		this.rightListener = rightListener;
	}

	public void setLeftListener(OnClickListener leftListener) {
		this.leftListener = leftListener;
	}

	/**
	 * constructor for classic dialog
	 * 
	 */
	public YouXinDialog(Context context, String title, String message) {
		super(context, true, null);
		this.title = title;
		this.message = message;
		this.mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_alert, null);
		setContentView(rootView);

		findViewById(R.id.dialog_root).setOnClickListener(this);

		titleView = (TextView) findViewById(R.id.dialog_title);
		messageView = (TextView) findViewById(R.id.skin_dialog_message);
		messageView.setMovementMethod(ScrollingMovementMethod.getInstance());

		if (StringUtil.isNotBlank(title)) {
			titleView.setText(title);
			titleView.setVisibility(View.VISIBLE);
		}else{
			titleView.setVisibility(View.GONE);
		}
		if (message != null) {
			messageView.setText(message);
		}

		rightBtn = (Button) findViewById(R.id.dialog_right_btn);
		rightBtn.setOnClickListener(this);
		leftBtn = (Button) findViewById(R.id.dialog_left_btn);
		leftBtn.setOnClickListener(this);


		applyLayoutParams();
	}
	
	public void hideTitle(){
		titleView.setVisibility(View.GONE);
	}
	
	public void showTitle(){
		titleView.setVisibility(View.VISIBLE);
	}

	/**
	 * @param text
	 *            要先调用dialog.show()才能用
	 */
	public void setRightButtonText(String text) {
		rightBtn.setText(text);
	}

	public String getRightButtonText() {
		return rightBtn.getText().toString();
	}

	public void setLeftButtonText(String text) {
		leftBtn.setText(text);
	}

	public String getLeftButtonText() {
		return leftBtn.getText().toString();
	}

	public Button getLeftButton() {
		return leftBtn;
	}

	public void setDrawableTop(int resId) {
		messageView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
	}

	public void setDrawableBottom(int resId) {
		messageView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, resId);
	}

	public void setMessageGravity(int gravity) {
		messageView.setGravity(gravity);
	}


	private void applyLayoutParams() {
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			lp.width = ScreenUtils.getScreenHeight(getContext());
		} else {
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		}
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.CENTER;
		getWindow().setAttributes(lp);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_right_btn:
			if (rightListener != null) {
				rightListener.onClick(this, 1);
			} else {
				dismiss();
			}
			break;
		case R.id.dialog_left_btn:
			if (leftListener != null) {
				leftListener.onClick(this, 0);
			} else {
				dismiss();
			}
			break;
		}

	}

	/**
	 * 按钮是否可点击
	 * 
	 * @param isClickable
	 */
	public void setLeftButtonClickcable(boolean isClickable) {
		leftBtn.setEnabled(isClickable);
	}

	/**
	 * 按钮是否可点击
	 * 
	 * @param isClickable
	 */
	public void setRightButtonClickcable(boolean isClickable) {
		rightBtn.setEnabled(isClickable);
	}



}
