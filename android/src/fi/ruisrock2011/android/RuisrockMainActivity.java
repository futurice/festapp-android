package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.dao.NewsDAO;
import fi.ruisrock2011.android.domain.NewsArticle;
import fi.ruisrock2011.android.service.RuisrockService;
import fi.ruisrock2011.android.util.CalendarUtil;

/**
 * Main activity.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class RuisrockMainActivity extends Activity {
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_menu_bands:
				startActivity(new Intent(getBaseContext(), ArtistListActivity.class));
				break;
			case R.id.main_menu_timetable:
				startActivity(new Intent(getBaseContext(), ScheduleTabActivity.class));
				break;
			case R.id.main_menu_map:
				startActivity(new Intent(getBaseContext(), MapActivity.class));
				break;
			case R.id.main_menu_info:
				startActivity(new Intent(getBaseContext(), InfoPageActivity.class));
				break;
			case R.id.main_menu_news:
				startActivity(new Intent(getBaseContext(), NewsListActivity.class));
				break;
//			case R.id.mainGridFonecta:
//				startActivity(new Intent(getBaseContext(), FonectaActivity.class));
//				AnalyticsHelper.sendAnalytics(RuisrockMainActivity.this, AnalyticsHelper.EVENT_02_TAB);
//				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (CalendarUtil.getNow().before(GigDAO.getEndOfSunday())) {
			startService(new Intent(this, RuisrockService.class));
		}
		createMainMenuItems();
		handleNotificationEvents();
	}
	
	private void createMainMenuItems() {
		findViewById(R.id.main_menu_info).setOnClickListener(clickListener);
		findViewById(R.id.main_menu_bands).setOnClickListener(clickListener);
		findViewById(R.id.main_menu_timetable).setOnClickListener(clickListener);
		findViewById(R.id.main_menu_map).setOnClickListener(clickListener);
//		findViewById(R.id.mainGridFonecta).setOnClickListener(clickListener);
		findViewById(R.id.main_menu_news).setOnClickListener(clickListener);
	}
	
	private void handleNotificationEvents() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String alertGigId = extras.getString("alert.gig.id");
			String newsUrl = extras.getString("alert.newsArticle.url");
			if (alertGigId != null) {
				Intent artistInfo = new Intent(getBaseContext(), ArtistInfoActivity.class);
			    artistInfo.putExtra("gig.id", alertGigId);
			    startActivity(artistInfo);
			} else if (newsUrl != null) {
				NewsArticle article = NewsDAO.findNewsArticle(getBaseContext(), newsUrl);
				if (article != null) {
					Intent i = new Intent(getBaseContext(), NewsContentActivity.class);
					i.putExtra("news.title", article.getTitle());
					i.putExtra("news.date", article.getDateString());
					i.putExtra("news.content", article.getContent());
				    startActivity(i);
				}
			}
		}
	}

	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		switch (itemId) {
		case R.id.menuNews:
			Intent settingsActivity = new Intent(getBaseContext(), NewsListActivity.class);
			startActivity(settingsActivity);
			break;
		}
		return false;
	}
	*/
	
}
