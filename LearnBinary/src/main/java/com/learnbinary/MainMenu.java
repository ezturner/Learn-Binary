package com.learnbinary;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.content.Intent;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Ethan on 11/25/13.
 */
public class MainMenu  extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.menu);

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
