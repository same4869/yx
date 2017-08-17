package com.ml.yx;

import android.app.ActivityManager;
import android.app.Application;
import android.util.Log;

import com.bugtags.library.Bugtags;
import com.ml.yx.comm.AppInfoUtils;
import com.ml.yx.comm.Constants;
import com.ml.yx.comm.SharedPreferencesUtil;
import com.ml.yx.web.WebRequest;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by wangxun on 16/4/3.
 */
public class YouXinApplication extends Application {

    private static YouXinApplication instance;

    public static YouXinApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        WebRequest.setToken(SharedPreferencesUtil.getUserToken());

        // 设置友盟统计测试
        if (Constants.IS_DEBUG) {
            MobclickAgent.setDebugMode(true);
            Bugtags.start("db5a961fbcaa9692b954f7a845670e2b", this, Bugtags.BTGInvocationEventBubble);
        }

        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
        // 需要友盟错误统计功能
        MobclickAgent.setCatchUncaughtExceptions(true);
        // 设置启动统计的间隔10s
        MobclickAgent.setSessionContinueMillis(10000);

        MobclickAgent.UMAnalyticsConfig uMAnalyticsConfig = new MobclickAgent.UMAnalyticsConfig(getApplicationContext(), "56d578e567e58eacd800151e", AppInfoUtils.getChannelByMeta(getApplicationContext()));
        MobclickAgent.startWithConfigure(uMAnalyticsConfig);


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //Log.x call replaced
    }

    public void exitApp() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(getPackageName());

        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
