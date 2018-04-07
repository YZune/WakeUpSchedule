package com.suda.yzune.wakeupschedule.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by YZune on 2017/9/8.
 */

public class SharedPreferencesUtils {

    public static void saveStringToSP(Context context, String key, String str) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, str);
        editor.apply();
    }

    public static String getStringFromSP(Context context, String key, String defaultString) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getString(key, defaultString);
    }

    public static void saveBooleanToSP(Context context, String key, boolean t) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, t);
        editor.apply();
    }

    public static boolean getBooleanFromSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getBoolean(key, true);
    }

    public static boolean getBooleanFromSP(Context context, String key, boolean defaultBoolean) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getBoolean(key, defaultBoolean);
    }

    public static void saveIntToSP(Context context, String key, int i){
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, i);
        editor.apply();
    }

    public static int getIntFromSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static int getIntFromSP(Context context, String key, int defaultInt) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getInt(key, defaultInt);
    }

    public static void clean(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
