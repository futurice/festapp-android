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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ServicesListActivity extends Activity {
	
	private ListView list;
	private ListItemStringAdapter adapter;
	private OnItemClickListener listItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int index, long arg) {
			Object o = av.getItemAtPosition(index);
			if (o instanceof String) {
				String selected = (String) o;
				Intent intent = new Intent(getBaseContext(), InfoSubPageActivity.class);
				if (selected.equals(getString(R.string.service_Activities))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_ACTIVITIES, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_BikePark))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_BIKE_PARK, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_Camping))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_CAMPING, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_Cloakroom))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_CLOAKROOM, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_Merchandise))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_MERCHANDISE, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_PhoneCharging))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_PHONE_CHARGING, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_Sponsors))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_SPONSORS, getBaseContext()));
				}
				
				intent.putExtra("subPageTitle", selected);
				startActivity(intent);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services);
		
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
