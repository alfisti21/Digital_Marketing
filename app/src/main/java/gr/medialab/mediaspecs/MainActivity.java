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

import gr.medialab.mediaspecs.BuildConfig;
import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
//import android.util.Log;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String versionName = BuildConfig.VERSION_NAME;
    private static final int ACCESSIBILITY_ENABLED = 1;
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
    SharedPreferences myPrefs;
    Intent mServiceIntent;
    Intent mServiceIntent2;
    Intent mServiceIntent3;
    Intent mServiceIntent4;
    SensorService mSensorService;
    ScreenSaver mScreenSaver;
    HeartBeat mHeartBeat;
    LiveDataService mLiveDataService;
    Context ctx;
    Context ctx2;
    Context ctx3;
    Context ctx4;
    Context ctx5;

    public Context getCtx() {
        return ctx;
    }
    public Context getCtx2() {
        return ctx2;
    }
    public Context getCtx4() {
        return ctx4;
    }
    public Context getCtx5() {
        return ctx5;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MyScheduledReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUp)
        {
            //Log.e("EXO ALARM?","DEN EXO");
            scheduleRestart();
        }else{
            //Log.e("EXO ALARM?","EXO");
        }
        ctx = this;
        ctx2 = this;
        ctx3 = this;
        ctx4 = this;
        ctx5 = this;

        setContentView(R.layout.activity_main);
        if (!isTablet()) {
            // stop screen rotation on phones because <explain>
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
            }
        }

        if (!isAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        if (!isAccessServiceEnabled(this, MyAccessibilityService.class)) {
            Log.e("GIATI?????", "GIATI?");
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }


        String mac = getMacAddr();

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("IMEI", mac);
        editor.apply();
        //Log.e("IMEI", mac);


        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName deviceAdminComponentName = new ComponentName(this, DeviceAdmin.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You must enable device administration for certain features"
                + " of the app to function.");

        //I thought that this would start the activity that lets the user
        //choose whether to enable the app as a device admin
        startActivityForResult(intent, 47);


        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (isMyServiceRunning(mSensorService.getClass())) {
            //Log.e("TREXEI", "TREXEI");
            startService(mServiceIntent);
        }

        mScreenSaver = new ScreenSaver(getCtx2());
        mServiceIntent2 = new Intent(getCtx2(), mScreenSaver.getClass());
        if (isMyServiceRunning(mScreenSaver.getClass())) {
            startService(mServiceIntent2);
        }

        Button confirm = findViewById(R.id.button);
        TextView txtVersion = findViewById(R.id.textViewVersion);
        txtVersion.setText(versionName);
        final EditText sapCode = findViewById(R.id.editText3);
        final EditText email = findViewById(R.id.editText4);
        final EditText password = findViewById(R.id.editText5);

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String a1 = myPrefs.getString("LIVE DATA SERVICE", null);
        String a2 = myPrefs.getString("HEARTBEAT SERVICE", null);

        assert a1 != null;
        assert a2 != null;
        try {
            if (a1.matches("1") && a2.matches("1")) {

                mHeartBeat = new HeartBeat(getCtx4());
                mServiceIntent3 = new Intent(getCtx4(), mHeartBeat.getClass());
                if (isMyServiceRunning(mHeartBeat.getClass())) {
                    startService(mServiceIntent3);
                }

                mLiveDataService = new LiveDataService(getCtx5());
                mServiceIntent4 = new Intent(getCtx5(), mLiveDataService.getClass());
                if (isMyServiceRunning(mLiveDataService.getClass())) {
                    startService(mServiceIntent4);
                }
                Intent service = new Intent(getApplicationContext(), VideoCheck.class);
                startService(service);
                Intent service2 = new Intent(getApplicationContext(), VideoCheck2.class);
                startService(service2);
                finishAndRemoveTask();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle your on click event here
                //scheduleRestart();
                String sap = sapCode.getText().toString();
                String mailAdd = email.getText().toString();
                String passWord = password.getText().toString();
                if (mailAdd.matches("") || passWord.matches("") || sap.matches("")) {
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Δοκιμάστε ξανά", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast1.show();
                } else {
                    myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putString("SAP", sap);
                    editor.putString("EMAIL", mailAdd);
                    editor.putString("PASSWORD", passWord);
                    editor.putString("VERSION", "");
                    editor.putString("VERSION2", "");
                    editor.putString("VERSION3", "");
                    editor.putString("AUTOCLICK", "");
                    editor.putString("SCREENSAVER", "1");
                    editor.apply();
                    Toast toast2 = Toast.makeText(getApplicationContext(), "Παρακαλώ περιμένετε...", Toast.LENGTH_SHORT);
                    toast2.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast2.show();
                    //createFloatingWidget(null);
                    if(isInternetAvailable()){
                        //Log.e("EXO INTERNET?","EXO");
                        Intent i = new Intent(ctx3, LoginService.class);
                        ctx3.startService(i);
                        finish();
                    }else{
                        //Log.e("EXO INTERNET?","DEN EXO");
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Δοκιμάστε ξανά αργότερα\nΔεν υπάρχει σύνδεση\nστο διαδίκτυο", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast1.show();
                        finish();
                    }
                }
            }
        });
    }


    /*  start floating widget service  */
    public void createFloatingWidget(View view) {
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        } else
            //If permission is granted start floating widget service
        //Log.e("POTE FTASAME EDW", "POTE FTASAME EDW???");
            startFloatingWidgetService();

    }
    private void startFloatingWidgetService() {
        startService(new Intent(MainActivity.this, FloatingWidgetService.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK)
                //If permission granted start floating widget service
                startFloatingWidgetService();
            else
                //Permission is not available then display toast
                Toast.makeText(this,
                        getResources().getString(R.string.draw_other_app_permission_denied),
                        Toast.LENGTH_SHORT).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return false;
            }
        }
        return true;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private boolean isTablet() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public boolean isAccessServiceEnabled(Context context, Class accessibilityServiceClass) {
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("ACCESSIBILITY SERVICE", "1");
        editor.apply();
        String prefString = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

        return prefString!= null && prefString.contains(context.getPackageName() + "/" + accessibilityServiceClass.getName());
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        //Log.e("ACCESSIBILITY", "MPIKA");
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            //Log.e("AU", "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == ACCESSIBILITY_ENABLED) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);

            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();
    }

    public void scheduleRestart(){
        Log.e("EVALA OMOS", "EVALA OMOS");
        Intent myIntent = new Intent(getApplicationContext(), MyScheduledReceiver.class);

        PendingIntent pendingIntent
                = PendingIntent.getBroadcast(getApplicationContext(),
                0, myIntent, 0);

        AlarmManager alarmManager
                = (AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        long interval = 4*60*60*1000; //4 hours
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
    }


    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        //Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}

