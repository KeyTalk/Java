## Class  :  proguard-rules.pro
#
# Created by : KeyTalk IT Security BV on 2017
# All rights reserved @ keytalk.com
#

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\AndroidDevelopment\AndroidSDK/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt

-optimizations !code/simplification/arithmetic


-dontwarn android.support.**
-dontwarn javax.activation.**

# Hold onto the mapping.text file, it can be used to unobfuscate
-printmapping mapping.txt

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService


#must
-keep class org.spongycastle.** { *; }
-keep class android.webkit.** { *; }
-keep class android.security.** { *; }
-keep class java.security.** { *; }
-keep class com.org.keytalk.view.activities.WebViewActivity$* { *; }
-keep class com.org.keytalk.core.security.KMWebViewClient { *; }
-keep class com.org.keytalk.core.security.KMWebViewClientForJellyBean { *; }




-keepattributes Signature

-ignorewarnings

# Gson specific classes
-keep class sun.misc.Unsafe { *; }

-keepclasseswithmembernames class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembernames class com.org.keytalk.core.security.KeyTalkCommunicationManager {
    public <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


