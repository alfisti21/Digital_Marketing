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

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.graphics.Color.BLACK;

public class ScreenProtector extends AppCompatActivity {
    private CountDownTimer mCountDownTimer;
    private static final long INTERVAL = 1000L;
    private long timeRemaining = 10*60000L; //10 minutes

    //Intent mServiceIntent;
    Context ctx;
    public Context getCtx() {
        return ctx;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.e("ARXI TO TIMER", "ARXI");

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        if(!nowIsBetweenTwoHours(21,15 , 8, 45)) {
            finishAndRemoveTask();
        }

        ctx = this;
        FloatingWidgetService mSensorService = new FloatingWidgetService();
        final Intent mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            //Log.e("Trexei to bubble??","Nai");
            stopService(mServiceIntent);
        } else {
            //Log.e("Trexei to bubble??","Oxi");
        }

        setContentView(R.layout.activity_screen_protection);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final LinearLayout background = findViewById(R.id.fonto);
        background.setBackgroundColor(BLACK);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 0.1f;
        getWindow().setAttributes(params);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        ConstraintLayout mConstraintLayout = findViewById(R.id.protector);

        mConstraintLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case (MotionEvent.ACTION_DOWN) :
                    case (MotionEvent.ACTION_MOVE) :
                        //Log.d("DEBUG_TAG","Action was MOVE");
                    case (MotionEvent.ACTION_UP) :
                        //Log.d("DEBUG_TAG","Action was UP");
                    case (MotionEvent.ACTION_CANCEL) :
                        //Log.d("DEBUG_TAG","Action was CANCEL");
                    case (MotionEvent.ACTION_OUTSIDE) :
                        DevicePolicyManager mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                        // First, confirm that this package is whitelisted to run in lock task mode.
                        if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
                            stopLockTask();
                        }
                        startService(mServiceIntent);
                        finishAndRemoveTask();
                        //Log.d("DEBUG_TAG","Action was DOWN");
                        return true;
                    //Log.d("DEBUG_TAG","Movement occurred outside bounds " + "of current screen element");
                }
                return false;
            }
        });

        mCountDownTimer = new CountDownTimer(timeRemaining, INTERVAL) {
            @Override
            public void onTick(long l) {
                timeRemaining = l;
            }

            @Override
            public void onFinish() {
                DevicePolicyManager mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                // First, confirm that this package is whitelisted to run in lock task mode.
                if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
                    stopLockTask();
                }
                //Log.e("TELOS TO TIMER", "TELOS");
                Intent refresh = new Intent(getApplicationContext(), ScreenProtector.class);
                startActivity(refresh);//Start the same Activity
                finish(); //finish Activity.
            }
        };
        mCountDownTimer.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        DevicePolicyManager mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // First, confirm that this package is whitelisted to run in lock task mode.
        if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
            startLockTask();
        }
    }

    @Override
    protected void onDestroy() {
        //Log.e("TELOS TO TIMER", "TELOS APO ONDESTROY");
        mCountDownTimer.cancel();
        finishAndRemoveTask();
        super.onDestroy();
    }

    boolean  nowIsBetweenTwoHours(int fromHour, int fromMinute, int toHour, int toMinute) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Europe/Athens"));
        c.setTime(new Date());

        Date now = c.getTime();

        c.set(Calendar.HOUR_OF_DAY, fromHour);
        c.set(Calendar.MINUTE, fromMinute);

        Date from = c.getTime();

        if (toHour < fromHour) {
            c.add(Calendar.DATE, 1);
        }

        c.set(Calendar.HOUR_OF_DAY, toHour);
        c.set(Calendar.MINUTE, toMinute);

        Date to = c.getTime();

        // System.out.println(a);
        // System.out.println(b);
        // System.out.println(d);

        return from.compareTo(now) * now.compareTo(to) >= 0;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                //Log.i ("isMyServiceRunning?", true+"");
                //Log.e("Trexei to bubble?? ti", serviceClass.getName());
                return false;
            }
        }
        //Log.i ("isMyServiceRunning?", false+"");
        return true;
    }

}
