package com.ml.yx.views;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ml.yx.R;

public class LoadingDialog extends Dialog implements OnCancelListener, OnShowListener {

	private View loadingView;
	private TextView messageView;
	private Animation animation;
	private int size = 0;

	public LoadingDialog(Context context) {
		super(context, true, null);
		size = context.getResources().getDimensionPixelSize(R.dimen.dp200);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_progress_loading);

		setOnCancelListener(this);
		setOnShowListener(this);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = size;
		lp.height = size;
		lp.gravity = Gravity.CENTER;
		getWindow().setAttributes(lp);
		
		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		loadingView = findViewById(R.id.progress_loading_img);
		
		Drawable drawable = null;
				
		try{
			drawable = getContext().getResources().getDrawable(R.mipmap.campage_loading);
		}catch(Throwable e){
			e.printStackTrace();
		}
		
		if(drawable != null){
			loadingView.setBackgroundDrawable(drawable);
		}
		
		messageView = (TextView) findViewById(R.id.progress_loading_message);
		try{
			animation = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.progress_load);
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

	public void setContentMessage(String message) {
		messageView.setText(message);
		messageView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		loadingView.clearAnimation();
	}

	@Override
	public void onShow(DialogInterface dialog) {
		loadingView.clearAnimation();
		if(animation != null){
			loadingView.startAnimation(animation);
		}
	}

}
