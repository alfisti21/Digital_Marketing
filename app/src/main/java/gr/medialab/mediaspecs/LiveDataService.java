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
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
//import android.util.Log;
import android.os.PowerManager;
import android.view.Gravity;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LiveDataService extends Service {
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
    private Timer timer = new Timer();
    SharedPreferences myPrefs;
    PowerManager.WakeLock wakeLock;



    public LiveDataService() {
    }

    public LiveDataService(Context applicationContext) {
        super();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @SuppressLint("WakelockTimeout")
    @Override
    public void onCreate() {
        super.onCreate();

        /*PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"mediaspecs:tag");
        wakeLock.acquire();*/

        stopService(new Intent(LiveDataService.this, LoginService.class));
        Intent service = new Intent(getApplicationContext(), HeartBeat.class);
        startService(service);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //startForeground(1,new Notification());

        final RequestQueue queue = Volley.newRequestQueue(this);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {



                myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                String verification = myPrefs.getString("SAP", null);

                String url = "https://vdf.livedevices.gr/api/devices?$filter=sapCode eq "+verification;
                //Log.e("SAP URL", url);

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                        (Request.Method.GET, url, (String)null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if(response.length()==0){
                                    Toast toast = Toast.makeText(getApplicationContext(), "Λάθος SAP\nΔοκιμάστε ξανά", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                    Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(mainActivity);
                                } else {
                                    try {
                                        JSONObject jsonobject = response.getJSONObject(0);
                                        String version = jsonobject.getString("version");
                                        //Log.e("VERSION", version);
                                        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                                        String sharedVersion = myPrefs.getString("VERSION", null);
                                        if(!sharedVersion.equals(version)){
                                            //Log.e("onResponse", response.toString());
                                            //Log.e("Mnimi", Environment.getExternalStorageDirectory().toString());
                                            mCreateAndSaveFile(".response.json", response.toString());
                                            SharedPreferences.Editor editor = myPrefs.edit();
                                            editor.putString("VERSION", version);
                                            editor.apply();
                                        }else{
                                            Intent service = new Intent(getApplicationContext(), VideoCheck.class);
                                            startService(service);
                                            Intent service2 = new Intent(getApplicationContext(), VideoCheck2.class);
                                            startService(service2);
                                            Intent service3 = new Intent(getApplicationContext(), ApkVersionCheck.class);
                                            startService(service3);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Log.e("SFALMA JSON", error.toString());
                                Toast toast = Toast.makeText(getApplicationContext(), "Λάθος SAP2\nΔοκιμάστε ξανά", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainActivity);
                                /*Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainActivity);*/
                                // TODO: Handle error
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                        String accesstoken = myPrefs.getString("TOKEN", null);
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + accesstoken);
                        return headers;
                    }
                };
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonObjectRequest);
            }
        }, 0, 8*60*60*1000);//8 hours
        return START_STICKY;
    }

    public void mCreateAndSaveFile(String params, String mJsonResponse) {
        try {
            FileWriter file = new FileWriter(Environment.getExternalStorageDirectory() + "/" + params);
            file.write(mJsonResponse);
            file.flush();
            file.close();
            Toast toast = Toast.makeText(getApplicationContext(), "Τα δεδομένα\nαποθηκεύτηκαν επιτυχώς", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.putString("LIVE DATA SERVICE", "1");
            editor.apply();
            //Thread.sleep(2000);
            Intent service = new Intent(getApplicationContext(), VideoCheck.class);
            startService(service);
            Intent service2 = new Intent(getApplicationContext(), VideoCheck2.class);
            startService(service2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
