package com.learnbinary;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.InterstitialAd;
import com.learnbinary.user.UserPreferences;
import com.learnbinary.util.SystemUiHider;

import android.content.Context;
import android.content.DialogInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Timer;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BinaryBlitz extends Activity {

    private final static String LOG_TAG = BinaryBlitz.class.getSimpleName();

    // The amount of
    public static final int INTERVAL_PRACTICE = 3000, INTERVAL_EASY = 2500,
            INTERVAL_MODERATE = 2000, INTERVAL_HARD  = 1500;

    // The integer flags for various difficulties
    public static final int PRACTICE = 0, EASY = 1,
            MODERATE = 2, HARD = 3;

    private SystemUiHider mSystemUiHider;

    private ArrayList<Button> topButtons, bottomButtons;

    private InterstitialAd mInterstitialAd;

    private int targetNumber, playerNumber;

    private String binarySequence;

    private Random random;

    private int iteration, level, interval;

    private Timer timer, vanishTimer;

    private boolean playing, isResumed;

    private boolean ableToUnclick, canSeeScore, mShowAds, mLevelsUnlocked;

    private TextView playerNumberDisplay, targetNumberDisplay ,
            levelView ,victoryMessage , difficultyText, previousLabel;

    final private String difficultyMessage = "Difficulty Increased : ";
    final private String vanishGoalMessage = "Goal Vanishes";
    final private String valueDisappearMessage = "Values Disappear";
    final private String unclickMessage = "Unclicking Disabled";
    final private String scoreMessage = "Score Hidden";

    private OnTouchListener listener;

    private int baseInterval, difficulty;

    private ArrayList<TextView> labels;

    private IInAppBillingService mService;

    private Context mContext;

    private UserPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = this;
        mShowAds = true;
        mLevelsUnlocked = false;

        // Load the user preferences.
        while(prefs == null)
            prefs = UserPreferences.getPreferences();

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_layout_blitz);

        random = new Random();

        topButtons = new ArrayList<>();
        labels = new ArrayList<>();
        bottomButtons=new ArrayList<>();

        for(int i=0; i<8 ;i++){
            // get top buttons
            String buttonName = "top_button";
            int id = getResources().getIdentifier(buttonName + i, "id", getPackageName());
            topButtons.add((Button) findViewById(id));

            // get bottom buttons
            buttonName = "bottom_button";
            id = getResources().getIdentifier(buttonName + i, "id", getPackageName());
            bottomButtons.add((Button) findViewById(id));


            String labelName = "label_number";
            id = getResources().getIdentifier(labelName + i, "id", getPackageName());
            labels.add((TextView)findViewById(id));
        }


        playerNumberDisplay = (TextView)findViewById(R.id.player_number_display);
        targetNumberDisplay = (TextView)findViewById(R.id.target_number_display);

        playing = false;

        levelView = (TextView)findViewById(R.id.level_view);
        victoryMessage = (TextView)findViewById(R.id.victory_view);

        difficultyText = (TextView)findViewById(R.id.difficulty_view);

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
                            BinaryBlitz.this.onTouch(button);
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
        difficulty = 0;
        // TODO : get from user prefs
        switch (difficulty) {
            case PRACTICE:
                baseInterval = INTERVAL_PRACTICE;
                break;

            case EASY:
                baseInterval = INTERVAL_EASY;
                break;

            case MODERATE:
                baseInterval = INTERVAL_MODERATE;
                break;

            case HARD:
                baseInterval = INTERVAL_HARD;
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

        /*
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
*/
    }


    /**
     * Determines if given points are inside view. Used for the swipe
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

    /**
     * The method used by the Start button to start playing. If the user is already playing the
     * game then a Toast will be made instructing them.
     * @param v
     */
    public void onStart(View v){
        if(playing) {
            Toast toast = Toast.makeText(this , "Press Submit before pressing play!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }


        if(!difficultyText.getText().equals(""))
            difficultyText.setText("");

        // clear the top button
        for(Button button : topButtons)
            button.setText("");

        // Set the bottom buttons' text to 0
        for(Button button : bottomButtons)
            button.setText("0");

        //
        for(TextView label : labels)
            label.setTypeface(null, Typeface.NORMAL);


        playerNumberDisplay.setText("0");
        victoryMessage.setText("");

        playing = true;
        playerNumber = 0;
        interval = getInterval();
        iteration = 0;
        targetNumber = random.nextInt(255);
        targetNumberDisplay.setText(""+targetNumber);
        binarySequence=Integer.toBinaryString(targetNumber);

        if(binarySequence.length()<8)
            for(int i=binarySequence.length();i<8;i++)
                binarySequence = "0" + binarySequence;



        labels.get(0).setTypeface(null, Typeface.BOLD);

        Button temp = topButtons.get(7);
        String text = binarySequence.charAt(0)+"";
        temp.setText(text);
        setTimer();

        if(level > 20)
            vanishGoalTimer();

        if(!canSeeScore)
            playerNumberDisplay.setText("???");

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
                runOnUiThread(() -> {
                    onUpdate();
                });
            }
        },getInterval() ,getInterval());

    }

    private boolean vTimerRunning;
    private void vanishGoalTimer(){
        vanishTimer = new Timer();
        vTimerRunning = true;
        vanishTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    targetNumberDisplay.setText("???");
                    vanishTimer.purge();
                    vanishTimer.cancel();
                    vTimerRunning = false;
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

    /**
     * This method records the user swipes so that the buttons can be toggled.
     * @param v
     */
    public void onTouch(View v){
        if(!playing)
            return;

        int id=Integer.parseInt((String)v.getTag());
        int num = (int) Math.pow(2,id);
        Button button=(Button) v;
        if(button.getText().equals("0")){
            button.setText("1");
            this.playerNumber+=num;
        } else if(ableToUnclick){
            button.setText("0");
            this.playerNumber-=num;
        }

        if(canSeeScore)
            this.playerNumberDisplay.setText(playerNumber+"");


    }

    /**
     * Returns the interval in milliseconds between game steps
     * @return  interval an int of the milliseconds between the steps
     */
    private int getInterval(){
        if(level<=100)
            return (int)(Math.pow(0.97,level % 20) * baseInterval);

        return (int)(Math.pow(0.96,level - 100) * baseInterval);

    }

    /**
     *
     */
    private void onUpdate(){

        if(!playing)
            return;

        // TODO : replace with a method
        Button temp = topButtons.get(7 - iteration);
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
    private void blankLabels(){
        for(TextView label : labels){
            label.setText("");
        }
    }

    /**
     *
     * @param v
     */
    public void submitClick(View v){
        if(!playing){
            String needToPlay = getResources().getString(R.string.start_playing_message);
            Toast toast = Toast.makeText(getApplicationContext() , needToPlay , Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        endRound();
    }

    /**
     * Request a new ad to show.
     */
    private void requestNewInterstitial() {
        /*
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);*/
    }

    /**
     *
     */
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

            levelView.setText("Level " + level);

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
            String text =(int)Math.pow(2 , 7 - i)+"";
            label.setText(text);
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
