package com.futurice.festapp.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    /**
     * Returns the value for the given key.
     *
     * @param context
     * @param key
     * @return
     */
    public static String getValue(Context context, String key) {
        try {
            return getSharedPreferences(context).getString(key, null);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Stores the given value.
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static void setValue(Context context, String key, String value) {
        try {
            getSharedPreferences(context).edit().putString(key, value).apply();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes the value for the given key.
     *
     * @param context
     * @param key
     */
    public static void removeValue(Context context, String key) {
        try {
            getSharedPreferences(context).edit().remove(key).apply();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the SharedPreferences.
     *
     * @param context
     * @return
     */
    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}