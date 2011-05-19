package fi.ruisrock.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import fi.ruisrock.android.service.RuisrockService;
import fi.ruisrock.android.ui.ListItem;
import fi.ruisrock.android.ui.ListItemAdapter;
import fi.ruisrock.android.ui.map.MapImageView;

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
				//startActivity(new Intent(getBaseContext(), MapActivity.class));
				startActivity(new Intent(getBaseContext(), MapActivity.class));
				break;
			default:
				break;
			}
		}
	};
	
	/*
	private ListView mainList;
	private ListItemAdapter adapter;
	private OnItemClickListener listItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int index, long arg) {
			Object o = av.getItemAtPosition(index);
			if (o instanceof ListItem) {
				String selectedItem = ((ListItem) o).getName();
				if (selectedItem.equals(getString(R.string.Schedule))) {
					startActivity(new Intent(getBaseContext(), ScheduleTabActivity.class));
				} else if (selectedItem.equals(getString(R.string.Artists))) {
					startActivity(new Intent(getBaseContext(), ArtistListActivity.class));
				} else if (selectedItem.equals(getString(R.string.News))) {
					startActivity(new Intent(getBaseContext(), NewsListActivity.class));
				} else if (selectedItem.equals(getString(R.string.Debug))) {
					startActivity(new Intent(getBaseContext(), DebugActivity.class));
				} else if (selectedItem.equals("Test")) {
					startActivity(new Intent(getBaseContext(), TestActivity.class));
				}
			}
		}
	};
	*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startService(new Intent(this, RuisrockService.class));
		createMainMenuItems();
	}
	
	private void createMainMenuItems() {
		findViewById(R.id.mainGridInfo).setOnClickListener(clickListener);
		findViewById(R.id.mainGridArtists).setOnClickListener(clickListener);
		findViewById(R.id.mainGridSchedule).setOnClickListener(clickListener);
		findViewById(R.id.mainGridMap).setOnClickListener(clickListener);
		findViewById(R.id.mainGridFonecta).setOnClickListener(clickListener);
		
		
		/*
		mainList = (ListView) findViewById(R.id.mainList);
		List<ListItem> items = new ArrayList<ListItem>();
		items.add(new ListItem(getString(R.string.Schedule), getResources().getDrawable(R.drawable.icon)));
		items.add(new ListItem(getString(R.string.Artists), getResources().getDrawable(R.drawable.icon)));
		items.add(new ListItem(getString(R.string.News), getResources().getDrawable(R.drawable.icon)));
		items.add(new ListItem(getString(R.string.Debug), getResources().getDrawable(R.drawable.icon)));
		items.add(new ListItem("Test", getResources().getDrawable(R.drawable.icon)));
		adapter = new ListItemAdapter(this, items);
		mainList.setAdapter(adapter);
		mainList.setOnItemClickListener(listItemClickListener);
		*/
	}

	
	
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
	
}
