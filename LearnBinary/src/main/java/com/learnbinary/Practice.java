package com.learnbinary;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

/**
 * Created by Ethan on 2/19/14.
 */
public class Practice extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_selection);
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
