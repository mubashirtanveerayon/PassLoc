package com.example.constants;

import android.app.Application;
import android.content.Context;

public class PassLoc extends Application {

    private static Context appContext;

    public void onCreate(){
        super.onCreate();
        System.loadLibrary("sqlcipher");

        appContext = this.getApplicationContext();
    }

    public static Context getContext(){
        return appContext;
    }

}
