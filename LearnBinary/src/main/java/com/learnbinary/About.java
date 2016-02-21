package com.learnbinary;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.content.Intent;

/**
 * Created by Ethan on 11/25/13.
 */
public class About  extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);

    }
}
