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
import android.content.ComponentName;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.view.Gravity.CENTER;
import static java.lang.Math.round;

public class ScreenSaverActivity extends AppCompatActivity implements SensorEventListener {
    File to3 = new File(Environment.getExternalStorageDirectory() + "/" + ".hiddenFolder3" + "/" + "MediaSpecs.apk");
    File to2 = new File(Environment.getExternalStorageDirectory() + "/" + ".hiddenFolder" + "/" + "main1.mp4");
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    SharedPreferences myPrefs;
    Context ctx;
    Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/" + ".hiddenFolder" + "/" + "main1.mp4");
    public Context getCtx() {
        return ctx;
    }

    Intent mServiceIntent;
    SensorService mSensorService;
    Context ctx2;
    public Context getCtx2() {
        return ctx2;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    //@RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ComponentName deviceAdmin = new ComponentName(this, DeviceAdmin.class);


        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        if(nowIsBetweenTwoHours(21,15 , 8, 45)) {
            Intent i = new Intent(getApplicationContext(), ScreenProtector.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
            finish();
        }
        sensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        if (sensorMan != null) {
            accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        final VideoView video;
        //Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/" + "main1.mp4");

        super.onCreate(savedInstanceState);


        ctx2 = this;
        mSensorService = new SensorService(getCtx2());
        mServiceIntent = new Intent(getCtx2(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            //Log.e("SECOND TREXEI", "TREXEI");
            //Log.e("SECOND TREXEI", String.valueOf(isMyServiceRunning(mSensorService.getClass())));
            startService(mServiceIntent);
        }

        if (!isTablet()) {
            // stop screen rotation on phones because <explain>
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        ctx = this;
        ctx2 = this;

        FloatingWidgetService mSensorService = new FloatingWidgetService();
        final Intent mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            //Log.e("Trexei to bubble??","Nai");
            stopService(mServiceIntent);
        } else {
            //Log.e("Trexei to bubble??","Oxi");
        }


        //String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        //Log.e("CURRENT TIME", currentTime.toString());


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().addFlags(WindowManager.LayoutParams.TYPE_PHONE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if(nowIsBetweenTwoHours(21,15 , 8, 45)){
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
        if (!isTablet()) {
            setContentView(R.layout.activity_screen_saver);
        } else{
            int orientation = this.getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                //Log.e("EIMAI TABLET", "PORTRAIT");
                setContentView(R.layout.activity_screen_saver_tablet_portrait);
            }else{
                //Log.e("EIMAI TABLET", "LANDSCAPE");
                setContentView(R.layout.activity_screen_saver_tablet_landscape);
            }
        }


        ConstraintLayout mConstraintLayout = findViewById(R.id.guide1);

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



        TextView txt0 = findViewById(R.id.txt0);
        TextView txt1 = findViewById(R.id.txt1);
        TextView txt2 = findViewById(R.id.txt2);
        TextView txt3 = findViewById(R.id.txt3);
        TextView txt4 = findViewById(R.id.txt4);
        TextView txt5 = findViewById(R.id.txt5);
        TextView txt6 = findViewById(R.id.txt6);
        TextView txt7 = findViewById(R.id.lianiki);
        TextView txt8 = findViewById(R.id.txt20);
        TextView txt9 = findViewById(R.id.txt22);
        TextView txt10 = findViewById(R.id.txt23);
        TextView txt11 = findViewById(R.id.txt24);
        TextView txt12 = findViewById(R.id.txt25);
        TextView txt13 = findViewById(R.id.txt21);
        TextView txt14 = findViewById(R.id.fpa);
        TextView txt15 = findViewById(R.id.times);
        TextView txt16 = findViewById(R.id.doseis);

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String token_vat = myPrefs.getString("VAT", null);

        ArrayList<String> nameList = specsFunction();

        NumberFormat nf = new DecimalFormat("#.00");

        double f = Float.valueOf(nameList.get(7));
        double d = Float.valueOf(nameList.get(9));
        double f1 = Float.valueOf(nameList.get(13));
        double d1 = Float.valueOf(nameList.get(14));
        double disc24 = Float.valueOf(nameList.get(10));
        double disc17 = Float.valueOf(nameList.get(15));
        int intd = (int) round(d);
        int intd1 = (int) round(d1);
        int intf = (int) round(disc24);
        double a = f / d;
        double a1 = f1 / d1;
        double b = disc24 / d;
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


        txt0.setText(nameList.get(12)+"\n"+nameList.get(8));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txt0.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }else {
            txt0.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        }
        txt8.setText("Επεξεργαστής"+"\n");
        txt8.setTextColor(Color.parseColor("#000000"));
        txt9.setText("Κάμερα Μπροστά"+"\n");
        txt9.setTextColor(Color.parseColor("#000000"));
        txt10.setText("Μνήμη RAM/ROM"+"\n");
        txt10.setTextColor(Color.parseColor("#000000"));
        txt11.setText("Μπαταρία"+"\n");
        txt11.setTextColor(Color.parseColor("#000000"));
        txt12.setText("Μέγεθος Οθόνης"+"\n");
        txt12.setTextColor(Color.parseColor("#000000"));
        txt13.setText("Τύπος Δικτύου"+"\n");
        txt13.setTextColor(Color.parseColor("#000000"));
        txt0.setBackgroundResource(R.color.red);
        txt0.setTextColor(Color.parseColor("#FFFFFF"));
        txt1.setText("\n" + nameList.get(0));
        txt1.setTextColor(Color.parseColor("#000000"));
        txt2.setText("\n" + nameList.get(1));
        txt2.setTextColor(Color.parseColor("#000000"));
        txt3.setText("\n" + nameList.get(2) + "/" + nameList.get(6));
        txt3.setTextColor(Color.parseColor("#000000"));
        txt4.setText("\n" + nameList.get(3) + " mAh");
        txt4.setTextColor(Color.parseColor("#000000"));
        txt5.setText("\n" + nameList.get(4) + "''");
        txt5.setTextColor(Color.parseColor("#000000"));
        txt6.setText("\n" + nameList.get(5));
        txt6.setTextColor(Color.parseColor("#000000"));
        txt7.setText("Τιμή Λιανικής:");
        txt7.setTextColor(Color.parseColor("#FFFFFF"));
        txt14.setText("Συμπεριλαμβάνεται ΦΠΑ");
        txt14.setTextColor(Color.parseColor("#FFFFFF"));

        if(disc24 != 0){
            if(token_vat.matches("VAT24")){
                txt15.setText("Αρχική: "+f8 + "€" +" / "+"Τελική: "+f3+ "€");

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt15.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                }
                txt15.setTextColor(Color.parseColor("#FFFFFF"));
                txt16.setText("Άτοκες Δόσεις: " + intd + "x" + f2 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt16.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                }
                txt16.setTextColor(Color.parseColor("#FFFFFF"));

            }else{
                txt15.setText("Αρχική: "+f10 + "€" +" / "+"Τελική: "+f7+ "€");

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt15.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                }
                txt15.setTextColor(Color.parseColor("#FFFFFF"));
                txt16.setText("Άτοκες Δόσεις: " + intd1 + "x" + f6 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt16.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                }
                txt16.setTextColor(Color.parseColor("#FFFFFF"));
            }

        }else{
            if(token_vat.matches("VAT24")){
                //Log.e("TO VAT EINAI 24","24");
                txt15.setText(f3 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt15.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
                }
                txt15.setTextColor(Color.parseColor("#FFFFFF"));
                txt16.setText("Άτοκες Δόσεις: " + intd + "x" + f2 + "€");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    txt16.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                }
                txt16.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                //Log.e("TO VAT EINAI 17","24");

            txt15.setText(f7 + "€");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                txt15.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
            }
            txt15.setTextColor(Color.parseColor("#FFFFFF"));
            txt16.setText("Άτοκες Δόσεις: " + intd1 + "x" + f6 + "€");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                txt16.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            }
            txt16.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }


