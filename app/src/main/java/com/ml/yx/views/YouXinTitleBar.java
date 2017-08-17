package com.ml.yx.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ml.yx.R;
import com.ml.yx.comm.CommUtil;
import com.ml.yx.comm.SharedPreferencesUtil;

/**
 * Created by xunwang on 16/4/7.
 */
public class YouXinTitleBar extends FrameLayout implements View.OnClickListener {
    private ImageView leftView;
    private TextView titleView;
    private RoundAngleImageView avatarView;
    private View redPoint;
    private YouXinTitleBarListener youXinTitleBarListener;
    private Context context;

    public YouXinTitleBar(Context context) {
        super(context);
        initView(context, null);
    }

    public YouXinTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public YouXinTitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.YouXinTitleBar);
        boolean bShowAvatar = a.getBoolean(R.styleable.YouXinTitleBar_showUserAvatar, true);
        int backgroundRes = a.getResourceId(R.styleable.YouXinTitleBar_barBackground, -1);
        String sTitle = a.getString(R.styleable.YouXinTitleBar_barTitle);
        a.recycle();
        LayoutInflater.from(context).inflate(R.layout.view_titlebar, this);
        View rootView = findViewById(R.id.titlebar_rootview);
        titleView = (TextView) findViewById(R.id.titlebar_center_view);
        findViewById(R.id.titlebar_left_view).setOnClickListener(this);
        findViewById(R.id.titlebar_right_layout).setOnClickListener(this);
        leftView = (ImageView) findViewById(R.id.titlebar_left_view);
        leftView.setOnClickListener(this);
        avatarView = (RoundAngleImageView) findViewById(R.id.titlebar_right_view);
        redPoint = findViewById(R.id.red_point);
        if (!bShowAvatar) {
            findViewById(R.id.titlebar_right_layout).setVisibility(GONE);
        } else {
            findViewById(R.id.titlebar_right_layout).setVisibility(VISIBLE);
            setInstructorAvatar(SharedPreferencesUtil.getInstructorId());
        }
        titleView.setText(sTitle);
        if (backgroundRes != -1) {
            rootView.setBackgroundResource(backgroundRes);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_view:
                if (youXinTitleBarListener != null) {
                    youXinTitleBarListener.leftListener(view);
                }
                break;
            case R.id.titlebar_right_layout:
                if (youXinTitleBarListener != null) {
                    youXinTitleBarListener.rightListener(view);
                }
                break;
        }
    }

    public void setTitleText(String text) {
        titleView.setText(text);
    }

    public void setRightViewVisibility(int visibility) {
        findViewById(R.id.titlebar_right_layout).setVisibility(visibility);
    }

    public ImageView getLeftView() {
        return leftView;
    }

    public void setYouXinTitleBarListener(YouXinTitleBarListener youXinTitleBarListener) {
        this.youXinTitleBarListener = youXinTitleBarListener;
    }

    public interface YouXinTitleBarListener {
        public void leftListener(View v);

        public void rightListener(View v);
    }

    public void showRedPoint() {
        redPoint.setVisibility(VISIBLE);
    }

    public void hideRedPoint() {
        redPoint.setVisibility(GONE);
    }

    public void setInstructorAvatar(int avatarId) {
        avatarView.setImageDrawable(CommUtil.getCoachDrawable(getContext(),avatarId));
//        if (StringUtil.isNotBlank(avatarUrl)) {
//            WebDataLoader.getInstance(context).loadImageViewWithDefaultImage(avatarView, avatarUrl, R.mipmap.default_head_avatar, R.mipmap.default_head_avatar);
//        }
    }

}
