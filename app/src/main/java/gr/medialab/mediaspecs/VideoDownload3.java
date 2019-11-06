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

import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class VideoDownload3 extends AppCompatActivity {
	SharedPreferences myPrefs;
    private long downloadID;
    private Uri Download_Uri2 = Uri.parse("https://mediainteractivestorage.blob.core.windows.net/videos/livedevices_intro.mp4");
    File file2=new File(Environment.getExternalStorageDirectory() + "/" + ".hiddenFolder1" + "/" + "intro.mp4");


    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            //Checking if the received broadcast is for our enqueued download by matching download id

            File from2 = new File(Environment.getExternalStorageDirectory() + "/" + ".hiddenFolder1" + "/" + "intro.mp4");
            File to2 = new File(Environment.getExternalStorageDirectory() + "/" + ".hiddenFolder1" + "/" + "intro1.mp4");
            if(to2.exists()){
                to2.delete();
            }
            from2.renameTo(to2);
            Toast toast = Toast.makeText(VideoDownload3.this, "Download Intro.mp4\nCompleted", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
                /*Intent i = new Intent(getApplicationContext(), ScreenSaverActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);*/
            finish();

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File folder = new File(Environment.getExternalStorageDirectory(), ".hiddenFolder1");
        folder.mkdir();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_download3);
        //final Button button = findViewById(R.id.download);
        /*if(file1.exists()){
            file1.delete();
        }
        if(file2.exists()){
            file2.delete();
        }*/

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        beginDownload();
        /*button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                button.setVisibility(View.GONE);
                beginDownload();
            }
        });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void beginDownload(){

        File folder = new File(Environment.getExternalStorageDirectory(), ".hiddenFolder1");
        if(!folder.exists()){
            folder.mkdir();
        }
        /*
        Create a DownloadManager.Request with all the information necessary to start the download
         */

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri2)
                .setVisibleInDownloadsUi(false)
                .setTitle("intro.mp4")// Title of the Download Notification
                .setDescription("Downloading...")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file2))// Uri of the destination file
                .setRequiresCharging(false)// Set if charging is required to begin the download
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network
        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.

        final ProgressBar mProgressBar2 = findViewById(R.id.progressBar2);
        mProgressBar2.setScaleY(5f);

        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean downloading = true;

                while (downloading) {

                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadID);

                    Cursor cursor = downloadManager.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }
                    final int dl_progress2 = (int) ((bytes_downloaded * 100L) / bytes_total);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            mProgressBar2.setProgress(dl_progress2);

                        }
                    });

                    //Log.e("STATUS MESSAGES", statusMessage(cursor));
                    cursor.close();
                }

            }
        }).start();

    }


    private String statusMessage(Cursor c) {
        String msg = "???";

        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg = "Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg = "Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg = "Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg = "Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg = "Download complete!";
                break;

            default:
                msg = "Download is nowhere in sight";
                break;
        }

        return (msg);
    }

}

