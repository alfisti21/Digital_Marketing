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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ScreenStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(!nowIsBetweenTwoHours(21,15 , 8, 45)) {
            Log.e("TI SKATA RE?", "POS GINETAI");
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                //code
            } else if (Intent.ACTION_SCREEN_OFF.equals(action) || Context.KEYGUARD_SERVICE.equals(action)) {
                Intent i = new Intent(context, ScreenSaverActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }else{
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                //code
            } else if (Intent.ACTION_SCREEN_OFF.equals(action) || Context.KEYGUARD_SERVICE.equals(action)) {
                Intent i = new Intent(context, ScreenProtector.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
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
}
