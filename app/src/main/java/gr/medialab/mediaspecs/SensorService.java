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
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;


public class SensorService extends Service {

    String CURRENT_PACKAGE_NAME = "MediaSpecs";
    String lastAppPN = "";
    boolean noDelay = false;
    public static SensorService instance;
    private Timer timer1 = new Timer();
    SharedPreferences myPrefs;

    public int counter=0;
    public SensorService(Context applicationContext) {
        super();
        //Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //startForeground(1,new Notification());
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("SENSOR SERVICE", "1");
        editor.apply();


        //delete

        final ScheduledExecutorService scheduler = Executors
                .newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // This method will check for the Running apps every 100ms
                String a= retriveNewApp();

                if(a.equals("com.android.settings")){
                    scheduler.shutdown();
                    }
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
        //delete

        //scheduleMethod();
        CURRENT_PACKAGE_NAME = getApplicationContext().getPackageName();
        //Log.e("Current PN", "" + CURRENT_PACKAGE_NAME);

        instance = this;
        startTimer();
        return START_STICKY;
    }


    private void scheduleMethod() {
        // TODO Auto-generated method stub

        final ScheduledExecutorService scheduler = Executors
                .newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // This method will check for the Running apps every 100ms
                String a= retriveNewApp();

                if(a.equals("com.android.settings")){
                    scheduler.shutdown();

                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private /*String*/ String retriveNewApp() {
        String currentApp = null;


        if (Build.VERSION.SDK_INT >= 21) {

            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            assert usm != null;
            long time = System.currentTimeMillis();
            List<UsageStats> applist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (applist != null && applist.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : applist) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();
                }
            }
            //Log.e(TAG, "Current App in foreground is: " + currentApp);


            assert currentApp != null;
            if(currentApp.equals("com.android.settings")){
                Intent dialogIntent = new Intent(this, PinActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
                //Log.e(TAG, "Current App in foreground is Correct");
                onDestroy();
            }

        }
        else {

            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String mm=(manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
            //Log.e(TAG, "Current App in foreground iss: " + mm);
            //return mm;
        }
        return currentApp;
    }

    public static void stop() {
        if (instance != null) {
            instance.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        //Log.i("EXIT", "ondestroy!");
        /*Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();*/
        super.onDestroy();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
