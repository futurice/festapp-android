package fi.ruisrock.android;

import com.flurry.android.FlurryAgent;

import android.app.TabActivity;

public class BaseTabActivity extends TabActivity {
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
