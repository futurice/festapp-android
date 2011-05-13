package fi.ruisrock.android;

import java.util.Date;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.domain.to.FestivalDay;

/**
 * TabActivity for the Festival Schedule.
 * 
 * Contains tabs for Friday, Saturday and Sunday.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class ScheduleTabActivity extends TabActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_tabs);
		
		TabHost tabHost = getTabHost();
		addTabSpec(tabHost, FestivalDay.FRIDAY);
		addTabSpec(tabHost, FestivalDay.SATURDAY);
		addTabSpec(tabHost, FestivalDay.SUNDAY);
	}
	
	private void addTabSpec(TabHost tabHost, FestivalDay festivalDay) {
		TabSpec tabSpec = tabHost.newTabSpec(festivalDay.name());
		tabSpec.setIndicator(festivalDay.getFinnishName(), getResources().getDrawable(R.drawable.icon));
		
	    Intent intent = new Intent(getBaseContext(), ScheduleDayActivity.class);
	    intent.putExtra("festivalDay", festivalDay);
		tabSpec.setContent(intent);
		tabHost.addTab(tabSpec);
		tabHost.setCurrentTabByTag(GigDAO.getFestivalDay(new Date()).name());
	}
	
}
