package com.futurice.festapp.service;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.futurice.festapp.FestAppMainActivity;
import com.futurice.festapp.R;
import com.futurice.festapp.dao.ConfigDAO;
import com.futurice.festapp.dao.GigDAO;
import com.futurice.festapp.dao.NewsDAO;
import com.futurice.festapp.domain.Gig;
import com.futurice.festapp.domain.NewsArticle;
import com.futurice.festapp.util.CalendarUtil;
import com.futurice.festapp.util.FestAppConstants;
import com.futurice.festapp.util.HTTPUtil;

/**
 * Application background services.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class FestAppService extends Service{

	private static final String TAG = FestAppService.class.getSimpleName();
	private int counter = -1;
	private PendingIntent alarmIntent;
	private Semaphore dataUpdateSem = new Semaphore(1);
	private TimerTask backendTask = new TimerTask() {
		@Override
		public void run() {
			Log.i(TAG, "Starting backend operations");
			counter++;
			try {
				if (FestAppConstants.F_FORCE_DATA_FETCH){
					doAllTasks();
					return;
				}
				if (new Date().before(GigDAO.getEndOfSunday())) {
					alertGigs();
					if (counter % 12 == 0) { // every hour
						Log.i(TAG, "Executing 1-hour operations.");
						doFrequentTasks();
					}
					if (counter % (12 * 5) == 0) {// every 5 hours
						Log.i(TAG, "Executing 5-hour operations.");
						doSeldomTasks();
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
	
	private void doAllTasks() {
		doSeldomTasks();
		doFrequentTasks();
	}
	
	private void doSeldomTasks() {
		updateFoodAndDrinkPage();
		updateTransportationPage();
		updateServicesPageData();
		updateFrequentlyAskedQuestionsPageData();
	}
	
	private void doFrequentTasks() {
		updateGigs();
		updateNewsArticles();
	}

	private void alertGigs() {
		for (Gig gig : GigDAO.findGigsToAlert(this)) {
			notify(gig);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final boolean force = intent.getExtras() != null && 
				intent.getBooleanExtra("com.futurice.festapp.service.FORCE", false);
		
		Log.d(TAG, "Running cron tasks");
		new Thread() {
			public void run() {
				try {
					dataUpdateSem.acquire();
					if (force){
						FestAppConstants.F_FORCE_DATA_FETCH = true;
						Log.d(TAG, "Forced data reload");
					}
					Log.d(TAG, "Running backend task");
					backendTask.run();
					Log.d(TAG, "Backend task finished");
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				} finally{
					FestAppConstants.F_FORCE_DATA_FETCH = false;
					dataUpdateSem.release();
				}
			}
		}.start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private int idCounter = (int)System.currentTimeMillis();
	private void notify(Gig gig) {
		Intent contentIntent = new Intent(this, FestAppMainActivity.class);
		contentIntent.putExtra("alert.gig.id", gig.getId());
		PendingIntent pending = PendingIntent.getActivity(this, idCounter++, contentIntent, 0);

		String tickerText = gig.getArtist() + ": " + gig.getOnlyStageAndTime();
		notify(pending, gig.getId(), tickerText, gig.getArtist(),
				gig.getOnlyStageAndTime());
	}

	private void notify(NewsArticle article) {
		Intent contentIntent = new Intent(getBaseContext(),
				FestAppMainActivity.class);
		contentIntent.putExtra("alert.newsArticle.url", article.getUrl());
		PendingIntent pending = PendingIntent.getActivity(getBaseContext(),
				idCounter++, contentIntent, 0);

		String title = article.getTitle();
		notify(pending, article.getUrl(), title, article.getDateString(), title);
	}

	@SuppressWarnings("deprecation")
	private void notify(PendingIntent pending, String tagId, String tickerText,
			String contentTitle, String contentText) {
		Builder builder = new Notification.Builder(this);
		builder.setSmallIcon(R.drawable.notification);
		builder.setTicker(tickerText);
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(true);
		builder.setContentTitle(contentTitle);
		builder.setContentText(contentText);
		builder.setContentIntent(pending);
		builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(tagId, 0, builder.getNotification());
	}

	private void updateNewsArticles() {
		try {
			String etag = ConfigDAO.getAttributeValue(ConfigDAO.ATTR_ETAG_FOR_NEWS, this);
			if (!HTTPUtil.isContentUpdated(FestAppConstants.NEWS_JSON_URL, etag)) {
				Log.i(TAG, "News were up-to-date.");
				return;
			}
			List<NewsArticle> newArticles = NewsDAO.updateNewsOverHttp(this);
			if (newArticles != null && newArticles.size() > 0) {
				for (NewsArticle article : newArticles) {
					if (article.getDate() == null){
						continue;
					}
					int timeBetween = CalendarUtil.getMinutesBetweenTwoDates(
							article.getDate(), new Date());
					if (timeBetween < FestAppConstants.SERVICE_NEWS_ALERT_THRESHOLD_IN_MINUTES) {
						notify(article);
					}
				}
			}
			Log.i(TAG, "Successfully updated data for News.");
		} catch (Exception e) {
			Log.e(TAG, "Could not update News.", e);
		}
	}

	private void updateServicesPageData() {
		try {
			String etag = ConfigDAO.getAttributeValue(ConfigDAO.ATTR_ETAG_FOR_SERVICES, this);
			if (!HTTPUtil.isContentUpdated(FestAppConstants.SERVICES_JSON_URL, etag)) {
				Log.i(TAG, "Services data was up-to-date.");
				return;
			}
			ConfigDAO.updateServicePagesOverHttp(getBaseContext());
			Log.i(TAG, "Successfully updated data for Services.");
		} catch (Exception e) {
			Log.e(TAG, "Could not update Services data.", e);
		}
	}

	private void updateFrequentlyAskedQuestionsPageData() {
		try {
			String etag = ConfigDAO.getAttributeValue(ConfigDAO.ATTR_ETAG_FOR_FREQUENTLY_ASKED_QUESTIONS, this);
			if (!HTTPUtil.isContentUpdated(FestAppConstants.FREQUENTLY_ASKED_QUESTIONS_JSON_URL, etag)) {
				Log.i(TAG, "FrequentlyAskedQuestions data was up-to-date.");
				return;
			}
			ConfigDAO.updateFrequentlyAskedQuestionsPagesOverHttp(getBaseContext());
			Log.i(TAG,"Successfully updated data for FrequentlyAskedQuestions.");
		} catch (Exception e) {
			Log.e(TAG, "Could not update FrequentlyAskedQuestions data.", e);
		}
	}

	private void updateFoodAndDrinkPage() {
		try {
			String etag = ConfigDAO.getAttributeValue(ConfigDAO.ATTR_ETAG_FOR_FOODANDDRINK, this);
			if (!HTTPUtil.isContentUpdated(FestAppConstants.FOOD_AND_DRINK_HTML_URL, etag)) {
				Log.i(TAG, "FoodAndDrink-page was up-to-date.");
				return;
			}
			ConfigDAO.updateFoodAndDrinkPageOverHttp(getBaseContext());
			Log.i(TAG, "Successfully updated data for FoodAndDrink.");
		} catch (Exception e) {
			Log.e(TAG, "Could not update FoodAndDrink-page.", e);
		}
	}

	private void updateTransportationPage() {
		try {
			String etag = ConfigDAO.getAttributeValue(ConfigDAO.ATTR_ETAG_FOR_TRANSPORTATION, this);
			if (!HTTPUtil.isContentUpdated(FestAppConstants.TRANSPORTATION_HTML_URL, etag)) {
				Log.i(TAG, "Transportation-page was up-to-date.");
				return;
			}
			ConfigDAO.updateTransportationPageOverHttp(getBaseContext());
			Log.i(TAG, "Successfully updated data for Transportation.");
		} catch (Exception e) {
			Log.e(TAG, "Could not update Transportation-page.", e);
		}
	}

	private void updateGigs() {
		try {
			String etag = ConfigDAO.getAttributeValue(ConfigDAO.ATTR_ETAG_FOR_GIGS, this);
			if (!HTTPUtil.isContentUpdated(FestAppConstants.GIGS_JSON_URL, etag)) {
				Log.i(TAG, "Gigs were up-to-date.");
				return;
			}
			GigDAO.updateGigsOverHttp(getBaseContext());
			Log.i(TAG, "Successfully updated Gigs.");
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
		long wait = FestAppConstants.SERVICE_INITIAL_WAIT_TIME;
		long interval = FestAppConstants.SERVICE_FREQUENCY;
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, wait, interval, alarmIntent);
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
