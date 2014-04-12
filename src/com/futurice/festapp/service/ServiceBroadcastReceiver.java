package com.futurice.festapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceBroadcastReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent ) {
    	if (intent.getExtras() != null && 
    			intent.getBooleanExtra("com.futurice.festapp.service.FORCE", false)){
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
