package fi.ruisrock2011.android.dao;

import java.util.Random;

import fi.ruisrock2011.android.domain.to.HTTPBackendResponse;
import fi.ruisrock2011.android.util.HTTPUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AnalyticsHelper {

	// http://c.cem4mobile.com/?s=<application_id>&p=<event_name>&i=<visitor_id>"

	private static final String TAG = "AnalyticsHelper";

	public static final String EVENT_START = "p=Kaynnistys";
	public static final String EVENT_02_TAB = "p=020202";
	public static final String EVENT_02_CALL = "p=02_soitto";
	public static final String EVENT_02_CANCEL = "p=02_peruutus";
	private static final String ANALYTICS_URL = "http://c.cem4mobile.com/?s=ruisrock-android&";

	private static final String RUISROCK_PREFS = "ruisrock prefs";
	private static final String VISITOR_ID = "visitor_id";

	private static String getVisitorId(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(RUISROCK_PREFS, Context.MODE_PRIVATE);
		if (!preferences.contains(VISITOR_ID)) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(VISITOR_ID, "" + System.currentTimeMillis() + "" + getRandomNumber());
			editor.commit();
		}
		return "i=" + preferences.getString(VISITOR_ID, "");
	}

	private static int getRandomNumber() {
		Random random = new Random();
		return random.nextInt();
	}

	public static void sendAnalytics(final Context context, final String event) {
		new Thread() {
			public void run() {
				HTTPUtil httpUtil = new HTTPUtil();
				HTTPBackendResponse response = httpUtil.performGet(ANALYTICS_URL + event + "&" + getVisitorId(context));
				if (!response.isValid() || response.getContent() == null) {
					Log.e(TAG, "Failed to send analytics.");
				}
			}
		}.start();
	}
}
