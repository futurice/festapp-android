package com.futurice.festapp.analytics;

import android.app.Activity;

public class AnalyticsTrackingActivity extends Activity {

	@Override
	protected void onStart() {
		super.onStart();
		TagManagerUtils.pushOpenScreenEvent(this, getClass().getName());
	}

	@Override
	protected void onStop() {
		super.onStop();
		TagManagerUtils.pushCloseScreenEvent(this, getClass().getName());
	}
}
