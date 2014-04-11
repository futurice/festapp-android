package com.futurice.festapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceBroadcastReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent ) {
    	Log.d("CHECK", "receive intent, action = " + intent.getAction());
        
    	if (intent.getAction().equals("FORCE_DATA_LOAD")){
    		Log.d("CHECK", "force data reload found");
            Intent myIntent = new Intent( context, FestAppService.class );
            myIntent.putExtra("force", true);
            context.startService( myIntent );
    	}
    	else{
            Intent myIntent = new Intent( context, FestAppService.class );
            context.startService( myIntent );
    	}
    }
}
