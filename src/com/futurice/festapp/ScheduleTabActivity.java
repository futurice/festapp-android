package com.futurice.festapp;

import java.util.Date;
import java.util.List;

import com.futurice.festapp.dao.FestivalDayDAO;
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
		List<FestivalDay> festivalDays = FestivalDayDAO.getFestivalDays(this);
		for(FestivalDay f : festivalDays){
			addTabSpec(f);
		}
		/*addTabSpec(FestivalDay.FRIDAY);
		addTabSpec(FestivalDay.SATURDAY);
		addTabSpec(FestivalDay.SUNDAY);*/
		
		FestivalDay day = GigDAO.getFestivalDay(new Date(),this);
		if (day == null) {
			day = FestivalDayDAO.getFirstDayOfFestival(this);
		}
		tabHost.setCurrentTabByTag(day.toString());
	}
	
	private void addTabSpec(FestivalDay festivalDay) {
		TabSpec tabSpec = tabHost.newTabSpec(festivalDay.toString());
		
		View tabView = LayoutInflater.from(tabHost.getContext()).inflate(R.layout.schedule_tab_bg, null);
		TextView text = (TextView) tabView.findViewById(R.id.tabsText);
		text.setText(festivalDay.getLocalName(this));
		
		tabSpec.setIndicator(tabView);
		
	    Intent intent = new Intent(getBaseContext(), TimelineActivity.class);
	    intent.putExtra("festivalDay", festivalDay.toString());
		tabSpec.setContent(intent);
		tabHost.addTab(tabSpec);
	}
	
	
	
}
