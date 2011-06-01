package fi.ruisrock2011.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.ruisrock2011.android.dao.ConfigDAO;
import fi.ruisrock2011.android.ui.ListItemStringAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GeneralInfoListActivity extends Activity {
	
	private ListView list;
	private ListItemStringAdapter adapter;
	private OnItemClickListener listItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int index, long arg) {
			Object o = av.getItemAtPosition(index);
			if (o instanceof String) {
				String selected = (String) o;
				Intent intent = new Intent(getBaseContext(), InfoSubPageActivity.class);
				if (selected.equals(getString(R.string.generalInfo_Firstaid))) {
					//intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_ACTIVITIES, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_BikePark))) {
					
				}
				
				intent.putExtra("subPageTitle", selected);
				startActivity(intent);
			}
			/*
    <string name="generalInfo.FrequentlyAsked">Usein kysyttyä</string>
    <string name="generalInfo.OpenHours">Aukioloajat</string>
    <string name="generalInfo.InfoStand">Infopiste</string>
    <string name="generalInfo.LostAndFound">Löytötavarat</string>
    <string name="generalInfo.FirstAid">Ensiapu &amp; sairaalahoito</string>
    <string name="generalInfo.Tickets">Lipunmyynti</string>
			 */
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list_string);
		((TextView) findViewById(R.id.whiteTitle)).setText(R.string.GeneralInfo);
		list = (ListView) findViewById(R.id.list);
		populateListItems();
	}
	
	private void populateListItems() {
		List<String> items = new ArrayList<String>();
		items.add(getString(R.string.service_Activities));
		items.add(getString(R.string.service_BikePark));
		items.add(getString(R.string.service_Camping));
		items.add(getString(R.string.service_Cloakroom));
		items.add(getString(R.string.service_Merchandise));
		items.add(getString(R.string.service_PhoneCharging));
		items.add(getString(R.string.service_Sponsors));
		Collections.sort(items);
		adapter = new ListItemStringAdapter(this, items);
		list.setAdapter(adapter);
		list.setOnItemClickListener(listItemClickListener);
	}

}
