package com.ml.yx.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ml.yx.R;
import com.ml.yx.comm.BBLog;


/**
 * @Author:Lijj
 * @Date:2014-4-22下午1:34:00
 * @Todo:TODO
 */
public class RoundAngleImageView extends ImageView {

    private float xRadius = 0F;
    private float yRadius = 0F;
    private Drawable backgroudDrawable;
    private float spaceSize = 0F;
    //是否是圆形（radius为长宽的一半）
    private boolean isCircle = true;

    public float getxRadius() {
        return xRadius;
    }

    public void setxRadius(float xRadius) {
        this.xRadius = xRadius;
    }

    public float getyRadius() {
        return yRadius;
    }

    public void setyRadius(float yRadius) {
        this.yRadius = yRadius;
    }

    public RoundAngleImageView(Context context) {
        super(context);
        backgroudDrawable = context.getResources().getDrawable(R.mipmap.head_bg);
        spaceSize = 3;
    }

    public RoundAngleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);
        backgroudDrawable = typeArray.getDrawable(R.styleable.RoundAngleImageView_backgroudDrawable);
        spaceSize = typeArray.getDimension(R.styleable.RoundAngleImageView_spaceSize, 0);
        xRadius = typeArray.getDimension(R.styleable.RoundAngleImageView_xRadius, 0);
        yRadius = typeArray.getDimension(R.styleable.RoundAngleImageView_yRadius, 0);
        if (xRadius != 0 || yRadius != 0) {
            isCircle = false;
        }
        typeArray.recycle();
    }

    public void setBackgroudDrawable(Drawable backgroudDrawable) {
        this.backgroudDrawable = backgroudDrawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        if (isCircle) {
            xRadius = width / 2.0F;
            yRadius = height / 2.0F;
        }
    }

    protected void onDraw(Canvas canvas) {
        if (backgroudDrawable != null) {
            BitmapDrawable bd = (BitmapDrawable) backgroudDrawable;
            onDrawBackgroud(bd, canvas);
        }
        onDrawSrc(canvas);
    }

    private void onDrawSrc(Canvas canvas) {
        //BBLog call replaced
        BitmapDrawable bitmapDrawable;
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (drawable instanceof TransitionDrawable) {
            TransitionDrawable tranDrawable = (TransitionDrawable) drawable;
            bitmapDrawable = (BitmapDrawable) tranDrawable.getDrawable(1);
        } else {
            bitmapDrawable = (BitmapDrawable) drawable;
        }
        BitmapShader shader = new BitmapShader(bitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        // 设置映射否则图片显示不全
        float viewWidth = getWidth() - spaceSize;
        float viewHeight = getHeight() - spaceSize;
        RectF rect = new RectF(spaceSize, spaceSize, viewWidth, viewHeight);
        int width = bitmapDrawable.getBitmap().getWidth();
        int height = bitmapDrawable.getBitmap().getHeight();
        // int size = Math.min(width, height);
        RectF src = new RectF(0, 0, width, height);
        // RectF src = new RectF(spaceSize, spaceSize, width - spaceSize, height - spaceSize);
        // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于view的宽高；所以这里取大值；
        float scale = Math.max(viewWidth * 1.0f / width, viewHeight
                * 1.0f / height);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(src, rect, Matrix.ScaleToFit.CENTER);
        matrix.setScale(scale, scale);
        shader.setLocalMatrix(matrix);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        canvas.drawRoundRect(rect, xRadius - spaceSize, yRadius - spaceSize, paint);
    }

    private void onDrawBackgroud(BitmapDrawable bitmapDrawable, Canvas canvas) {
        BitmapShader shader = new BitmapShader(bitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        int width = getWidth();
        int height = getHeight();
        // 设置映射否则图片显示不全
        RectF rect = new RectF(0.0f, 0.0f, width, height);

        RectF src = new RectF(0.0f, 0.0f, width, height);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(src, rect, Matrix.ScaleToFit.CENTER);

        shader.setLocalMatrix(matrix);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        canvas.drawRoundRect(rect, xRadius, yRadius, paint);
    }

}
