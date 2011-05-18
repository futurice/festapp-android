package fi.ruisrock.android.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import fi.ruisrock.android.ArtistInfoActivity;
import fi.ruisrock.android.R;
import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.dao.NewsDAO;
import fi.ruisrock.android.domain.Gig;
import fi.ruisrock.android.domain.NewsArticle;
import fi.ruisrock.android.rss.RSSItem;
import fi.ruisrock.android.rss.RSSReader;
import fi.ruisrock.android.util.RuisrockConstants;

/**
 * Application background services.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class RuisrockService extends Service {
	
	enum GigConvertTarget { ARTIST, STAGE_AND_TIME }
	
	private static final String TAG = RuisrockService.class.getSimpleName();
	private Timer timer;
	
	private TimerTask backendTask = new TimerTask() {
		@Override
		public void run() {
			Log.i(TAG, "Starting backend operations");
			try {
				//updateNewsArticles();
				//updateGigs();
				alertGigs();
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
		
	    Intent contentIntent = new Intent(this, ArtistInfoActivity.class);
	    contentIntent.putExtra("gig.id", gig.getId());
	    int uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
	    PendingIntent pending = PendingIntent.getActivity(getBaseContext(), uniqueId, contentIntent, 0);

	    Notification notification = new Notification(R.drawable.icon, gig.getArtist() + ": " + gig.getStageAndTime(), System.currentTimeMillis());
	    notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    notification.defaults |= Notification.DEFAULT_SOUND;
	    notification.defaults |= Notification.DEFAULT_VIBRATE;
	    notification.setLatestEventInfo(getBaseContext(), gig.getArtist(), gig.getStageAndTime(), pending);
	    notificationManager.notify(gig.getId(), 0, notification);
	}
	
	private void updateNewsArticles() {
		RSSReader rssReader = new RSSReader();
		List<RSSItem> feed = rssReader.loadRSSFeed(RuisrockConstants.NEWS_RSS_URL);
		if (feed != null && feed.size() > 0) {
			List<NewsArticle> articles = new ArrayList<NewsArticle>();
			for (RSSItem rssItem : feed) {
				articles.add(new NewsArticle(rssItem));
			}
			NewsDAO.replaceAll(getBaseContext(), articles);
		}
	}
	
	private void updateGigs() {
		try {
			GigDAO.updateGigsOverHttp(getBaseContext());
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
