package com.futurice.festapp.service;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.futurice.festapp.R;
import com.futurice.festapp.RuisrockMainActivity;
import com.futurice.festapp.dao.ConfigDAO;
import com.futurice.festapp.dao.GigDAO;
import com.futurice.festapp.dao.NewsDAO;
import com.futurice.festapp.domain.Gig;
import com.futurice.festapp.domain.NewsArticle;
import com.futurice.festapp.util.CalendarUtil;
import com.futurice.festapp.util.HTTPUtil;
import com.futurice.festapp.util.RuisrockConstants;

/**
 * Application background services.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class RuisrockService extends Service{

	private static final String TAG = RuisrockService.class.getSimpleName();
	private int counter = -1;
	private PendingIntent alarmIntent;
	private TimerTask backendTask = new TimerTask() {
		@Override
		public void run() {
			Log.i(TAG, "Starting backend operations");
			counter++;
			try {
				Date nowDate = new Date();
				if (nowDate.before(GigDAO.getEndOfSunday())) {
					alertGigs();
					if (counter % 12 == 0) { // every hour
						Log.i(TAG, "Executing 1-hour operations.");
						updateGigs();
						updateNewsArticles();
					}
					if (counter % (12 * 5) == 0) { // every 5 hours
						Log.i(TAG, "Executing 5-hour operations.");
						updateFoodAndDrinkPage();
						// TODO: Ruisrock 2012. Transportation and services not
						// updated from server.
						// updateTransportationPage();
						// updateServicesPageData();
						updateFrequentlyAskedQuestionsPageData();
					}
				} else {
					Log.i(TAG, "Stopping service due to date constraint.");
					stopSelf();
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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Running cron tasks");
		new Thread() {
			public void run() {
				Log.d(TAG, "Running backend task");
				backendTask.run();
				Log.d(TAG, "Backend task finished");
			}
		}.start();
		return super.onStartCommand(intent, flags, startId);
	}
	private void notify(Gig gig) {
		Intent contentIntent = new Intent(getBaseContext(),
				RuisrockMainActivity.class);
		contentIntent.putExtra("alert.gig.id", gig.getId());
		int uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
		PendingIntent pending = PendingIntent.getActivity(getBaseContext(),
				uniqueId, contentIntent, 0);

		String tickerText = gig.getArtist() + ": " + gig.getOnlyStageAndTime();
		notify(pending, gig.getId(), tickerText, gig.getArtist(),
				gig.getOnlyStageAndTime());
	}

	private void notify(NewsArticle article) {
		Intent contentIntent = new Intent(getBaseContext(),
				RuisrockMainActivity.class);
		contentIntent.putExtra("alert.newsArticle.url", article.getUrl());
		int uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
		PendingIntent pending = PendingIntent.getActivity(getBaseContext(),
				uniqueId, contentIntent, 0);

		String title = article.getTitle();
		notify(pending, article.getUrl(), title, article.getDateString(), title);
	}

	private void notify(PendingIntent pending, String tagId, String tickerText,
			String contentTitle, String contentText) {
		Notification notification = new Notification(R.drawable.notification,
				tickerText, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.setLatestEventInfo(getBaseContext(), contentTitle,
				contentText, pending);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(tagId, 0, notification);
	}

	private void updateNewsArticles() {
		try {
			if (HTTPUtil.isContentUpdated(RuisrockConstants.NEWS_JSON_URL,
					ConfigDAO.getEtagForNews(getBaseContext()))) {
				List<NewsArticle> newArticles = NewsDAO
						.updateNewsOverHttp(getBaseContext());
				if (newArticles != null && newArticles.size() > 0) {
					for (NewsArticle article : newArticles) {
						if (article.getDate() != null
								&& CalendarUtil.getMinutesBetweenTwoDates(
										article.getDate(), new Date()) < RuisrockConstants.SERVICE_NEWS_ALERT_THRESHOLD_IN_MINUTES) {
							notify(article);
						}
					}
				}
				Log.i(TAG, "Successfully updated data for News.");
			} else {
				Log.i(TAG, "News were up-to-date.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not update News.", e);
		}
	}

	/*private void updateServicesPageData() {
		try {
			if (HTTPUtil.isContentUpdated(RuisrockConstants.SERVICES_JSON_URL,
					ConfigDAO.getEtagForServices(getBaseContext()))) {
				ConfigDAO.updateServicePagesOverHttp(getBaseContext());
				Log.i(TAG, "Successfully updated data for Services.");
			} else {
				Log.i(TAG, "Services data was up-to-date.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not update Services data.", e);
		}
	}*/

	private void updateFrequentlyAskedQuestionsPageData() {
		try {
			if (HTTPUtil
					.isContentUpdated(
							RuisrockConstants.FREQUENTLY_ASKED_QUESTIONS_JSON_URL,
							ConfigDAO
									.getEtagForFrequentlyAskedQuestions(getBaseContext()))) {
				ConfigDAO
						.updateFrequentlyAskedQuestionsPagesOverHttp(getBaseContext());
				Log.i(TAG,
						"Successfully updated data for FrequentlyAskedQuestions.");
			} else {
				Log.i(TAG, "FrequentlyAskedQuestions data was up-to-date.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not update FrequentlyAskedQuestions data.", e);
		}
	}

	private void updateFoodAndDrinkPage() {
		try {
			if (HTTPUtil.isContentUpdated(
					RuisrockConstants.FOOD_AND_DRINK_HTML_URL,
					ConfigDAO.getEtagForFoodAndDrink(getBaseContext()))) {
				ConfigDAO.updateFoodAndDrinkPageOverHttp(getBaseContext());
				Log.i(TAG, "Successfully updated data for FoodAndDrink.");
			} else {
				Log.i(TAG, "FoodAndDrink-page was up-to-date.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not update FoodAndDrink-page.", e);
		}
	}

	/*private void updateTransportationPage() {
		try {
			if (HTTPUtil.isContentUpdated(
					RuisrockConstants.TRANSPORTATION_HTML_URL,
					ConfigDAO.getEtagForTransportation(getBaseContext()))) {
				ConfigDAO.updateTransportationPageOverHttp(getBaseContext());
				Log.i(TAG, "Successfully updated data for Transportation.");
			} else {
				Log.i(TAG, "Transportation-page was up-to-date.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Could not update Transportation-page.", e);
		}
	}*/

	private void updateGigs() {
		try {
			if (HTTPUtil.isContentUpdated(RuisrockConstants.GIGS_JSON_URL,
					ConfigDAO.getEtagForGigs(getBaseContext()))) {
				GigDAO.updateGigsOverHttp(getBaseContext());
				Log.i(TAG, "Successfully updated Gigs.");
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
		Intent intent = new Intent("CHECK_ALARMS");
		alarmIntent = PendingIntent.getBroadcast(this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		long wait = RuisrockConstants.SERVICE_INITIAL_WAIT_TIME;
		long interval = RuisrockConstants.SERVICE_FREQUENCY;
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, wait, interval, alarmIntent);;
		Log.i(TAG, "Creating service");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Destroying service");
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.cancel(alarmIntent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
}
