package com.ml.yx.comm;

import android.util.Log;

public class BBLog {

    public static final String LOG = BBLog.class.getSimpleName();

    public static boolean isDebug() {
        return Constants.IS_DEBUG;
    }

    public static void d(String tag, String message) {
        if (!isDebug() || message == null)
            return;
        //Log.x call replaced
    }

    public static void e(String tag, String message) {
        if (!isDebug() || message == null)
            return;
        //Log.x call replaced
    }

    public static void w(String tag, String message) {
        if (!isDebug() || message == null)
            return;
        //Log.x call replaced
    }

    public static void i(String tag, String message) {
        if (!isDebug() || message == null)
            return;
        //Log.x call replaced
    }

    public static void wtf(String tag, String message) {
        if (!isDebug() || message == null)
            return;
        //Log.x call replaced
    }

    public static void e2(String tag, String message) {
        if (message == null)
            return;
        //Log.x call replaced
    }

}
