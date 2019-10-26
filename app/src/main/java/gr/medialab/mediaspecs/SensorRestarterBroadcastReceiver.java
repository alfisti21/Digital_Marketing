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
//import android.util.Log;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.e("SENSOR ELAVA ON BOOT??", "NAI TO ELAVA");
            context.startForegroundService(new Intent(context, SensorService.class));
        } else {*/
        //Log.e("PIRA INTENT????", "EEEEEEEEE?");
        //Log.e("PIRA INTENT????", String.valueOf(intent));
        //Log.e("PIRA INTENT????", String.valueOf(context));
            context.startService(new Intent(context, SensorService.class));
        }
    }
//}
