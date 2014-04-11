package com.futurice.festapp;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.futurice.festapp.dao.ConfigDAO;
import com.futurice.festapp.dao.GigDAO;
import com.futurice.festapp.dao.NewsDAO;
import com.futurice.festapp.domain.NewsArticle;
import com.futurice.festapp.service.FestAppService;

/**
 * Main activity.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class FestAppMainActivity extends Activity {
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_menu_debug:
				startActivity(new Intent(getBaseContext(), DebugActivity.class));
				break;
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
			case R.id.main_menu_faq:
				showFAQ();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Date dateNow = new Date();
		if (dateNow.before(GigDAO.getEndOfSunday())) {
			startService(new Intent(this, FestAppService.class));
		}
		createMainMenuItems();
		handleNotificationEvents();
	}
	
	private void createMainMenuItems() {
		if (DebugActivity.F_DEBUG){
			View v = findViewById(R.id.main_menu_debug);
			v.setVisibility(View.VISIBLE);
			v.setOnClickListener(clickListener);
		}
		findViewById(R.id.main_menu_info).setOnClickListener(clickListener);
		findViewById(R.id.main_menu_bands).setOnClickListener(clickListener);
		findViewById(R.id.main_menu_timetable).setOnClickListener(clickListener);
		findViewById(R.id.main_menu_map).setOnClickListener(clickListener);
		findViewById(R.id.main_menu_news).setOnClickListener(clickListener);
		findViewById(R.id.main_menu_faq).setOnClickListener(clickListener);
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
	private void showFAQ() {
		Intent intent = new Intent(this, InfoSubPageActivity.class);
		intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_GENERALINFO_FREQUENTLY_ASKED, getBaseContext()));
		intent.putExtra("subPageTitle", getString(R.string.frequently_asked_questions));
		startActivity(intent);
	}
}
