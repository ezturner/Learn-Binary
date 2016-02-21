package com.learnbinary;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Ethan on 2/20/2016.
 */
public class LearnBinary extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }
}
