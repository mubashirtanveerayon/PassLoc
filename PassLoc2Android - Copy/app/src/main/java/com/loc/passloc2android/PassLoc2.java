package com.loc.passloc2android;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class PassLoc2 extends Application   {


    private static Context appContext;


    public static final String CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "Notification";


    public static Context getContext(){

        return appContext;
    }



    public void onCreate(){
        super.onCreate();
        System.loadLibrary("sqlcipher");
        appContext = getApplicationContext();


        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);






    }

}
