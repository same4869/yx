package com.ml.yx.comm;

/**
 * 常量配置类
 *
 * @author Lijj
 */

public class Constants {


    public static final boolean IS_TEST = false;
    public static final boolean IS_DEBUG = false;


    public static final String SCHEME = "yx://youxin";
    public static final String SCHEME_VIEW = "yx://youxin?view=";

    /**
     * 存储目录/文件
     **/
    public static final String WENBA_DIR = "/yx";
    public static final String WENBA_CACHE = WENBA_DIR + "/cache";
    public static final String WENBA_IMAGES = WENBA_DIR + "/images";
    public static final String WENBA_AUDIO = WENBA_DIR + "/audio";
    public static final String WENBA_LOGS = WENBA_DIR + "/logs";
    public static final String WENBA_EVENTS = WENBA_DIR + "/events";
    public static final String WENBA_S = WENBA_DIR + "/logs";
    public static final String TAKE_CAMERA_PIC_PATH = "camera_pic_temp";
    public static final String TAKE_AUDIO_PATH = "audio_mp3_temp";
    public static final String UFILE_HEADER = "u-";
    public static final String SCAN_HEADER = "scan-";

    public static final String APP_ID = "wx5d2348adcd22c2bc";
    public static final String APP_SECRET = "3c65115fdb007ba72200e8801d4fff7a";


    public final static String RECEIVER_FULL_USER_INFO = "full_user_info";
    public final static String RECEIVER_START_WORK = "start_work";


    public static final String BROADCAST_USER_NOT_LOGIN = "com.ml.yx.broadcast.user_not_login";// 用户没有登录广播

}
