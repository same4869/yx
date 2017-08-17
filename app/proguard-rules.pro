# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Lijj/Documents/tool/android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-ignorewarnings
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-libraryjars libs

-keep class org.apache.** {*; }
-keep class org.codehaus.jackson.** {*; }
-keep class com.fasterxml.jackson.databind.** {*; }
-keep class com.nineoldandroids.** {*; }
-keep class android.support.** {*; }
-keep class com.baidu.location.**{*;}
-keep class com.tencent.**{*;}
-keep class com.mob.**{*;}
-keep class com.umeng.**{*;}
-keep class io.netty.** {*;}
-keep class org.android.** {*;}
-keep class com.bugtags.** {*;}

-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.webkit.WebView
-keep public class * extends android.app.Dialog

-keepclassmembers class ** {
  	native <methods>;
}

-keepclassmembers class ** {
    public void onEvent*(**);
}

# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}


-keep public class * implements java.io.Serializable{
    <fields>;
}

-keepclassmembers class * extends android.webkit.WebChromeClient {
   public void openFileChooser(...);
}

-keep public enum * {
}

-keepattributes Exceptions, Signature, InnerClasses
