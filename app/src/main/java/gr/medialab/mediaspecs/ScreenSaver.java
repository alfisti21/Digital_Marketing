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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
//import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class ScreenSaver extends Service {
    SharedPreferences myPrefs;
    public static ScreenSaver instance;
    public ScreenStateReceiver mReceiver = new ScreenStateReceiver();
    private static List<BroadcastReceiver> receivers = new ArrayList<>();

    public ScreenSaver(Context applicationContext) {
        super();
    }
    public ScreenSaver() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //startForeground(1,new Notification());
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("SCREEN SERVICE", "1");
        editor.apply();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        //ScreenStateReceiver mReceiver = new ScreenStateReceiver();
        if(!isReceiverRegistered(mReceiver)) {
            registerReceiver(mReceiver, intentFilter);
            receivers.add(mReceiver);
        }

        instance = this;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try{
        unregisterReceiver(mReceiver);
        }catch (Exception e) {
            e.printStackTrace();
        }
        stopSelf();
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, ScreenSaverBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }

    public boolean isReceiverRegistered(BroadcastReceiver receiver){
        boolean registered = receivers.contains(receiver);
        //Log.i(getClass().getSimpleName(), "is receiver "+receiver+" registered? "+registered);
        return registered;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
