package com.ml.yx.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by xunwang on 16/3/23.
 */
public class LevelsLayout extends LinearLayout {
    private LevelsInterface levelsInterface;

    private int currentLevel = 0;

    private Paint paint = new Paint();

    private int type; //0是关卡选择页，1是视频训练页，公用一个，但布局是反的

    public LevelsLayout(Context context) {
        super(context);

        init();
    }

    public LevelsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LevelsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        if (currentLevel >= 0) {
            final int count = getChildCount();
            if (type == 0) {
                if (((ViewGroup) getChildAt(count - 1)).getChildAt(0) != null) {
                    ((ViewGroup) getChildAt(count - 1)).getChildAt(0).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (levelsInterface != null) {
                                levelsInterface.onLevelClick(1);
                            }
                        }
                    });
                }
                if (((ViewGroup) getChildAt(count - 1)).getChildAt(1) != null) {
                    ((ViewGroup) getChildAt(count - 1)).getChildAt(1).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (levelsInterface != null) {
                                levelsInterface.onForkClick(1);
                            }
                        }
                    });
                }
            }
            for (int i = 1; i < count; i++) {
                View view1, view2, view3 = null, view4 = null;
                final View view5;
                if (type == 0) {
                    if (!levelsInterface.isForkLeft(i)) {
                        if (i % 7 == 3) {
                            view1 = ((ViewGroup) getChildAt(count - i - 1)).getChildAt(1);
                            view5 = ((ViewGroup) getChildAt(count - i - 1)).getChildAt(0);
                            view2 = ((ViewGroup) getChildAt(count - i)).getChildAt(0);
                        } else {
                            view1 = ((ViewGroup) getChildAt(count - i - 1)).getChildAt(0);
                            view5 = ((ViewGroup) getChildAt(count - i - 1)).getChildAt(1);
                            view2 = ((ViewGroup) getChildAt(count - i)).getChildAt(0);
                        }
                        final int finalI = i;
                        if (view1 != null) {
                            view1.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (levelsInterface != null) {
                                        levelsInterface.onLevelClick(finalI + 1);
                                    }
                                }
                            });
                        }
                        if (view5 != null) {
                            view5.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (levelsInterface != null) {
                                        levelsInterface.onForkClick(finalI + 1);
                                    }
                                }
                            });
                        }
                    } else {
                        if (i % 7 == 6) {
                            view1 = ((ViewGroup) getChildAt(count - i - 1)).getChildAt(0);
                            view5 = ((ViewGroup) getChildAt(count - i - 1)).getChildAt(1);
                            view2 = ((ViewGroup) getChildAt(count - i)).getChildAt(1);
                        } else {
                            view1 = ((ViewGroup) getChildAt(count - i - 1)).getChildAt(1);
                            view5 = ((ViewGroup) getChildAt(count - i - 1)).getChildAt(0);
                            view2 = ((ViewGroup) getChildAt(count - i)).getChildAt(1);
                        }
                        final int finalI1 = i;
                        if (view1 != null) {
                            view1.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (levelsInterface != null) {
                                        levelsInterface.onLevelClick(finalI1 + 1);
                                    }
                                }
                            });
                        }
                        if (view5 != null) {
                            view5.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (levelsInterface != null) {
                                        levelsInterface.onForkClick(finalI1 + 1);
                                    }
                                }
                            });
                        }
                    }
                    view3 = getChildAt(count - i - 1);
                    view4 = getChildAt(count - i);
                } else {
                    view1 = getChildAt(count - i - 1);
                    view2 = getChildAt(count - i);
                }

                if (view1 != null && view2 != null) {
                    //if (ViewCompat.isLaidOut(view1) && ViewCompat.isLaidOut(view2)) {
                    float x1, x2, y1, y2;
                    if (type == 0) {
                        x1 = view1.getX() + view3.getX() + (float) view1.getWidth() / 2f;
                        y1 = view1.getY() + view3.getY() + (float) view1.getHeight() / 2f;
                        x2 = view2.getX() + view4.getX() + (float) view2.getWidth() / 2f;
                        y2 = view2.getY() + view4.getY() + (float) view2.getHeight() / 2f;
                    } else {
                        x1 = view1.getX() + (float) view1.getWidth() / 2f;
                        y1 = view1.getY() + (float) view1.getHeight() / 2f;
                        x2 = view2.getX() + (float) view2.getWidth() / 2f;
                        y2 = view2.getY() + (float) view2.getHeight() / 2f;
                    }
                    int level;//恶心的逻辑，建议优化，尤其是反的那个逻辑，还有下标开始逻辑
                    if (type == 0) {
                        level = currentLevel + 1;
                    } else {
                        level = currentLevel;
                    }
                    if (i < level) {
                        if (type == 1) {
                            paint.setColor(Color.parseColor("#FF2A2A2A"));
                        } else {
                            paint.setColor(Color.parseColor("#FF665E0A"));
                        }
                    } else {
                        if (type == 1) {
                            paint.setColor(Color.parseColor("#FF665E0A"));
                        } else {
                            paint.setColor(Color.parseColor("#FF2A2A2A"));
                        }
                    }
                    canvas.drawLine(x1, y1, x2, y2, paint);
                }
            }
            // }
        }

        super.onDraw(canvas);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCurrentLevel(int current) {
        this.currentLevel = current;
    }

    public void setLevelsInterface(LevelsInterface levelsInterface) {
        this.levelsInterface = levelsInterface;
    }

    public interface LevelsInterface {

        void onLevelClick(int i);

        void onForkClick(int i);

        boolean isForkLeft(int i);
    }
}
