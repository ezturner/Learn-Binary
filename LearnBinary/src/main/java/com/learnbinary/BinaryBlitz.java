package com.learnbinary;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.InterstitialAd;
import com.learnbinary.util.SystemUiHider;

import android.content.Context;
import android.content.DialogInterface;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Timer;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BinaryBlitz extends Activity {


    private SystemUiHider mSystemUiHider;

    private ArrayList<Button> topButtons;

    private ArrayList<Button> bottomButtons;

    private TextView topNumber;
    private TextView bottomNumber;

    InterstitialAd mInterstitialAd;
    private int targetNumber;
    private int playerNumber;

    private int etIsABitch = 69;

    private String binarySequence;

    private Random random;

    private int iteration;

    private int level;

    private int interval;

    private Timer timer;
    private Timer vanishTimer;

    private boolean playing;
    private boolean isResumed;

    private boolean ableToUnclick;
    private boolean canSeeScore;

    private TextView playerNumberDisplay;
    private TextView targetNumberDisplay;
    private TextView victoryDisplay;
    private TextView victoryMessage;
    private TextView difficultyText;

    final private String difficultyMessage = "Difficulty Increased : ";
    final private String vanishGoalMessage = "Goal Vanishes";
    final private String valueDisappearMessage = "Values Disappear";
    final private String unclickMessage = "Unclicking Disabled";
    final private String scoreMessage = "Score Hidden";

    private OnTouchListener listener;

    private int baseInterval;
    private int difficulty;


    private boolean mShowAds;
    private boolean mLevelsUnlocked;

    private ArrayList<TextView> labels;

    private IInAppBillingService mService;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = this;
        mShowAds = true;
        mLevelsUnlocked = false;

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_blitz);

        random=new Random();

        topButtons=new ArrayList<Button>();

        for(int i=1;i<9;i++){
            topButtons.add((Button) findViewById(getResources().getIdentifier("topbutton" + i, "id", getPackageName())));
        }

        bottomButtons=new ArrayList<Button>();

        for(int i=1;i<9;i++){
            bottomButtons.add((Button)findViewById(getResources().getIdentifier("bottomButton"+i, "id" , getPackageName())));
        }

        labels = new ArrayList<TextView>();

        for(int i=0;i<8;i++){
            labels.add((TextView)findViewById(getResources().getIdentifier("buttonNumber"+i, "id" , getPackageName())));
        }

        playerNumberDisplay=(TextView)findViewById(getResources().getIdentifier("playerNumberDisplay", "id" , getPackageName()));
        targetNumberDisplay=(TextView)findViewById(getResources().getIdentifier("targetNumberDisplay", "id" , getPackageName()));

        topNumber=(TextView)findViewById(getResources().getIdentifier("textView10", "id" , getPackageName()));
        bottomNumber=(TextView)findViewById(getResources().getIdentifier("textView9","id", getPackageName()));
        playing = false;

        victoryDisplay = (TextView)findViewById(getResources().getIdentifier("victoryText","id", getPackageName()));
        victoryMessage = (TextView)findViewById(getResources().getIdentifier("victoryMessage","id", getPackageName()));

        difficultyText = (TextView)findViewById(getResources().getIdentifier("difficultyText","id", getPackageName()));

        level = 0;
        vTimerRunning = false;
        ableToUnclick = true;
        canSeeScore = true;
        listener = new OnTouchListener() {
            private ArrayList<Integer> buttonsPressed;
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    buttonsPressed = new ArrayList<Integer>();
                    checkTouchPosition(event);
                } else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    checkTouchPosition(event);

                }
                return false;
            }

            private void checkTouchPosition(MotionEvent event){
                for(Button button : bottomButtons){

                    if(isPointInsideView(event.getRawX(), event.getRawY(), button) && !buttonsPressed.contains(Integer.parseInt((String)button.getTag()))){
                        if(ableToUnclick || !ableToUnclick && button.getText().equals("0")){
                            onClick(button);
                            buttonsPressed.add(Integer.parseInt((String)button.getTag()));
                        }
                    }
                }
            }
        };

        for(Button button : bottomButtons){
            button.setOnTouchListener(listener);
        }

        Intent i = this.getIntent();
        int flag = i.getFlags();
        difficulty = flag;
        switch(difficulty){
            //Practice
            case 0:
                baseInterval = 3000;
                break;
            //Easy
            case 1:
                baseInterval = 2500;
                break;
            //Moderate
            case 2:
                baseInterval = 2000;
                break;
            //Hard
            case 3:
                baseInterval = 1500;
                break;
        }

        //The ad code
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4175241364211124/6773561694");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        Bundle ownedItems = null;
        try {
            ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
        } catch(RemoteException e){
            e.printStackTrace();
        }

        int response = ownedItems.getInt("RESPONSE_CODE");
        if (response == 0) {
            ArrayList<String> ownedSkus =
                    ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String>  purchaseDataList =
                    ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String>  signatureList =
                    ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
            String continuationToken =
                    ownedItems.getString("INAPP_CONTINUATION_TOKEN");

            for (int d = 0; d < purchaseDataList.size(); ++d) {
                String purchaseData = purchaseDataList.get(d);
                String signature = signatureList.get(d);
                String sku = ownedSkus.get(d);

                if(sku.equals("noads")){
                    mShowAds = false;
                } else if(sku.equals("levelunlock")){
                    mLevelsUnlocked = true;
                } else if(sku.equals("noadsandunlock")){
                    mShowAds = false;
                    mLevelsUnlocked = true;
                }
                // do something with this purchase information
                // e.g. display the updated list of products owned by user
            }

            // if continuationToken != null, call getPurchases again
            // and pass in the token to retrieve more items
        }

    }


    /**
     * Determines if given points are inside view
     * @param x - x coordinate of point
     * @param y - y coordinate of point
     * @param view - view object to compare
     * @return true if the points are within view bounds, false otherwise
     */
    private boolean isPointInsideView(float x, float y, View view){
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        if(( x > viewX && x < (viewX + view.getWidth())) &&
                ( y > viewY && y < (viewY + view.getHeight()))){
            return true;
        } else {
            return false;
        }
    }

    public void onPlay(View v){
        if(playing){
            return;
        }

        if(!difficultyText.getText().equals("")){
            difficultyText.setText("");
        }

        for(Button button : topButtons){
            button.setText("");
        }

        for(Button button : bottomButtons){
            button.setText("0");
        }

        for(TextView view : labels){
            view.setTypeface(null,Typeface.NORMAL);
        }

        playerNumberDisplay.setText("0");
        victoryMessage.setText("");

        playing = true;
        playerNumber = 0;
        interval = getInterval();
        iteration = 0;
        targetNumber = random.nextInt(255);
        targetNumberDisplay.setText(""+targetNumber);
        binarySequence=Integer.toBinaryString(targetNumber);
        if(binarySequence.length()<8){
            for(int i=binarySequence.length();i<8;i++){
                binarySequence = "0" + binarySequence;
            }
        }
        labels.get(0).setTypeface(null, Typeface.BOLD);
        Button temp = topButtons.get(0);
        String text = binarySequence.charAt(0)+"";
        temp.setText(text);
        setTimer();
        if(level > 20){
            vanishGoalTimer();
        }

        if(!canSeeScore){
            playerNumberDisplay.setText("???");
        }


    }

    public void onResume(){
        super.onResume();
        if(playing)
            setTimer();
    }

    private void setTimer(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onUpdate();
                    }
                });
            }
        },0,getInterval());

    }

    private boolean vTimerRunning;
    private void vanishGoalTimer(){
        vanishTimer = new Timer();
        vTimerRunning = true;
        vanishTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        targetNumberDisplay.setText("???");
                        vanishTimer.purge();
                        vanishTimer.cancel();
                        vTimerRunning = false;
                    }
                });
            }
        }, 3500);
    }


    public void onRestart(){
        super.onRestart();
    }

    public void onStop(){
        super.onStop();
    }

    public void onPause(){
        super.onPause();
        if(playing){
           timer.cancel();
            timer.purge();
        }
    }

    public void onStart(){
        super.onStart();
    }

    public void onClick(View v){
        if(playing){
            int id=Integer.parseInt((String)v.getTag());
            int num=(int)Math.pow(2,id);
            Button button=(Button) v;
            if(button.getText().equals("0")){
                button.setText("1");
                this.playerNumber+=num;
            } else if(ableToUnclick){
                button.setText("0");
                this.playerNumber-=num;
            }
            if(canSeeScore){
                this.playerNumberDisplay.setText(playerNumber+"");
            }
        }
    }

    private int getInterval(){

        if(level<=100){
            return (int)(Math.pow(0.97,level % 20) * baseInterval);
        } else {
            return (int)(Math.pow(0.98,level - 100) * baseInterval);
        }
    }

    private TextView previousLabel;

    private void onUpdate(){

        if(playing){
            Button temp = topButtons.get(iteration);
            String tempS = binarySequence.charAt(iteration)+"";

            temp.setText(tempS);

            if(previousLabel != null)
                previousLabel.setTypeface(null,Typeface.NORMAL);

            previousLabel = labels.get(iteration);
            previousLabel.setTypeface(null, Typeface.BOLD);

            iteration++;
            if(iteration == 8 && difficulty != 0){
                endRound();
            } else if(iteration == 8){
                timer.cancel();
                if(vTimerRunning){
                    vTimerRunning = false;
                    vanishTimer.cancel();
                    vanishTimer.purge();
                }
            }
        }

    }
    private void blankLabels(){
        for(TextView label : labels){
            label.setText("");
        }
    }

    public void submitClick(View v){
        endRound();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void endRound(){
        if(random.nextInt(4) == 1 && mShowAds){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                if(random.nextInt(3) == 1){
                    this.onRemoveAds();
                }
            }
        }

        if(playing){

            timer.cancel();
            playing = false;
            boolean hasWon = ( playerNumber == targetNumber );
            isResumed = false;

            if(hasWon){
                level++;
                if(level == 21){
                    difficultyText.setText(difficultyMessage + vanishGoalMessage);
                } else if(level == 41){
                    difficultyText.setText(difficultyMessage + valueDisappearMessage);
                    blankLabels();
                } else if(level == 61){
                    difficultyText.setText(difficultyMessage + unclickMessage);
                    ableToUnclick = false;
                } else if(level == 81){
                    difficultyText.setText(difficultyMessage + scoreMessage);
                    canSeeScore = false;
                } else {
                    victoryMessage.setText("Level Completed!");
                }
                if(level > 21 && targetNumberDisplay.getText().equals("???") ){
                    targetNumberDisplay.setText("" + targetNumber);
                }

            }else {
                victoryMessage.setText("Level "+ level +" Failed");
                if(level >= 41){
                    resetLabels();
                }
                level = 0;

                ableToUnclick = true;
                canSeeScore = true;
            }

            victoryDisplay.setText("Level " + level);

            if(!canSeeScore){
                playerNumberDisplay.setText(playerNumber + "");
            }
            if(vTimerRunning){
                vTimerRunning = false;
                vanishTimer.cancel();
                vanishTimer.purge();
            }

            if(level >= 50 && !mLevelsUnlocked){
                level = 0;
                onBuyLevel();
            }
        }
    }

    private void resetLabels(){
        for(int i=0;i<8;i++){
            TextView label = labels.get(i);
            label.setText((int)Math.pow(2 , 7 - i)+"");
        }
    }
    public void onDestroy(){
        super.onDestroy();
        playing=false;
    }
    private AlertDialog dialog;
    public void onHelpClick(View v){
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Instructions");
        dialog.setMessage("To win the game, you have to make both the numbers on the right match. To do this, press the bottom buttons, which will add the amount listed above it to your total. ");

        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Okay",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface log,int id) {
                // if this button is clicked, close
                // current activity
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void onBuyLevel(){
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Unlock More Levels!");
        dialog.setMessage("Unlock levels 50+ in the Store!");

        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Sure!",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface log,int id) {
                Intent intent = new Intent(mContext , IAPActivity.class);
                startActivity(intent);
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,"No Thanks",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface log,int id) {
                // if this button is clicked, close
                // current activity
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void onRemoveAds(){
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Remove Ads!");
        dialog.setMessage("Remove Ads in the Store!");

        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Sure!",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface log,int id) {
                Intent intent = new Intent(mContext , IAPActivity.class);
                startActivity(intent);
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,"No Thanks",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface log,int id) {
                // if this button is clicked, close
                // current activity
                dialog.cancel();
            }
        });
        dialog.show();
    }
}