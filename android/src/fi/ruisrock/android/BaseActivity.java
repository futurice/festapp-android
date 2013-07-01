package fi.ruisrock.android;

import android.app.Activity;

import com.flurry.android.FlurryAgent;

public class BaseActivity extends Activity{
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
