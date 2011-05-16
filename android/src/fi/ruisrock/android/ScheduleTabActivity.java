package fi.ruisrock.android;

import java.util.Date;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
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
		
		/*
		{
			// Testing
			final View v = tabHost.getTabWidget().getChildAt(1);
			v.setBackgroundColor(Color.TRANSPARENT);
			TextView tv = (TextView) v.findViewById(android.R.id.title);
			tv.setTextColor(Color.RED);
			//tv.setTextColor(this.getResources().getColorStateList(R.drawable.tab_text_selector));
		}
		*/

		
		tabHost.setCurrentTabByTag(GigDAO.getFestivalDay(new Date()).name());
	}
	
	private void addTabSpec(TabHost tabHost, FestivalDay festivalDay) {
		TabSpec tabSpec = tabHost.newTabSpec(festivalDay.name());
		
		TextView tabView = new TextView(this);
		LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 40, 1);
		lp3.setMargins(1, 0, 1, 0);
		tabView.setLayoutParams(lp3);
		tabView.setText(festivalDay.getFinnishName());
		//tabView.setTextColor(Color.WHITE);
		tabView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		tabView.setBackgroundDrawable(getResources().getDrawable(R.drawable.schedule_tab_item));
		//tabView.setTextColor(R.color.tab_item);
		
	    tabView.setTextColor(new ColorStateList(
	    	     
                new int[][] {

                        new int[] { android.R.attr.state_selected },

                        new int[0],

                    }, new int[] {

                        Color.rgb(255, 128, 192),

                        Color.WHITE,

                    }

                ));

		
		//tabView.setBackgroundDrawable( getResources().getDrawable(R.drawable.tab_indicator));
		tabView.setPadding(13, 0, 13, 0);

		
		tabSpec.setIndicator(tabView);
		//tabSpec.setIndicator(festivalDay.getFinnishName(), getResources().getDrawable(R.drawable.schedule_tab_item));
		
	    Intent intent = new Intent(getBaseContext(), TimelineActivity.class);
	    intent.putExtra("festivalDay", festivalDay);
		tabSpec.setContent(intent);
		tabHost.addTab(tabSpec);
	}
	
	
}
