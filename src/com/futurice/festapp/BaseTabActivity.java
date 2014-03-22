package com.futurice.festapp;

import com.flurry.android.FlurryAgent;

import android.app.TabActivity;

public class BaseTabActivity extends TabActivity {
	public void onStart() {
		super.onStart();
		FlurryAgent.setReportLocation(false);
		FlurryAgent.onStartSession(this, "K58BHQ759NZTF3PV44VW");
	}
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
