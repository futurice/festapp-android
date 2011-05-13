package fi.ruisrock.android;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.domain.Gig;
import fi.ruisrock.android.domain.to.DaySchedule;
import fi.ruisrock.android.domain.to.FestivalDay;
import fi.ruisrock.android.ui.GigRelativeLayout;
import fi.ruisrock.android.ui.ScheduleGigView;
import fi.ruisrock.android.util.CalendarUtil;
import fi.ruisrock.android.util.StringUtil;

public class ScheduleDayActivity extends Activity {
	
	private FestivalDay festivalDay;
	private DaySchedule daySchedule;
	private LinearLayout stageLayout;
	private LinearLayout gigLayout;
	
	private GigRelativeLayout gl;
	
	private OnClickListener foo = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v instanceof GigRelativeLayout) {
				GigRelativeLayout gl = (GigRelativeLayout) v;
				Drawable d = gl.getBackground();
				gl.setBackgroundResource(R.drawable.artist_132);
				Intent artistInfo = new Intent(getBaseContext(), ArtistInfoActivity.class);
				ScheduleDayActivity.this.gl = gl;
			    artistInfo.putExtra("gig.id", gl.getGig().getId());
			    startActivityForResult(artistInfo, 0);
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (gl != null) {
				gl.setBackgroundResource(R.drawable.schedule_gig);
			}
		}
	}
	
	//private static final int PIXELS_PER_MINUTE = 4;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		setFestivalDay();
		daySchedule = GigDAO.findDaySchedule(this, festivalDay);
		constructUiElements();
	}
	
	
	private void constructUiElements() {
		stageLayout = (LinearLayout) findViewById(R.id.stageLayout);
		stageLayout.removeAllViews();
		addStages();
		
		
		gigLayout = (LinearLayout) findViewById(R.id.gigLayout);
		addTimeline();
		addGigs();
		
		
		
		//gigLayout.addView(new ScheduleGigView(this, daySchedule));
	}
	
	private void addGigs() {
		Map<String, List<Gig>> stageGigs = daySchedule.getStageGigs();
		
		for (String stage : stageGigs.keySet()) {
			LinearLayout llAlso = new LinearLayout(this);
			llAlso.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			llAlso.setOrientation(LinearLayout.HORIZONTAL);

			for (Gig gig : stageGigs.get(stage)) {
				GigRelativeLayout gl = new GigRelativeLayout(this, null, gig);
				/*
				TextView tv = new TextView(this);
				tv.setText(gig.getArtist());
				tv.setBackgroundResource(R.drawable.schedule_gig);
				tv.setTextColor(Color.BLACK);
				tv.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
					}
				});
				*/
				//int width = gig.getDuration() * PIXELS_PER_MINUTE;
				//gl.setMinimumWidth(width);
				//gl.setMinWidth(width);
				//gl.setMaxWidth(width);
				llAlso.addView(gl);
				gl.setOnClickListener(foo);
			}
			gigLayout.addView(llAlso);
		}
		
	}


	
	private void addTimeline() {
		Date startTime = daySchedule.getEarliestTime();
		Date endTime = daySchedule.getLatestTime();
		
		if (startTime == null || endTime == null) {
			return;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		while (cal.getTime().before(endTime)) {
			TextView tv = new TextView(this);
			tv.setText("18:00");
			gigLayout.addView(tv);
			cal.add(Calendar.HOUR, 1);
		}
		
		
		
	}

	private void addStages() {
		for (String stageName : daySchedule.getStages()) {
			TextView textView = new TextView(this);
			textView.setText(stageName);
			textView.setHeight(75);
			textView.setPadding(1, 1, 1, 1);
			stageLayout.addView(textView);
		}
	}


	private void setFestivalDay() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			FestivalDay festivalDay = (FestivalDay) getIntent().getExtras().get("festivalDay");
			this.festivalDay = festivalDay;
		}
	}
	

}
