package com.ml.yx.comm;

import android.content.Context;
import android.text.format.DateUtils;

import com.ml.yx.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static final String tag = DateUtil.class.getSimpleName();

    private static long wenbaTime = System.currentTimeMillis();
    private static long localTime = System.currentTimeMillis();

    public static void setWenbaTime(long time) {
        wenbaTime = time;
        localTime = System.currentTimeMillis();
    }

    public static long getCurWenbaTime() {
        return System.currentTimeMillis() + (wenbaTime - localTime);
    }

    public static int getTimeHour(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        cal.get(Calendar.HOUR_OF_DAY);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static String getDateString2(Context c, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar curDate = Calendar.getInstance(Locale.CHINA);
        curDate.setTimeInMillis(DateUtil.getCurWenbaTime());
        Calendar date = Calendar.getInstance(Locale.CHINA);
        date.setTimeInMillis(time);

        String timeStr = sdf.format(new Date(time));
        timeStr = " " + timeStr;

        long day = date.get(Calendar.DAY_OF_YEAR);
        long today = curDate.get(Calendar.DAY_OF_YEAR);

        if (day == today) {
            return c.getString(R.string.today) + timeStr;
        } else if (day + 1 == today) {
            return c.getString(R.string.yesterday) + timeStr;
        } else if (day + 2 == today) {
            return c.getString(R.string.before_yesterday) + timeStr;
        } else {
            long week = time / DateUtils.WEEK_IN_MILLIS;
            long toWeek = System.currentTimeMillis() / DateUtils.WEEK_IN_MILLIS;
            if (week + 1 == toWeek) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(time);
                int weekIndex = cal.get(Calendar.DAY_OF_WEEK);
                String str = "";
                if (weekIndex == 1) {
                    str = "周日";
                } else if (weekIndex == 2) {
                    str = "周一";
                } else if (weekIndex == 3) {
                    str = "周二";
                } else if (weekIndex == 4) {
                    str = "周三";
                } else if (weekIndex == 5) {
                    str = "周四";
                } else if (weekIndex == 6) {
                    str = "周五";
                } else if (weekIndex == 7) {
                    str = "周六";
                }
                return str + timeStr;
            } else {
                long year = date.get(Calendar.YEAR);
                long toYear = curDate.get(Calendar.YEAR);
                SimpleDateFormat sdf2 = null;
                if (year == toYear) {
                    sdf2 = new SimpleDateFormat("MM.dd HH:mm", Locale.getDefault());
                } else {
                    sdf2 = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                }

                return sdf2.format(new Date(time));
            }
        }
    }

    public static String getDateString3(Context c, long time) {
        Calendar curDate = Calendar.getInstance(Locale.CHINA);
        curDate.setTimeInMillis(DateUtil.getCurWenbaTime());
        Calendar date = Calendar.getInstance(Locale.CHINA);
        date.setTimeInMillis(time);

        long day = date.get(Calendar.DAY_OF_YEAR);
        long today = curDate.get(Calendar.DAY_OF_YEAR);

        if (day == today) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return c.getString(R.string.today) + " " + sdf.format(new Date(time));
        } else {
            long year = date.get(Calendar.YEAR);
            long toYear = curDate.get(Calendar.YEAR);
            SimpleDateFormat sdf2 = null;
            if (year == toYear) {
                sdf2 = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            } else {
                sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            }

            return sdf2.format(new Date(time));
        }
    }

    public static String getDateString4(Context c, long time) {
        Calendar curDate = Calendar.getInstance(Locale.CHINA);
        curDate.setTimeInMillis(DateUtil.getCurWenbaTime());
        Calendar date = Calendar.getInstance(Locale.CHINA);
        date.setTimeInMillis(time);

        long day = date.get(Calendar.DAY_OF_YEAR);
        long today = curDate.get(Calendar.DAY_OF_YEAR);
        if (day == today) {
            return c.getString(R.string.today);
        } else {
            long year = date.get(Calendar.YEAR);
            long toYear = curDate.get(Calendar.YEAR);
            SimpleDateFormat sdf2 = null;
            if (year == toYear) {
                sdf2 = new SimpleDateFormat("MM月dd日", Locale.getDefault());
            } else {
                sdf2 = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
            }

            return sdf2.format(new Date(time));
        }
    }

    public static String getDateString5(Context c, long time) {
        Calendar curDate = Calendar.getInstance(Locale.CHINA);
        curDate.setTimeInMillis(DateUtil.getCurWenbaTime());
        Calendar date = Calendar.getInstance(Locale.CHINA);
        date.setTimeInMillis(time);

        long year = date.get(Calendar.YEAR);
        long toYear = curDate.get(Calendar.YEAR);
        SimpleDateFormat sdf2 = null;
        if (year == toYear) {
            sdf2 = new SimpleDateFormat("MM月", Locale.getDefault());
        } else {
            sdf2 = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
        }

        return sdf2.format(new Date(time));
    }

    public static Date getLastClockByHour(final Date date, final int lastHour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= lastHour) {
            cal.add(Calendar.DATE, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, lastHour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 按照指定的template返回特定格式的时间
     * @param c
     * @param time
     * @param template 格式 如:"yyyy年MM月dd日 HH:mm:ss"
     * @return
     */
    public static String getDateString(Context c, long time, String template) {
        Calendar curDate = Calendar.getInstance(Locale.CHINA);
        curDate.setTimeInMillis(DateUtil.getCurWenbaTime());
        Calendar date = Calendar.getInstance(Locale.CHINA);
        date.setTimeInMillis(time);
        SimpleDateFormat sdf = null;
        sdf = new SimpleDateFormat(template, Locale.getDefault());
        return sdf.format(new Date(time));
    }

}
