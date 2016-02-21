package com.learnbinary;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Ethan on 2/5/14.
 */
public class DifficultySelection extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_difficulty_selection);
    }

    public void onClick(View v){
        Intent i = this.getIntent();
        int destination = i.getFlags();
        Intent intent = new Intent();
        if(destination == 0){
        intent = new Intent(this, BinaryBlitz.class);
        } else if(destination == 1) {
            intent = new Intent(this, HexBlitz.class);
        }
        intent.addFlags(Integer.parseInt((String)v.getTag()));
        startActivity(intent);
    }
}
