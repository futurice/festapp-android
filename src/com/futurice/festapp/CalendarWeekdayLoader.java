package com.futurice.festapp;

import com.futurice.festapp.util.CalendarUtil;

import android.app.Application;
import android.content.Context;

public class CalendarWeekdayLoader extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        CalendarUtil.load(this);;
    }

}
