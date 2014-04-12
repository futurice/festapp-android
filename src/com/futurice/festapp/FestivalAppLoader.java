package com.futurice.festapp;

import com.futurice.festapp.util.CalendarUtil;

import android.app.Application;
import android.content.Context;

public class FestivalAppLoader extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        
        //Get weekdays names from resources
        CalendarUtil.load(this);;
    }

}
