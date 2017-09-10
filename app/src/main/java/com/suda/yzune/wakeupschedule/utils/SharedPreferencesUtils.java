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

    public static String getStringFromSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getString(key, "");
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

}
