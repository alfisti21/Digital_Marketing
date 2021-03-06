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
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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

public class VideoCheck extends Service {
    SharedPreferences myPrefs;

    public VideoCheck(Context applicationContext) {
        super();
    }

    public VideoCheck() {
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

        final RequestQueue queue = Volley.newRequestQueue(this);

                String url = "https://mediainteractivestorage.blob.core.windows.net/videos/livedevices_main.mp4";
                //Log.e("VIDEOCHECK URL", url);

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                        (Request.Method.HEAD, url, (String)null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                //Log.e("VIDEOCHECK RESPONSE", response.toString());
                                stopSelf();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Σφάλμα δικτύου", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                    @Override
                    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                        if (response.data == null || response.data.length == 0) {
                            //response.headers.get("ETag");
                            String version = response.headers.get("ETag");
                            //Log.e("ETAG", response.headers.get("ETag"));
                            myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                            String sharedVersion = myPrefs.getString("VERSION2", null);
                            if(!sharedVersion.equals(version)) {
                                SharedPreferences.Editor editor = myPrefs.edit();
                                editor.putString("VERSION2", version);
                                editor.apply();
                                Intent i = new Intent(getApplicationContext(), VideoDownload.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                stopSelf();
                            return Response.success(new JSONArray(), HttpHeaderParser.parseCacheHeaders(response));
                            }
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

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Log.e("VIDEOCHECK", "onDestroy");
        //this.stopSelf();
        super.onDestroy();
    }

}