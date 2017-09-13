package com.keytalk.nextgen5.util;

import java.util.Set;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
 * Class  :  PreferenceManager
 * Description : Shared Preference class.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public final class PreferenceManager {

    private static final String PREF_NAME = "this_is_my_keytalk";

    private PreferenceManager() {};

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static Editor getPreferenceEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static String getString(Context context, String key) {
        return getSharedPreferences(context).getString(key, null);
    }

    public static int getInt(Context context, String key) {
        return getSharedPreferences(context).getInt(key, -1);
    }

    public static boolean getBoolean(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }


    public static long getLong(Context context, String key) {
        return getSharedPreferences(context).getLong(key, -1);
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defaultValue) {
        return getSharedPreferences(context).getStringSet(key,  defaultValue);
    }

    public static void put(Context context, String key, String value) {
        Editor editor = getPreferenceEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    public static void put(Context context, String key, int value) {
        Editor editor = getPreferenceEditor(context);
        editor.putInt(key, value);
        editor.commit();
    }

    public static void put(Context context, String key, boolean value) {
        Editor editor = getPreferenceEditor(context);
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void put(Context context, String key, long value) {
        Editor editor = getPreferenceEditor(context);
        editor.putLong(key, value);
        editor.commit();
    }
    public static void put(Context context, String key, Set<String> value) {
        Editor editor = getPreferenceEditor(context);
        editor.putStringSet(key, value);
        editor.commit();
    }
}
