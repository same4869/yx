package com.ml.yx.comm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.text.TextUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppInfoUtils {
    private static String channel = null;
    private static int appVersion = -1;
    private static String mobileDeviceId;

    public static int getCurrentVersionCode(Context context) {
        if (appVersion == -1) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
                appVersion = pinfo.versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return appVersion;
    }

//    public static String getChannelByMeta(Context context) {
//        if (channel == null) {
//            try {
//                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//                channel = appInfo.metaData.getString("UMENG_CHANNEL");
//            } catch (NameNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//        return channel;
//    }

    public static String getChannelByMeta(Context context) {
        if (channel != null) {
            return channel;
        }
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith("META-INF/channel")) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] split = ret.split("_");
        if (split != null && split.length >= 2) {
            channel = ret.substring(split[0].length() + 1);
        }
        if (StringUtil.isBlank(channel)) {
            channel = "nochannel";
        }
        return channel;
    }

    public static String getMobileDeviceInfo(Context context) {
        if (mobileDeviceId != null) {
            return mobileDeviceId;
        }
        try {
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();

            if (TextUtils.isEmpty(imei)) {
                imei = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String macAddress = wifi.getConnectionInfo().getMacAddress();

            mobileDeviceId = StringUtil.md5(imei + macAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobileDeviceId;
    }

    public static String getCurrentVersionName(Context context) {
        String currentVersionName = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            currentVersionName = pinfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionName;
    }

    /**
     * 获得apk签名
     *
     * @param context
     * @param apkPath
     * @return 当签名无法获得，返回null
     */
    public static String getApkSignature(Context context, String apkPath) {
        if (context == null) {
            return "";
        }
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return "";
        }
        PackageInfo mInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
        if (mInfo != null) {
            if (mInfo.signatures != null && mInfo.signatures.length > 0) {
                Signature signatures = mInfo.signatures[0];
                try {
                    MessageDigest md2 = MessageDigest.getInstance("SHA1");
                    md2.update(signatures.toByteArray());
                    byte[] digest2 = md2.digest();
                    return toHexString(digest2);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {
                return null;// 当为null时，无法获得签名
            }
        }
        return "";
    }

    // 判断是否安装了应用
    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    private static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len - 1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }

    private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

}
