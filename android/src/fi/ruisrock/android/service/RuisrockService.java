package fi.ruisrock.android.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.dao.NewsDAO;
import fi.ruisrock.android.domain.NewsArticle;
import fi.ruisrock.android.rss.RSSItem;
import fi.ruisrock.android.rss.RSSReader;
import fi.ruisrock.android.util.RuisrockConstants;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Application background services.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class RuisrockService extends Service {
	
	private static final String TAG = RuisrockService.class.getSimpleName();
	private Timer timer;
	
	private TimerTask backendTask = new TimerTask() {
		@Override
		public void run() {
			Log.i(TAG, "Starting backend operations");
			try {
				updateNewsArticles();
				updateGigs();
			} catch (Throwable t) {
				Log.e(TAG, "Failed execute backend operations", t);
			} finally {
				Log.i(TAG, "Finished backend operations");
			}
		}
	};
	
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
		timer.schedule(backendTask, 1000L, 1 * 60 * 1000L);
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
