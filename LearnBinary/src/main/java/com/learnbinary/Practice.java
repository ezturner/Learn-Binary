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
public class Practice extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.practice);
    }

    public void onClickBinary(View v){
        Intent intent = new Intent(this, DifficultySelection.class);
        intent.setFlags(0);
        startActivity(intent);
    }

    public void onClickHex(View v){
        Intent intent = new Intent(this, DifficultySelection.class);
        intent.setFlags(1);
        startActivity(intent);
    }
}
