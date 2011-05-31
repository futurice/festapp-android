package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import fi.ruisrock2011.android.service.RuisrockService;

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
			case R.id.mainGridArtists:
				startActivity(new Intent(getBaseContext(), ArtistListActivity.class));
				break;
			case R.id.mainGridSchedule:
				startActivity(new Intent(getBaseContext(), ScheduleTabActivity.class));
				break;
			case R.id.mainGridMap:
				startActivity(new Intent(getBaseContext(), MapActivity.class));
				break;
			case R.id.mainGridInfo:
				startActivity(new Intent(getBaseContext(), InfoPageActivity.class));
				break;
			case R.id.mainGridNews:
				startActivity(new Intent(getBaseContext(), NewsListActivity.class));
				break;
			case R.id.mainGridFonecta:
				startActivity(new Intent(getBaseContext(), FonectaActivity.class));
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startService(new Intent(this, RuisrockService.class));
		createMainMenuItems();
		
		String alertGigId = getAlertGigId();
		if (alertGigId != null) {
			Intent artistInfo = new Intent(getBaseContext(), ArtistInfoActivity.class);
		    artistInfo.putExtra("gig.id", alertGigId);
		    startActivity(artistInfo);
		}
	}
	
	private void createMainMenuItems() {
		findViewById(R.id.mainGridInfo).setOnClickListener(clickListener);
		findViewById(R.id.mainGridArtists).setOnClickListener(clickListener);
		findViewById(R.id.mainGridSchedule).setOnClickListener(clickListener);
		findViewById(R.id.mainGridMap).setOnClickListener(clickListener);
		findViewById(R.id.mainGridFonecta).setOnClickListener(clickListener);
		findViewById(R.id.mainGridNews).setOnClickListener(clickListener);
	}
	
	private String getAlertGigId() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String id = (String) extras.get("alert.gig.id");
			if (id != null) {
				return id;
			}
		}
		return null;
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
