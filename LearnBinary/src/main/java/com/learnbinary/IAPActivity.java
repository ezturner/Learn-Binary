package com.learnbinary;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ezturner on 4/8/2015.
 */
public class IAPActivity extends Activity {

    private Bundle mQuerySkus;
    private final String mAdText = "Remove Ads - ";
    private final String mLevelText = "Unlock Levels 50+ - ";
    private final String mLevelAndAdText = "Unlock Levels 50+ and Remove Ads - ";

    private String mAdPrice;
    private String mLevelPrice;
    private String mLevelAndAdPrice;

    private Button mAdButton;
    private Button mLevelButton;
    private Button mLevelAdButton;

    private IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_iap_buy_screen);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        ArrayList<String> skuList = new ArrayList<String>();
        skuList.add("noads");
        skuList.add("levelunlock");
        skuList.add("noadsandunlock");
        mQuerySkus = new Bundle();
        String put = "ITEM_ID_LIST";
        mQuerySkus.putStringArrayList(put, skuList);

        mAdButton = (Button)findViewById(getResources().getIdentifier("removeads", "id" , getPackageName()));
        mLevelButton = (Button)findViewById(getResources().getIdentifier("unlocklevels", "id" , getPackageName()));
        mLevelAdButton = (Button)findViewById(getResources().getIdentifier("levelad", "id" , getPackageName()));

        getThread().start();
    }

    public Thread getThread(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle skuDetails = null;
                try {
                     skuDetails = mService.getSkuDetails(3,
                            getPackageName(), "inapp", mQuerySkus);
                } catch(RemoteException e){
                    e.printStackTrace();
                }
                int response = skuDetails.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> responseList
                            = skuDetails.getStringArrayList("DETAILS_LIST");

                    try {
                        for (String thisResponse : responseList) {
                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");
                            if (sku.equals("noads")) mAdPrice = price;
                            else if (sku.equals("levelunlock")) mLevelPrice = price;
                            else if (sku.equals("noadsunlock")) mLevelAndAdPrice = price;
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                    }

                    mAdButton.setText(mAdText + mAdPrice);
                    mLevelButton.setText(mLevelText + mLevelPrice);
                    mLevelAdButton.setText(mLevelAndAdText + mLevelAndAdPrice);


                }
            }
        });
    }

    //The purchase for the no ads $0.99 transaction
    public void noAds(View v){
        Bundle buyIntentBundle = null;
        try {
            buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    "noads", "inapp", "x82jhdf82Su29ejdfk");
        } catch(RemoteException e){

        }

        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

        try {
            startIntentSenderForResult(pendingIntent.getIntentSender(),
                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                    Integer.valueOf(0));
        } catch(IntentSender.SendIntentException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Log.d("IAP", "You have bought the " + sku + ". Excellent choice, adventurer!");
                }
                catch (JSONException e) {
                    Log.d("IAP", "Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }

    //The purchase for the $1.99 level unlock transaction
    public void levelUnlock(View v){

    }

    //the purchase for the $1.99 level unlock and ad removal IAP
    public void noAdsUnlock(View v){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }
}
