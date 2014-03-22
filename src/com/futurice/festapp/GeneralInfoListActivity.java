package com.futurice.festapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.futurice.festapp.dao.ConfigDAO;
import com.futurice.festapp.ui.ListItemStringAdapter;

import com.futurice.festapp.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GeneralInfoListActivity extends BaseActivity {

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
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_GENERALINFO_FIRSTAID, getBaseContext()));
				} else if (selected.equals(getString(R.string.generalInfo_FrequentlyAsked))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_GENERALINFO_FREQUENTLY_ASKED, getBaseContext()));
				} else if (selected.equals(getString(R.string.generalInfo_InfoStand))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_GENERALINFO_INFO_STAND, getBaseContext()));
				} else if (selected.equals(getString(R.string.generalInfo_LostAndFound))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_GENERALINFO_LOST_AND_FOUND, getBaseContext()));
				} else if (selected.equals(getString(R.string.generalInfo_OpenHours))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_GENERALINFO_OPEN_HOURS, getBaseContext()));
				} else if (selected.equals(getString(R.string.generalInfo_Tickets))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_GENERALINFO_TICKETS, getBaseContext()));
				} else if (selected.equals(getString(R.string.generalInfo_Accessibility))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_GENERALINFO_ACCESSIBILITY, getBaseContext()));
				} else if (selected.equals(getString(R.string.generalInfo_SafetyInstructions))) {
					intent.putExtra("subPageContent", ConfigDAO.getAttributeValue(ConfigDAO.ATTR_PAGE_GENERALINFO_SAFETY_INSTRUCTIONS, getBaseContext()));
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

		((TextView)header.findViewById(R.id.listTitle)).setText(getResources().getString(R.string.GeneralInfo));

		list.addHeaderView(header);		
		
		populateListItems();
	}
	
	private void populateListItems() {
		List<String> items = new ArrayList<String>();
		items.add(getString(R.string.generalInfo_Firstaid));
		items.add(getString(R.string.generalInfo_FrequentlyAsked));
		items.add(getString(R.string.generalInfo_InfoStand));
		items.add(getString(R.string.generalInfo_LostAndFound));
		items.add(getString(R.string.generalInfo_OpenHours));
		items.add(getString(R.string.generalInfo_Tickets));
		items.add(getString(R.string.generalInfo_Accessibility));
		items.add(getString(R.string.generalInfo_SafetyInstructions));
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
