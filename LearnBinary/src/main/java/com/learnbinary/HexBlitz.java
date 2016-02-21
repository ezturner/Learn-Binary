package com.learnbinary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ethan on 2/19/14.
 */
public class HexBlitz extends Activity{

    private int baseInterval = 250;
    private int interval;
    private int goal;
    private String hexGoal;
    private int currentNumber;
    private ProgressBar progressBar;
    private Timer timer;
    private double progress;
    private boolean isPlaying;
    private int level;
    private int difficulty;

    private Random random;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if(text.length()>2){
                String toSet = s.toString().substring(0,1);
                countDisplay.setText(toSet);
            }
            if(text.equals("")){
                currentNumber = 0;
                countDisplay.setText(currentNumber+"");
            } else {
                currentNumber = Integer.parseInt(text, 16);
                countDisplay.setText(currentNumber+"");
            }
        }
    };

    private EditText inputText;
    private TextView hexDisplay;
    private TextView goalDisplay;
    private TextView message;
    private TextView levelIndicator;
    private TextView countDisplay;
    @Override
    public void onCreate(Bundle savedInstanceState){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hex_blitz);

        initializeVariables();
    }

    private void initializeVariables(){
        progressBar=(ProgressBar)findViewById(getResources().getIdentifier("progressBar", "id" , getPackageName()));

        inputText=(EditText)findViewById(getResources().getIdentifier("inputText", "id" , getPackageName()));

        hexDisplay =(TextView)findViewById(getResources().getIdentifier("hex_representation", "id" , getPackageName()));

        goalDisplay =(TextView)findViewById(getResources().getIdentifier("goalDisplay", "id" , getPackageName()));
        levelIndicator =(TextView)findViewById(getResources().getIdentifier("level_indicator", "id" , getPackageName()));
        message =(TextView)findViewById(getResources().getIdentifier("message", "id" , getPackageName()));
        countDisplay =(TextView)findViewById(getResources().getIdentifier("countDisplay", "id" , getPackageName()));
        baseInterval = 100;
        isPlaying = false;
        progress = 0;
        level = 0;
        goal = 0;
        currentNumber = 0;
        inputText.addTextChangedListener(watcher);

        random = new Random();

        Intent i = this.getIntent();
        int flag = i.getFlags();
        difficulty = flag;
        switch(difficulty){
            //Practice
            case 0:
                baseInterval = 500;
                break;
            //Easy
            case 1:
                baseInterval = 350;
                break;
            //Moderate
            case 2:
                baseInterval = 250;
                break;
            //Hard
            case 3:
                baseInterval = 150;
                break;
        }
    }


    private void updateProgressBar(){
        progress+=0.01;

        if(progress >= 1){
            roundDone();
            System.out.println("ROUND OVER");
        }
        double progressBarProgress = this.progressBar.getMax() * progress;
        this.progressBar.setProgress((int) progressBarProgress);
    }

    private void roundDone(){
        if(isPlaying){
            isPlaying = false;
            timer.purge();
            timer.cancel();

            if(currentNumber == goal){
                level+=1;
                message.setText("Level Won!");
            } else {
                level = 0;
                message.setText("Level Failed");
            }
            levelIndicator.setText(level+"");
            progressBar.setProgress(0);
            progress = 0;
        }
    }

    public void onPause(){
        super.onPause();
        if(isPlaying) {
            timer.cancel();
            timer.purge();
        }
    }

    public void onResume(){
        super.onResume();
        if(isPlaying)
            startTimer();
    }

    public void onStartClick(View v){
        isPlaying = true;
        startTimer();
        goal = random.nextInt(255);
        hexGoal = Integer.toHexString(goal);
        goalDisplay.setText(goal+"");
        message.setText("");
    }

    private int getInterval(){
        return (int) (baseInterval * Math.pow(0.95,level));
    }

    public void onSubmitClick(View v){
        roundDone();
    }

    private void startTimer(){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressBar();
                    }
                });
            }
        };
        System.out.println(getInterval());
        timer.schedule(task,0,getInterval());
    }

    private AlertDialog dialog;
    public void onHelpClick(View v){
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Instructions");
        dialog.setMessage("To win, make the number on the bottom right match that above it. \n" +
                "To do this, type the number in Hexidecimal form in the box on the left. \n" +
                "In hexidecimal the characters range from 0-9 and A-F, with 0 being 0, 9 being 9, A being 10," +
                "and so on, with F being 15.\n" +
                "There are two digits, the first of which is multiplied by 16, and the second is merely its value added, so 10 is 16 (1*16 + 0), 11 is 17 (1*16 + 1), and 24 is 36 (2*16 + 4).");



        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Okay",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface log,int id) {
                // if this button is clicked, close
                // current activity
                dialog.cancel();
            }
        });
        dialog.show();
    }

}
