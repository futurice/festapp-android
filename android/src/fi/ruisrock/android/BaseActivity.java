package fi.ruisrock.android;

import android.app.Activity;

import com.flurry.android.FlurryAgent;

public class BaseActivity extends Activity{
	public void onStart() {
		super.onStart();
		FlurryAgent.setReportLocation(false);
		FlurryAgent.onStartSession(this, "JPDYF8CBJHTZNBF4QXHY");
	}
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
