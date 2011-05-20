package fi.ruisrock2011.android;

import java.util.Date;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.domain.to.FestivalDay;

/**
 * TabActivity for the Festival Schedule.
 * 
 * Contains tabs for Friday, Saturday and Sunday.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
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
		tabHost.setCurrentTabByTag(GigDAO.getFestivalDay(new Date()).name());
	}
	
	private void addTabSpec(FestivalDay festivalDay) {
		TabSpec tabSpec = tabHost.newTabSpec(festivalDay.name());
		
		View tabView = LayoutInflater.from(tabHost.getContext()).inflate(R.layout.schedule_tab_bg, null);
		TextView text = (TextView) tabView.findViewById(R.id.tabsText);
		text.setText(festivalDay.getFinnishName());
		text.setTypeface(Typeface.MONOSPACE);
		
		tabSpec.setIndicator(tabView);
		
	    Intent intent = new Intent(getBaseContext(), TimelineActivity.class);
	    intent.putExtra("festivalDay", festivalDay);
		tabSpec.setContent(intent);
		tabHost.addTab(tabSpec);
	}
	
	
	
}
