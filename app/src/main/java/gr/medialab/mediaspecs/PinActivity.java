/*
 * ******************************************************
 *  * Copyright (C) 2019-2020 Angelos Ladopoulos ladopoulos.angelos@gmail.com
 *  *
 *  * This file is part of MediaSpecs Android application.
 *  *
 *  *MediaSpecs application can not be copied and/or distributed without the express
 *  * permission of Angelos Ladopoulos
 *  ******************************************************
 */

package gr.medialab.mediaspecs;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PinActivity extends AppCompatActivity {

    SharedPreferences myPrefs;
    String className = "com.android.settings";
    private ActivityManager manager;
    //static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //active = true;
        this.manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        super.onCreate(savedInstanceState);
        final SensorService s = new SensorService();
        setContentView(R.layout.activity_pin);
        if (!isTablet()) {
            // stop screen rotation on phones because <explain>
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Button confirm = findViewById(R.id.button);
        final EditText mEdit = findViewById(R.id.editText);
        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //handle your on click event here
                myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                String verification = myPrefs.getString("nameKey", null);
                if (mEdit.getText().toString().equals("100700")) {
                    myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putString("PIN", "True");
                    editor.apply();
                    Toast toast1 = Toast.makeText(getApplicationContext(), "PIN correct", Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast1.show();
                    //Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                    //startActivity( launchIntent );
                    finish();
                } else{
                    Toast toast2 = Toast.makeText(getApplicationContext(), "PIN incorrect\nPlease try again", Toast.LENGTH_LONG);
                    toast2.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast2.show();
                }
            }
        });
    }

    private boolean isTablet() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        this.recreate();
        //finish();
        //startActivity(new Intent(PinActivity.this,PinActivity.class));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(!hasFocus) {
            //Log.e("FOCUS", "FOCUS");
            stopService(new Intent(this, SensorService.class));
            Intent serviceIntent = new Intent(this, SensorService2.class);
            startService(serviceIntent);
        }
    }

    @Override
    protected void onUserLeaveHint()
    {
        //Log.e("onUserLeaveHint","HOME BUTTON PRESSED");
        stopService(new Intent(this, SensorService.class));
        super.onUserLeaveHint();
    }
}
