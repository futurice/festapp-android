package fi.ruisrock2011.android.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.ArtistInfoActivity;
import fi.ruisrock2011.android.RuisrockMainActivity;
import fi.ruisrock2011.android.dao.ConfigDAO;
import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.dao.NewsDAO;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.domain.NewsArticle;
import fi.ruisrock2011.android.rss.RSSItem;
import fi.ruisrock2011.android.rss.RSSReader;
import fi.ruisrock2011.android.util.HTTPUtil;
import fi.ruisrock2011.android.util.RuisrockConstants;

/**
 * Application background services.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class RuisrockService extends Service {
	
	private static final String TAG = RuisrockService.class.getSimpleName();
	private Timer timer;
	private int counter = 0;
	
	private TimerTask backendTask = new TimerTask() {
		@Override
		public void run() {
			Log.i(TAG, "Starting backend operations");
			counter++;
			try {
				alertGigs();
				if (counter % 12 == 0) { // every hour
					updateGigs();
					updateNewsArticles();
					updateFoodAndDrinkPage();
					updateTransportationPage();
				}
			} catch (Throwable t) {
				Log.e(TAG, "Failed execute backend operations", t);
			} finally {
				Log.i(TAG, "Finished backend operations");
			}
		}
	};
	
	private void alertGigs() {
		List<Gig> gigs = GigDAO.findGigsToAlert(getBaseContext());
		if (gigs.size() > 0) {
			for (Gig gig : gigs) {
				notify(gig);
			}
		}
	}
	
	private void notify(Gig gig) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
	    Intent contentIntent = new Intent(getBaseContext(), RuisrockMainActivity.class);
	    contentIntent.putExtra("alert.gig.id", gig.getId());
	    int uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
	    PendingIntent pending = PendingIntent.getActivity(getBaseContext(), uniqueId, contentIntent, 0);

	    Notification notification = new Notification(R.drawable.notification, gig.getArtist() + ": " + gig.getStageAndTime(), System.currentTimeMillis());
	    notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    notification.defaults |= Notification.DEFAULT_SOUND;
	    notification.defaults |= Notification.DEFAULT_VIBRATE;
	    notification.setLatestEventInfo(getBaseContext(), gig.getArtist(), gig.getStageAndTime(), pending);
	    notificationManager.notify(gig.getId(), 0, notification);
	}
	
	private void updateNewsArticles() {
		try {
			if (HTTPUtil.isContentUpdated(RuisrockConstants.NEWS_JSON_URL, ConfigDAO.getEtagForNews(getBaseContext()))) {
				NewsDAO.updateNewsOverHttp(getBaseContext());
			} else {
				Log.i(TAG, "News were up-to-date.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not update News.", e);
		}
	}
	
	private void updateFoodAndDrinkPage() {
		try {
			if (HTTPUtil.isContentUpdated(RuisrockConstants.FOOD_AND_DRINK_HTML_URL, ConfigDAO.getEtagForFoodAndDrink(getBaseContext()))) {
				ConfigDAO.updateFoodAndDrinkPageOverHttp(getBaseContext());
			} else {
				Log.i(TAG, "FoodAndDrink-page was up-to-date.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not update FoodAndDrink-page.", e);
		}
	}
	
	private void updateTransportationPage() {
		try {
			if (HTTPUtil.isContentUpdated(RuisrockConstants.TRANSPORTATION_HTML_URL, ConfigDAO.getEtagForTransportation(getBaseContext()))) {
				ConfigDAO.updateTransportationPageOverHttp(getBaseContext());
			} else {
				Log.i(TAG, "Transportation-page was up-to-date.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not update Transportation-page.", e);
		}
	}
	
	private void updateGigs() {
		try {
			if (HTTPUtil.isContentUpdated(RuisrockConstants.GIGS_JSON_URL, ConfigDAO.getEtagForGigs(getBaseContext()))) {
				GigDAO.updateGigsOverHttp(getBaseContext());
			} else {
				Log.i(TAG, "Gigs were up-to-date.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not update Gigs.", e);
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Creating service");
		
		timer = new Timer("RuisrockTimer");
		timer.schedule(backendTask, 1000L, RuisrockConstants.SERVICE_FREQUENCY);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Destroying service");
		
		timer.cancel();
		timer = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
