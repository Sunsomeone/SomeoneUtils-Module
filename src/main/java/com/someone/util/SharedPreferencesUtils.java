package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/11/10 08:45
 */

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SharedPreferencesUtils {

    public static void putPrivateBooleanData(String key, boolean value) {
        if (key != null) {
            SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        } else {
            throw new NullPointerException("SharedPreferences key can't be null");
        }
    }

    public static void putPrivateIntegerData(String key, Integer value) {
        if (key != null && value != null) {
            SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
        } else {
            throw new NullPointerException("SharedPreferences key or value can't be null");
        }
    }

    public static void putPrivateLongData(String key, long value) {
        if (key != null) {
            SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(key, value);
            editor.apply();
        } else {
            throw new NullPointerException("SharedPreferences key can't be null");
        }
    }

    public static void putPrivateFloatData(String key, float value) {
        if (key != null) {
            SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(key, value);
            editor.apply();
        } else {
            throw new NullPointerException("SharedPreferences key can't be null");
        }
    }

    public static void putPrivateStringData(String key, String value) {
        if (key != null && value != null) {
            SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        } else {
            throw new NullPointerException("SharedPreferences key or value can't be null");
        }
    }

    public static void putPrivateStringSetData(String key, Set<String> value) {
        if (key != null && value != null) {
            SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(key, value);
            editor.apply();
        } else {
            throw new NullPointerException("SharedPreferences key or value can't be null");
        }
    }

    public static boolean getPrivateBooleanData(String key) {
        return getPrivateBooleanData(key, false);
    }

    public static boolean getPrivateBooleanData(String key, boolean defaultReturn) {
        SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultReturn);
    }

    @NonNull
    public static Integer getPrivateIntegerData(String key) {
        return getPrivateIntegerData(key, -1);
    }

    @NonNull
    public static Integer getPrivateIntegerData(String key, int defaultReturn) {
        SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultReturn);
    }

    public static long getPrivateLongData(String key) {
        return getPrivateLongData(key, -1);
    }

    public static long getPrivateLongData(String key, long defaultReturn) {
        SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultReturn);
    }

    public static float getPrivateFloatData(String key) {
        return getPrivateFloatData(key, -1f);
    }

    public static float getPrivateFloatData(String key, float defaultReturn) {
        SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, defaultReturn);
    }

    public static String getPrivateStringData(String key) {
        return getPrivateStringData(key, "");
    }

    public static String getPrivateStringData(String key, String defaultReturn) {
        SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultReturn);
    }

    public static Set<String> getPrivateStringSetData(String key) {
        return getPrivateStringSetData(key, new LinkedHashSet<String>());
    }

    public static Set<String> getPrivateStringSetData(String key, Set<String> defaultReturn) {
        SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(key, defaultReturn);
    }

    public static Map<String, ?> getPrivateAllData(String key) {
        SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }

    public static void removePrivateData(String key) {
        SharedPreferences sharedPreferences = GlobalContextUtil.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
