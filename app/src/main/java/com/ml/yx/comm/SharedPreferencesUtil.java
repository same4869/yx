package com.ml.yx.comm;

import android.content.Context;
import android.content.SharedPreferences;

import com.ml.yx.YouXinApplication;

/**
 * Created by Lijj on 16/4/13.
 */
public class SharedPreferencesUtil {
    private static final String tbl = "yx_sp";

    private static final String USER_TOKEN = "user_token";
    private static final String USER_ID = "user_id";
    private static final String COACH_ID = "coach_id";
    private static final String COACH_NAME = "coach_name";
    private static final String MESSAGE_LAST_UPDATE_TIME = "message_last_update_time";
    private static final String NEW_MESSAGE = "new_message";
    private static final String SCHEME = "scheme";
    private static final String SEX = "sex";
    private static final String SUBJECT = "subject";
    private static final String IS_DOWNLOADING = "is_downloading";
    private static final String TOTLE_LEVEL = "totle_level";
    private static final String ADD_NEW_LEVEL_TYPE = "add_new_level"; //定制新阶段时，选择目标天数跳转的页面逻辑不一样，做一个简单标识，因为传值更加复杂
    //0为正常，1为点击开启新阶段，2为点返回到关卡列表再点+号

    private static SharedPreferences getSharedPreferences() {
        return YouXinApplication.getInstance().getSharedPreferences(tbl, Context.MODE_PRIVATE);
    }

    public static int getAddNewLevelType() {
        return getInt(ADD_NEW_LEVEL_TYPE, 0);
    }

    public static void setAddNewLevelType(int addNewLevelType) {
        putInt(ADD_NEW_LEVEL_TYPE, addNewLevelType);
    }

    public static boolean isDownloading() {
        return getBoolean(IS_DOWNLOADING, false);
    }

    public static void setIsDownloading(boolean isDownloading) {
        putBoolean(IS_DOWNLOADING, isDownloading);
    }

    public static int getTotleLevel() {
        return getInt(TOTLE_LEVEL, 1);
    }

    public static void setTotleLevel(int totleLevel) {
        putInt(TOTLE_LEVEL, totleLevel);
    }

    public static int getSex() {
        return getInt(SEX, -1);
    }

    public static void setSex(int sex) {
        putInt(SEX, sex);
    }

    public static int getSubject() {
        return getInt(SUBJECT, 0);
    }

    public static void setSubject(int subject) {
        putInt(SUBJECT, subject);
    }

    public static String getUserToken() {
        return getString(USER_TOKEN, null);
    }

    public static void setUserToken(String token) {
        putString(USER_TOKEN, token);
    }

    public static int getInstructorId() {
        return getInt(COACH_ID, 0);
    }

    public static void setInstructorId(int instructorId) {
        putInt(COACH_ID, instructorId);
    }

    public static String getInstructorName() {
        return getString(COACH_NAME, null);
    }

    public static void setInstructorName(String instructorName) {
        putString(COACH_NAME, instructorName);
    }

    public static long getMessageLastUpdateTime() {
        return getLong(MESSAGE_LAST_UPDATE_TIME, 0);
    }

    public static void setMessageLastUpdateTime(long lastUpdateTime) {
        putLong(MESSAGE_LAST_UPDATE_TIME, lastUpdateTime);
    }

    public static boolean hasNewMessage() {
        return getBoolean(NEW_MESSAGE, false);
    }

    public static void setNewMessage(boolean value) {
        putBoolean(NEW_MESSAGE, value);
    }

    public static String getScheme() {
        return getString(SCHEME, null);
    }

    public static void setScheme(String scheme) {
        putString(SCHEME, scheme);
    }

    public static String getUserId() {
        return getString(USER_ID, null);
    }

    public static void setUserId(String userId) {
        putString(USER_ID, userId);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        return getSharedPreferences().getLong(key, defaultValue);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


}
