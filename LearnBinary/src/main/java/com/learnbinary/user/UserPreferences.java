package com.learnbinary.user;

import android.content.Context;

/**
 * Created by ezturner on 2/22/2016.
 */
public class UserPreferences {


    private static UserPreferences preferences;
    private Context context;

    public static UserPreferences getPreferences(){
        return preferences;
    }

    public UserPreferences(Context context){
        this.context = context;
    }


}
