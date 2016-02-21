package com.learnbinary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

/**
 * Created by Ethan on 11/25/13.
 */
public class MainMenu extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void toAbout(View view){
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    public void toTools(View view){
        Intent intent = new Intent(this, Tools.class);
        startActivity(intent);
    }


    public void toPractice(View view){
        Intent intent = new Intent(this, Practice.class);
        startActivity(intent);
    }

    public void toBuy(View view){
        Intent intent = new Intent(this, IAPActivity.class);
        startActivity(intent);
    }
}
