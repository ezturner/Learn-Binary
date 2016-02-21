package com.learnbinary;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by Ethan on 2/19/14.
 */
public class BinaryConverter extends Activity{

    private EditText binaryText;
    private EditText numberText;

    private String previousNumberText;
    private String previousBinaryText;




    @Override
    public void onCreate(Bundle savedInstanceState){

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_binary_converter);

        binaryText = (EditText) findViewById(getResources().getIdentifier("binaryText", "id", getPackageName()));
        numberText = (EditText) findViewById(getResources().getIdentifier("numberText", "id", getPackageName()));

        previousBinaryText="";
        previousNumberText="";

    }

    public void onClick(View v) {
        if (!previousBinaryText.equals(binaryText)) {
            if (binaryText.getText().toString().equals("")) {
                numberText.setText("");
                previousBinaryText = "";
                previousNumberText = "";
            } else {
                String result = Integer.parseInt(binaryText.getText().toString(), 2) + "";
                previousBinaryText = binaryText.getText().toString();
                previousNumberText = result;
                numberText.setText(result);
            }
        }

        if (!previousNumberText.equals(numberText)) {
            if (numberText.toString().equals("")) {
                binaryText.setText("");
                previousBinaryText = "";
                previousNumberText = "";
            } else {
                String result = Integer.toBinaryString(Integer.parseInt(numberText.getText().toString()));
                previousNumberText =numberText.getText().toString();
                previousBinaryText = result;
                binaryText.setText(result);
            }
        }

    }
}
