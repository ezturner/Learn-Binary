package com.learnbinary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * An utility class for storing things in Shared Preferences.
 * Created by ezturner on 8/14/2015.
 */
public class PrefUtils {
    public static final String HAS_OPENED_APP = "__OPENED__" ;
    public static final String DIFFICULTY = "__DIFFICULTY__";

    /**
     * Called to save supplied value in shared preferences against given key.
     * @param context Context of caller activity
     * @param key Key of value to save against
     * @param value Value to save
     */
    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.apply();
    }

    /**
     * Called to retrieve required value from shared preferences, identified by given key.
     * Default value will be returned of no value found or error occurred.
     * @param context Context of caller activity
     * @param key Key to find value against
     * @param defaultValue Value to return if no data found against given key
     * @return Return the value found against given key, default if not found or any error occurs
     */
    public static String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static void deleteFromPrefs(Context context, String key){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        try{
            sharedPrefs.edit().remove(key).commit();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}