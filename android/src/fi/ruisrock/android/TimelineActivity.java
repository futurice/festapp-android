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
import android.os.Vibrator;
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

public class TimelineActivity extends Activity {
	
	private FestivalDay festivalDay;
	private DaySchedule daySchedule;
	private LinearLayout stageLayout;
	private LinearLayout gigLayout;
	private Vibrator vibrator;
	private LayoutInflater inflater;
	
	private GigRelativeLayout gl;
	
	private OnClickListener foo = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v instanceof GigRelativeLayout) {
				GigRelativeLayout gl = (GigRelativeLayout) v;
				Drawable d = gl.getBackground();
				gl.setBackgroundResource(R.drawable.schedule_gig_hilight);
				vibrator.vibrate(50l);
				Intent artistInfo = new Intent(getBaseContext(), ArtistInfoActivity.class);
				TimelineActivity.this.gl = gl;
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
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		inflater = LayoutInflater.from(this);
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
		
		
		/*
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.timeGridContainer);
		TimelineView tl = new TimelineView(getBaseContext());
		rl.addView(tl);
		setContentView(tl);
		*/
		
		//ScheduleGigView sgv = new ScheduleGigView(this, daySchedule);
		//rl.addView(sgv);
	}
	
	private void addGigs() {
		Map<String, List<Gig>> stageGigs = daySchedule.getStageGigs();
		
		TextView textView = new TextView(this);
		textView.setText("");
		textView.setHeight(66);
		textView.setPadding(1, 10, 1, 1);
		gigLayout.addView(textView);
		for (String stage : stageGigs.keySet()) {
			LinearLayout llAlso = new LinearLayout(this);
			llAlso.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			llAlso.setOrientation(LinearLayout.HORIZONTAL);

			Calendar previousTime = Calendar.getInstance();
			previousTime.setTime(daySchedule.getEarliestTime());
			previousTime.add(Calendar.MINUTE, -30);
			for (Gig gig : stageGigs.get(stage)) {
				if (previousTime.getTime().before(gig.getStartTime())) {
					int margin = GigRelativeLayout.PIXELS_PER_MINUTE * CalendarUtil.getMinutesBetweenTwoDates(previousTime.getTime(), gig.getStartTime());
					TextView tv = new TextView(this);
					tv.setMinHeight(66);
					tv.setMinWidth(margin);
					llAlso.addView(tv);
				}
				
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
				previousTime.setTime(gig.getEndTime());
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
		
		LinearLayout timelineLayout = (LinearLayout) findViewById(R.id.timelineLayout);
		LinearLayout timelineLayoutLines = (LinearLayout) findViewById(R.id.timelineLayoutLines);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.add(Calendar.MINUTE, -30);
		
		int minutes = 60 - cal.get(Calendar.MINUTE);
		int magicNumber = 30;
		if (minutes < magicNumber) {
			minutes += 60;
		}
		int margin = GigRelativeLayout.PIXELS_PER_MINUTE * minutes - magicNumber;
		TextView tv = new TextView(this);
		tv.setMinHeight(66);
		tv.setMinWidth(margin);
		timelineLayout.addView(tv);
		
		tv = new TextView(this);
		tv.setMinWidth(margin + magicNumber);
		timelineLayoutLines.addView(tv);
		cal.add(Calendar.MINUTE, minutes);
			
		
		while (cal.getTime().before(endTime)) {
			tv = new TextView(this);
			tv.setText(cal.get(Calendar.HOUR_OF_DAY) + ":00");
			tv.setMinHeight(66);
			tv.setMinWidth(GigRelativeLayout.PIXELS_PER_MINUTE * 60);
			timelineLayout.addView(tv);
			
			View verticalLine = inflater.inflate(R.layout.vertical_line, timelineLayoutLines);
			
			tv = new TextView(this);
			tv.setText("");
			tv.setMinWidth(GigRelativeLayout.PIXELS_PER_MINUTE * 59);
			timelineLayoutLines.addView(tv);
			
			
			cal.add(Calendar.HOUR, 1);
		}
		
		
		
	}

	private void addStages() {
		TextView textView = new TextView(this);
		textView.setText("");
		textView.setHeight(66);
		textView.setPadding(1, 10, 1, 1);
		stageLayout.addView(textView);
		for (String stageName : daySchedule.getStages()) {
			textView = new TextView(this);
			textView.setText(stageName);
			textView.setHeight(66);
			textView.setPadding(1, 10, 1, 1);
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
