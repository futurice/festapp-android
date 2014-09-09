package de.serviceexperiencecamp.android;

import android.app.Application;
import android.content.Context;

public class SecApp extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        SecApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return SecApp.context;
    }
}