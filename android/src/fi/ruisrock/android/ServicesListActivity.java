package fi.ruisrock.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.flurry.android.FlurryAgent;

import fi.ruisrock.android.dao.ConfigDAO;
import fi.ruisrock.android.ui.ListItemStringAdapter;
import fi.ruisrock.android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ServicesListActivity extends BaseActivity {
	
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
				} else if (selected.equals(getString(R.string.service_Camping))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_CAMPING, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_Cloakroom))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_CLOAKROOM, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_Merchandise))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_MERCHANDISE, getBaseContext()));
				} else if (selected.equals(getString(R.string.service_PhoneCharging))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_SERVICES_PHONE_CHARGING, getBaseContext()));
				}
				
				intent.putExtra("subPageTitle", selected);
				startActivity(intent);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_string);
		
		list = (ListView) findViewById(R.id.list);
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View header = inflater.inflate(R.layout.list_header, null, false);

		((TextView)header.findViewById(R.id.listTitle)).setText(getResources().getString(R.string.Services));

		list.addHeaderView(header);
		
		populateListItems();
		
		FlurryAgent.logEvent("Services");
	}
	
	private void populateListItems() {
		List<String> items = new ArrayList<String>();
		items.add(getString(R.string.service_Activities));
		items.add(getString(R.string.service_Camping));
		items.add(getString(R.string.service_Cloakroom));
		items.add(getString(R.string.service_Merchandise));
		items.add(getString(R.string.service_PhoneCharging));
		Collections.sort(items);
		adapter = new ListItemStringAdapter(this, items);
		list.setAdapter(adapter);
		list.setOnItemClickListener(listItemClickListener);
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}
