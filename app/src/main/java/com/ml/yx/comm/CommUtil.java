package com.ml.yx.comm;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.ml.yx.R;

/**
 * Created by Lijj on 16/5/30.
 */
public class CommUtil {

    public static Drawable getCoachDrawable(Context context,int coachId){
        Drawable drawable = null;
        switch (coachId){
            case 1:
                drawable = context.getResources().getDrawable(R.mipmap.coach_default_1);
                break;
            case 2:
                drawable = context.getResources().getDrawable(R.mipmap.coach_default_2);
                break;
            case 3:
                drawable = context.getResources().getDrawable(R.mipmap.coach_lock_1);
                break;
            case 4:
                drawable = context.getResources().getDrawable(R.mipmap.coach_lock_2);
                break;
            default:
                drawable = context.getResources().getDrawable(R.mipmap.coach_default_1);
        }
        return drawable;
    }
}
