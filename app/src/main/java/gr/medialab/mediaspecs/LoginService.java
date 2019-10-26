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
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
//import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginService extends Service {
    String versionName = BuildConfig.VERSION_NAME;
    public LoginService() {
    }

    SharedPreferences myPrefs;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final RequestQueue queue = Volley.newRequestQueue(this);

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String email = myPrefs.getString("EMAIL", null);
        String password = myPrefs.getString("PASSWORD", null);
        String imei = myPrefs.getString("IMEI", null);
        String sap = myPrefs.getString("SAP", null);
        String url = "https://vdf.livedevices.gr/api/token";
        //Log.e("TOKEN URL", url);


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userName", email);
            jsonBody.put("password", password);
            jsonBody.put("deviceUniqueIndentifier", imei);
            jsonBody.put("sapCode", sap);
            jsonBody.put("deviceApplicationVersion", versionName);
            //Log.e("JSONBODY",jsonBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //Log.e("onResponse", response.toString());
                    try {
                        String token = response.getString("token");
                        String vat = response.getString("vatRegime");
                        //Log.e("TOKEN", token);
                        //Log.e("VATREGIME", vat);
                        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putString("TOKEN", token);
                        editor.putString("VAT", vat);
                        editor.apply();
                        Intent service = new Intent(getApplicationContext(), LiveDataService.class);
                        startService(service);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e("SFALMA LOGIN", error.toString());
                    Toast toast = Toast.makeText(getApplicationContext(), "Λάθος Email\nή/και Password", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                    Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainActivity);
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonObjectRequest);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
