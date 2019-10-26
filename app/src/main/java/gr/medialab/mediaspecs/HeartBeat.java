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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HeartBeat extends Service {
    private Timer timer = new Timer();
    SharedPreferences myPrefs;

    public HeartBeat(Context applicationContext) {
        super();
    }

    public HeartBeat() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //startForeground(1,new Notification());
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("HEARTBEAT SERVICE", "1");
        editor.apply();
        final RequestQueue queue = Volley.newRequestQueue(this);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                String url = "https://vdf.livedevices.gr/api/livedeviceheartbeat";
                //Log.e("HEARTBEAT URL", url);

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                        (Request.Method.HEAD, url, (String)null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                //Log.e("HEARTBEAT RESPONSE", response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Log.e("SFALMA HEARTBEAT", error.toString());
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                        String accesstoken = myPrefs.getString("TOKEN", null);
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + accesstoken);
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                    @Override
                    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                        if (response.data == null || response.data.length == 0) {
                            return Response.success(new JSONArray(), HttpHeaderParser.parseCacheHeaders(response));
                        } else {
                            return super.parseNetworkResponse(response);
                        }
                    }
                };
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonObjectRequest);
            }
        }, 0, 3*60*1000);//3 minutes
        return START_STICKY;
    }

}