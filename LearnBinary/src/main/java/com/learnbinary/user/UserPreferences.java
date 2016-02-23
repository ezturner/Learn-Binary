package com.learnbinary.user;

import android.content.Context;

import com.learnbinary.util.PrefUtils;

/**
 * Created by ezturner on 2/22/2016.
 */
public class UserPreferences {


    private static UserPreferences preferences;
    private Context context;
    private boolean hasOpenedApp = false;
    private int difficulty = 0;

    public static UserPreferences getPreferences(){
        return preferences;
    }

    public UserPreferences(Context context){
        preferences = this;
        this.context = context;
        loadPrefs();
    }

    private void loadPrefs(){
        String opened = PrefUtils.getFromPrefs(context , PrefUtils.HAS_OPENED_APP , "no");
        hasOpenedApp = opened.equals("yes");

        String difficulty = PrefUtils.getFromPrefs(context, PrefUtils.DIFFICULTY , "0");
        try{
            this.difficulty = Integer.parseInt(difficulty);
        } catch (Exception e){
            this.difficulty = 0;
        }
    }


    public int getDifficulty(){
        return difficulty;
    }

    public boolean hasOpenedApp(){
        return hasOpenedApp;
    }




}
