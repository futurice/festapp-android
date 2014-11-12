package com.futurice.festapp.android;

import android.app.Application;
import android.content.Context;

public class FestApp extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        FestApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return FestApp.context;
    }
}