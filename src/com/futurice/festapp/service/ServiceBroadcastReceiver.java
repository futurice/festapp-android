package com.futurice.festapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceBroadcastReceiver extends BroadcastReceiver {
	 
    @Override
    public void onReceive(Context context, Intent intent ) {
        Intent myIntent = new Intent( context, RuisrockService.class );
        context.startService( myIntent );
    }
}