        String dokimi = nameList.get(11);
        String [] items = dokimi.split("\\s*,\\s*");
        List<String> container = Arrays.asList(items);
        HashSet<String> set = new HashSet<>(container);
        List<String> container2 = new ArrayList<>(set);
        int numberOfColors = set.size();

        LinearLayout colorsLayout = findViewById(R.id.availableColorsLayout);
        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        colorParams.setMarginStart(15);
        colorParams.gravity = CENTER;

        for(int i = 0; i<numberOfColors; i++) {
            ImageView imageColor = new ImageView(this);
            imageColor.setImageResource(R.drawable.circle_colors);
            imageColor.setLayoutParams(colorParams);
            imageColor.setColorFilter(Color.parseColor(container2.get(i)));
            colorsLayout.addView(imageColor);
        }



    }

    private ArrayList<String> specsFunction() {

        JSONArray jsonArray;
        ArrayList<String> cList = new ArrayList<>();
        String TAG_TRADENAME = "tradeName";
        String TAG_CPU = "processorAbbreviation";
        String TAG_CAMERA = "mainCamera";
        String TAG_NETWORK = "networkType";
        String TAG_SCREEN = "screenSizeInches";
        String TAG_BATTERY = "baterySizeSizeMilliAh";
        String TAG_RAM = "ramSizeNormalized";
        String TAG_ROM = "romSizeNormalized";
        String TAG_PRICE24 = "priceVAT24";
        String TAG_DOSEIS24 = "dosesCountVAT24";
        String TAG_PRICE_DISC24 = "deletedPriceVAT24";
        String TAG_PRICE_DISC17 = "deletedPriceVAT17";
        String TAG_COLORS_ALL = "allColorsHEX";
        String TAG_MANUFACTURER = "manufacturer";
        String TAG_PRICE17 = "priceVAT17";
        String TAG_DOSEIS17 = "dosesCountVAT17";

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String verification = myPrefs.getString("SAP", null);
        assert verification != null;
        //int id = Integer.parseInt(verification);

        try {
            InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/"+".response.json");
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data, StandardCharsets.UTF_8);
            jsonArray = new JSONArray(json);
            JSONObject jsonobject = jsonArray.getJSONObject(0);
            cList.add(jsonobject.getString(TAG_CPU));
            cList.add(jsonobject.getString(TAG_CAMERA));
            cList.add(jsonobject.getString(TAG_RAM));
            cList.add(jsonobject.getString(TAG_BATTERY));
            cList.add(jsonobject.getString(TAG_SCREEN));
            cList.add(jsonobject.getString(TAG_NETWORK));
            cList.add(jsonobject.getString(TAG_ROM));
            cList.add(jsonobject.getString(TAG_PRICE24));
            cList.add(jsonobject.getString(TAG_TRADENAME));
            cList.add(jsonobject.getString(TAG_DOSEIS24));
            cList.add(jsonobject.getString(TAG_PRICE_DISC24));
            cList.add(jsonobject.getString(TAG_COLORS_ALL));
            cList.add(jsonobject.getString(TAG_MANUFACTURER));
            cList.add(jsonobject.getString(TAG_PRICE17));
            cList.add(jsonobject.getString(TAG_DOSEIS17));
            cList.add(jsonobject.getString(TAG_PRICE_DISC17));

        } catch (JSONException | IOException je) {
            je.printStackTrace();
        }
        return cList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void start2()  {
        //Log.e("On Completion", "start is called");
        Intent secondscreen = new Intent(getApplicationContext(), SecondScreenSaverActivity.class);
        startActivity(secondscreen);
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
                //Log.i ("isMyServiceRunning?", true+"");
                //Log.e("Trexei to bubble?? ti", serviceClass.getName());
                return false;
            }
        }
        //Log.i ("isMyServiceRunning?", false+"");
        return true;
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        final VideoView video;
        /*String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        if(checkTime("21:15:00", "08:45:00", currentTime)) {
            Intent i = new Intent(getApplicationContext(), ScreenProtector.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
        }*/
        super.onResume();
        if(nowIsBetweenTwoHours(21,15 , 8, 45)) {
            Intent i2 = new Intent(getApplicationContext(), ScreenProtector.class);
            i2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i2.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i2);
            finish();
        }
        Intent heartbeat = new Intent(getApplicationContext(), HeartBeat.class);
        startService(heartbeat);

        DevicePolicyManager mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // First, confirm that this package is whitelisted to run in lock task mode.
        if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
            startLockTask();
        }

        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
        video = findViewById(R.id.videoView);
        if (to2.exists()) {

            //Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/" + "main1.mp4");

            //Creating and starting the Media Controller for the video
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(video);
            video.setMediaController(null);
            video.setVideoURI(uri);
            video.requestFocus();
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //mp.setLooping(true);

                    mp.setVolume(0, 0);
                    video.start();
                    Intent service3 = new Intent(getApplicationContext(), ApkVersionCheck.class);
                    startService(service3);
                    //Log.e("On Completion", "Video Playing");
                }
            });
        }else{
            Intent i = new Intent(getApplicationContext(), VideoDownload.class);
            i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Log.e("On Completion", "On Completion is accessed");
                DevicePolicyManager mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                // First, confirm that this package is whitelisted to run in lock task mode.
                /*if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
                    stopLockTask();
                }*/

                start2();
                //onDestroy();
                finish();
                //mp.setVolume(0,0);
                //video.resume();
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
                if (mDpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
                    stopLockTask();
                }
                FloatingWidgetService mSensorService = new FloatingWidgetService();
                final Intent mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
                startService(mServiceIntent);
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
