package com.futurice.festapp;

import android.app.Activity;
import android.os.Bundle;

public class DebugActivity extends Activity{
	
	public final static boolean F_DEBUG = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug);
	}

}
