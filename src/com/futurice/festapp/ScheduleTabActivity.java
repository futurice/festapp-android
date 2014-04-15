package com.futurice.festapp;

import java.util.Date;

import com.futurice.festapp.dao.GigDAO;
import com.futurice.festapp.domain.to.FestivalDay;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.futurice.festapp.R;

/**
 * TabActivity for the Festival Schedule.
 * 
 * Contains tabs for Friday, Saturday and Sunday.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
@SuppressWarnings("deprecation")
public class ScheduleTabActivity extends TabActivity {
	
	private TabHost tabHost;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_tabs);
		
		tabHost = getTabHost();
		addTabSpec(FestivalDay.FRIDAY);
		addTabSpec(FestivalDay.SATURDAY);
		addTabSpec(FestivalDay.SUNDAY);
		FestivalDay day = GigDAO.getFestivalDay(new Date());
		if (day == null) {
			day = FestivalDay.FRIDAY;
		}
		tabHost.setCurrentTabByTag(day.name());
	}
	
	private void addTabSpec(FestivalDay festivalDay) {
		TabSpec tabSpec = tabHost.newTabSpec(festivalDay.name());
		
		View tabView = LayoutInflater.from(tabHost.getContext()).inflate(R.layout.schedule_tab_bg, null);
		TextView text = (TextView) tabView.findViewById(R.id.tabsText);
		text.setText(festivalDay.getLocalAbbrv(this));
		
		tabSpec.setIndicator(tabView);
		
	    Intent intent = new Intent(getBaseContext(), TimelineActivity.class);
	    intent.putExtra("festivalDay", festivalDay);
		tabSpec.setContent(intent);
		tabHost.addTab(tabSpec);
	}
	
	
	
}
