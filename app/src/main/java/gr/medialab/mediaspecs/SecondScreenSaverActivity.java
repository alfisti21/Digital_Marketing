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
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class SecondScreenSaverActivity extends AppCompatActivity implements SensorEventListener {
	File to2 = new File(Environment.getExternalStorageDirectory() + "/" + ".hiddenFolder1" + "/" + "intro1.mp4");
    private SensorManager sensorMan;
    private Sensor accelerometer;

    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Random r = new Random();
    String upOrDown = r.nextBoolean() ? "UP" : "DOWN";
    Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/" + ".hiddenFolder1" + "/" + "intro1.mp4");
    SharedPreferences myPrefs;
    Intent mServiceIntent;
    SensorService mSensorService;
    Context ctx;
    public Context getCtx() {
        return ctx;
    }
    Context ctx3;
    public Context getCtx3() {
        return ctx3;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        if (sensorMan != null) {
            accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        final VideoView video;
        super.onCreate(savedInstanceState);

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String iteration = myPrefs.getString("SCREENSAVER", null);
        int iterationInt = Integer.parseInt(iteration);

        ctx3 = this;
        FloatingWidgetService mSensorService2 = new FloatingWidgetService();
        final Intent mServiceIntent2 = new Intent(getCtx3(), mSensorService2.getClass());

        if(iterationInt>20){
            //startService(mServiceIntent2);
            //moveTaskToBack(true);
            DevicePolicyManager mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            /*if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
                stopLockTask();
            }*/
            finishAndRemoveTask();
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("SCREENSAVER", "1");
            editor.apply();
        }else{
            iterationInt++;
            iteration = Integer.toString(iterationInt);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("SCREENSAVER", iteration);
            editor.apply();
        }

        ctx = this;
        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            //Log.e("SECOND TREXEI", "TREXEI");
            //Log.e("SECOND TREXEI", String.valueOf(isMyServiceRunning(mSensorService.getClass())));
            startService(mServiceIntent);
        }

        if (!isTablet()) {
            // stop screen rotation on phones because <explain>
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().addFlags(WindowManager.LayoutParams.TYPE_PHONE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if(nowIsBetweenTwoHours(21,15 , 8, 45)){
            finish();
            //Log.e("EINAI METAKSI?","EINAI");
            params.screenBrightness = 0.1f;
        }else{
            //Log.e("EINAI METAKSI?","DEN EINAI");
            params.screenBrightness = 1.0f;
        }
        getWindow().setAttributes(params);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);



        Log.e("RANDOM NUMBER IS:", upOrDown);

        if(upOrDown.matches("UP")){
        setContentView(R.layout.activity_second_screen_saver);
        }else{
            setContentView(R.layout.activity_second_screen_saver2);
        }



        ConstraintLayout mConstraintLayout = findViewById(R.id.guide2);

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
                        /*if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
                            stopLockTask();
                        }*/
                        finishAndRemoveTask();
                        //Log.d("DEBUG_TAG","Action was DOWN");
                        return true;
                    //Log.d("DEBUG_TAG","Movement occurred outside bounds " + "of current screen element");
                }
                return false;
            }
        });

        TextView txt0 = findViewById(R.id.txt0);
        TextView txt1 = findViewById(R.id.lianiki2);
        TextView txt2 = findViewById(R.id.fpa2);
        TextView txt3 = findViewById(R.id.times2);
        TextView txt4 = findViewById(R.id.doseis2);

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String token_vat = myPrefs.getString("VAT", null);

        ArrayList<String> nameList = specsFunction();

        NumberFormat nf = new DecimalFormat("#.00");
        double f = Float.valueOf(nameList.get(0));
        double d = Float.valueOf(nameList.get(1));
        double f1 = Float.valueOf(nameList.get(5));
        double d1 = Float.valueOf(nameList.get(6));
        double disc24 = Float.valueOf(nameList.get(2));
        double disc17 = Float.valueOf(nameList.get(7));
        int intd = (int) Math.round(d);
        int intd1 = (int) Math.round(d1);
        int intf = (int) Math.round(disc24);
        double a = f / d;
        double b = disc24 / d;
        double a1 = f1 / d1;
        double b1 = disc24 / d1;
        String f2 = nf.format(a);
        String f3 = nf.format(f);
        String f4 = nf.format(disc24);
        String f5 = nf.format(b);
        String f6 = nf.format(a1);
        String f7 = nf.format(f1);
        String f8 = nf.format(disc24);
        String f9 = nf.format(b1);
        String f10 = nf.format(disc17);

        txt0.setText(nameList.get(4)+"\n"+nameList.get(3));
        txt0.setTextColor(Color.parseColor("#FFFFFF"));
        txt1.setText("Τιμή Λιανικής:");
        txt1.setTextColor(Color.parseColor("#FFFFFF"));
        txt2.setText("Συμπεριλαμβάνεται ΦΠΑ");
        txt2.setTextColor(Color.parseColor("#FFFFFF"));

        if(disc24 != 0){
            if(token_vat.matches("VAT24")){
                txt3.setText("Αρχική: "+f4 + "€" +" / "+"Τελική: "+f3+ "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                }
                txt3.setTextColor(Color.parseColor("#FFFFFF"));
                txt4.setText("Άτοκες Δόσεις: " + intd + "x" + f2 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt4.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                }
                txt4.setTextColor(Color.parseColor("#FFFFFF"));
            }else{
                txt3.setText("Αρχική: "+f10 + "€" +" / "+"Τελική: "+f7+ "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                }
                txt3.setTextColor(Color.parseColor("#FFFFFF"));
                txt4.setText("Άτοκες Δόσεις: " + intd1 + "x" + f6 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt4.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                }
                txt4.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }else{
            if(token_vat.matches("VAT24")){
                txt3.setText(f3 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                }
                txt3.setTextColor(Color.parseColor("#FFFFFF"));
                txt4.setText("Άτοκες Δόσεις: " + intd + "x" + f2 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt4.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                }
                txt4.setTextColor(Color.parseColor("#FFFFFF"));
            }else{
                txt3.setText(f7 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                }
                txt3.setTextColor(Color.parseColor("#FFFFFF"));
                txt4.setText("Άτοκες Δόσεις: " + intd1 + "x" + f6 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt4.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                }
                txt4.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        video = findViewById(R.id.videoView10);
        if (to2.exists()) {
        //Uri uri=Uri.parse(Environment.getExternalStorageDirectory() + "/" + "intro1.mp4");

        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(video);
        video.setMediaController(null);
        video.setVideoURI(uri);
        video.requestFocus();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                //mp.setLooping(true);
                mp.setVolume(0,0);
                video.start();
            }
        });
        }else{
            Intent i = new Intent(getApplicationContext(), VideoDownload3.class);
            i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                //start1();
                //recreate();
               // mp.setVolume(0,0);
               //video.resume();
                Intent refresh = new Intent(getApplicationContext(), SecondScreenSaverActivity.class);
                startActivity(refresh);//Start the same Activity
                finish(); //finish Activity.
            }
        });

        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int i, int i1) {
                finish();
                return true;
            }
        });
    }
    private ArrayList<String> specsFunction(){

        JSONArray jsonArray;
        ArrayList<String> cList=new ArrayList<>();

        String TAG_PRICE24 = "priceVAT24";
        String TAG_DOSEIS24 = "dosesCountVAT24";
        String TAG_PRICE_DISC24 = "deletedPriceVAT24";
        String TAG_PRICE_DISC17 = "deletedPriceVAT17";
        String TAG_TRADENAME = "tradeName";
        String TAG_MANUFACTURER = "manufacturer";
        String TAG_PRICE17 = "priceVAT17";
        String TAG_DOSEIS17 = "dosesCountVAT17";

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String verification = myPrefs.getString("SAP", null);

        try {
            InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/"+".response.json");
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data, StandardCharsets.UTF_8);
            jsonArray=new JSONArray(json);
            JSONObject jsonobject = jsonArray.getJSONObject(0);
            cList.add(jsonobject.getString(TAG_PRICE24));
            cList.add(jsonobject.getString(TAG_DOSEIS24));
            cList.add(jsonobject.getString(TAG_PRICE_DISC24));
            cList.add(jsonobject.getString(TAG_TRADENAME));
            cList.add(jsonobject.getString(TAG_MANUFACTURER));
            cList.add(jsonobject.getString(TAG_PRICE17));
            cList.add(jsonobject.getString(TAG_DOSEIS17));
            cList.add(jsonobject.getString(TAG_PRICE_DISC17));

        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException je){
            je.printStackTrace();
        }
        return cList;
    }

    private void start1(){
        //Log.e("On Completion", "start is called");
        Intent dialogIntent = new Intent(SecondScreenSaverActivity.this, ScreenSaverActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
        finishAndRemoveTask();
    }

    private boolean isTablet() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
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

    @Override
    protected void onDestroy() {
        finishAndRemoveTask();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent heartbeat = new Intent(getApplicationContext(), HeartBeat.class);
        startService(heartbeat);

        DevicePolicyManager mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // First, confirm that this package is whitelisted to run in lock task mode.
        /*if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
            startLockTask();
        }*/

        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float)Math.sqrt(x*x + y*y + z*z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if(mAccel > 3){
                DevicePolicyManager mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                // First, confirm that this package is whitelisted to run in lock task mode.
                /*if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
                    stopLockTask();
                }*/
                finishAndRemoveTask();
                // do something
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public static boolean  nowIsBetweenTwoHours(
            int fromHour, int fromMinute,
            int toHour, int toMinute
    ) {
        boolean nowInNewDay = false;

        Calendar c = getLocalCalendar();

        Date now = c.getTime();

        c = getLocalCalendar();

        if (fromHour > c.get(Calendar.HOUR_OF_DAY)) {
            c.add(Calendar.DATE, -1);
            nowInNewDay = true;
        }

        c.set(Calendar.HOUR_OF_DAY, fromHour);
        c.set(Calendar.MINUTE, fromMinute);

        Date from = c.getTime();

        c = getLocalCalendar();

        if (!nowInNewDay && toHour < fromHour) {
            c.add(Calendar.DATE, 1);
        }

        c.set(Calendar.HOUR_OF_DAY, toHour);
        c.set(Calendar.MINUTE, toMinute);

        Date to = c.getTime();

        //System.out.println(from);
        //System.out.println(now);
        //System.out.println(to);

        return from.before(now) && now.before(to);
    }
    private static Calendar getLocalCalendar() {
        Calendar c = Calendar.getInstance();
        TimeZone fromTimeZone = c.getTimeZone();
        TimeZone toTimeZone = TimeZone.getTimeZone("Europe/Athens");

        c.setTime(new Date());

        c.setTimeZone(fromTimeZone);
        c.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
        if (fromTimeZone.inDaylightTime(c.getTime())) {
            c.add(Calendar.MILLISECOND, c.getTimeZone().getDSTSavings() * -1);
        }

        c.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
        if (toTimeZone.inDaylightTime(c.getTime())) {
            c.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
        }
        return c;
    }
}
