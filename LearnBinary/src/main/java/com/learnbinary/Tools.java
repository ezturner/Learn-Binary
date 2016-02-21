package com.learnbinary;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Ethan on 2/19/14.
 */
public class Tools extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tools);
    }

    public void toBinaryConverter(View v){
        Intent intent = new Intent(this, BinaryConverter.class);//fix this
        startActivity(intent);
    }
}
