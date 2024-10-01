package com.loc.passloc2android;

import android.app.Application;
import android.content.Context;

public class PassLoc2 extends Application   {


    private static Context appContext;



    public static Context getContext(){

        return appContext;
    }



    public void onCreate(){
        super.onCreate();
        System.loadLibrary("sqlcipher");
        appContext = getApplicationContext();


    }

}
