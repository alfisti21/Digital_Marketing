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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

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

        ImageView info = findViewById(R.id.infoPin);
        Button confirm = findViewById(R.id.button);
        Button update = findViewById(R.id.updateButton);
        final EditText mEdit = findViewById(R.id.editText);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(PinActivity.this);
                //builder1.setTitle("MediaSpecs");
                builder1.setMessage(Html.fromHtml("This application was built by Medialab Ltd. We are always on the look for talented people. If you feel like one, don't hesitate to contact us at: aladopoulos@medialab.gr. You can also find us here: <a href=\"http://www.medialab.gr\">www.medialab.gr</a>"));
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "CU",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
                ((TextView)alert11.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateCheck = new Intent(getApplicationContext(), ApkVersionCheck.class);
                startService(updateCheck);
            }
        });

        LayoutInflater factory = LayoutInflater.from(PinActivity.this);
        final View view1 = factory.inflate(R.layout.troll_layout, null);
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bazinga);

        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //handle your on click event here
                myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);

                String verification = myPrefs.getString("nameKey", null);
                if (mEdit.getText().toString().equals("0682956477")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PinActivity.this);
                    //builder1.setTitle("MediaSpecs");

                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "CU",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mEdit.setText(null);
                                    ((ViewManager)view1.getParent()).removeView(view1);
                                    dialog.dismiss();
                                }
                            });
                    builder1.setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mEdit.setText(null);
                                    ((ViewManager)view1.getParent()).removeView(view1);
                                    dialog.dismiss();
                                }
                            }
                    );
                    builder1.setView(view1);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    mp.setVolume(100, 100);
                    mp.start();
                    ((TextView)alert11.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                } else if (mEdit.getText().toString().equals("100700")) {
                    myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putString("PIN", "True");
                    editor.apply();
                    Toast toast1 = Toast.makeText(getApplicationContext(), "PIN correct", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast1.show();
                    //Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                    //startActivity( launchIntent );
                    finish();
                } else {
                    mEdit.setText(null);
                    Toast toast2 = Toast.makeText(getApplicationContext(), "PIN incorrect\nPlease try again", Toast.LENGTH_SHORT);
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
