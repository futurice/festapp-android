package fi.ruisrock.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.domain.Gig;
import fi.ruisrock.android.domain.to.DaySchedule;
import fi.ruisrock.android.domain.to.FestivalDay;
import fi.ruisrock.android.ui.GigTimelineWidget;
import fi.ruisrock.android.util.CalendarUtil;

public class TimelineActivity extends Activity {
	
	private static final String TAG = "TimelineActivity";
	
	private static final int TIMELINE_NUMBERS_LEFT_SHIFT = 30;
	
	private FestivalDay festivalDay;
	private DaySchedule daySchedule;
	private LinearLayout stageLayout;
	private LinearLayout gigLayout;
	private Vibrator vibrator;
	private LayoutInflater inflater;
	private Date timelineStartMoment;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			updateCurrentTimeline();
			handler.postDelayed(this, 5 * 1000L);
		}
	};
	private Handler handler = new Handler();
	
	private Date now = null;
	
	
	private GigTimelineWidget gl;
	
	private OnClickListener foo = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v instanceof GigTimelineWidget) {
				GigTimelineWidget gl = (GigTimelineWidget) v;
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
		setTimelineStartMoment();
		constructUiElements();
		
		handler.postDelayed(runnable, 5 * 1000L);
		
		/*
		timer = new Timer("TimelineActivityTimer");
		timer.schedule(timerTask, 1000L, 1 * 60 * 1000L);
		*/
	}
	
	private void setTimelineStartMoment() {
		Calendar cal = Calendar.getInstance();
		if (daySchedule.getEarliestTime() != null) {
			cal.setTime(daySchedule.getEarliestTime());
			cal.add(Calendar.MINUTE, -15);
			this.timelineStartMoment = cal.getTime();
		}
	}

	private void updateCurrentTimeline() {
		if (now == null) {
			this.now = new Date();
			try {
				now = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2011-07-08 22:00");
			} catch (Exception e) {
				
			}
			
		}
		if (now.after(timelineStartMoment) && now.before(daySchedule.getLatestTime())) {
			//LinearLayout timelineNow = (LinearLayout) findViewById(R.id.timelineNow);
			/*
			tv = new TextView(this);
			tv.setMinWidth((CalendarUtil.getMinutesBetweenTwoDates(startTime, now) + magicNumber) * GigTimelineWidget.PIXELS_PER_MINUTE);
			timelineNow.addView(tv);
			*/
			//View parent = inflater.inflate(R.layout.vertical_line, timelineNow);
			
			View line = findViewById(R.id.timelineNowLine);
			line.setVisibility(View.VISIBLE);
			line.setBackgroundColor(Color.RED);
			TextView marginView = (TextView) findViewById(R.id.timelineNowMargin);
			marginView.setMinWidth(CalendarUtil.getMinutesBetweenTwoDates(timelineStartMoment, now) * GigTimelineWidget.PIXELS_PER_MINUTE);
		} else {
			View line = findViewById(R.id.timelineNowLine);
			line.setVisibility(View.GONE);
		}
		/*
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.MINUTE, 1);
		now = cal.getTime();
		*/
	}
	
	
	private void constructUiElements() {
		stageLayout = (LinearLayout) findViewById(R.id.stageLayout);
		stageLayout.removeAllViews();
		addStages();
		
		gigLayout = (LinearLayout) findViewById(R.id.gigLayout);
		addTimeline();
		addGigs();
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
			llAlso.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			llAlso.setOrientation(LinearLayout.HORIZONTAL);

			Date previousTime = timelineStartMoment;
			for (Gig gig : stageGigs.get(stage)) {
				if (previousTime.before(gig.getStartTime())) {
					int margin = GigTimelineWidget.PIXELS_PER_MINUTE * CalendarUtil.getMinutesBetweenTwoDates(previousTime, gig.getStartTime());
					TextView tv = new TextView(this);
					tv.setMinHeight(66);
					tv.setMinWidth(margin);
					llAlso.addView(tv);
				}
				
				GigTimelineWidget gl = new GigTimelineWidget(this, null, gig, previousTime);
				llAlso.addView(gl);
				gl.setOnClickListener(foo);
				previousTime = gig.getEndTime();
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
		
		LinearLayout numbersLayout = (LinearLayout) findViewById(R.id.timelineNumbers);
		LinearLayout timelineVerticalLines = (LinearLayout) findViewById(R.id.timelineVerticalLines);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(timelineStartMoment);
		
		int minutes = 60 - cal.get(Calendar.MINUTE);
		/*
		if (minutes < TIMELINE_OFFSET) {
			minutes += 60;
		}
		*/
		TextView tv = new TextView(this);
		tv.setMinHeight(66);
		tv.setMinWidth(GigTimelineWidget.PIXELS_PER_MINUTE * minutes - TIMELINE_NUMBERS_LEFT_SHIFT);
		numbersLayout.addView(tv);
		
		tv = new TextView(this);
		tv.setMinWidth(GigTimelineWidget.PIXELS_PER_MINUTE * minutes);
		timelineVerticalLines.addView(tv);
		cal.add(Calendar.MINUTE, minutes);
		
		while (cal.getTime().before(endTime)) {
			tv = new TextView(this);
			String hour = cal.get(Calendar.HOUR_OF_DAY) + ":00";
			if (hour.startsWith("0")) {
				hour = "0" + hour;
			}
			tv.setText(hour);
			tv.setMinHeight(66);
			tv.setMinWidth(GigTimelineWidget.PIXELS_PER_MINUTE * 60);
			numbersLayout.addView(tv);
			
			View verticalLine = inflater.inflate(R.layout.vertical_line, timelineVerticalLines);
			tv = new TextView(this);
			tv.setText("");
			int width = GigTimelineWidget.PIXELS_PER_MINUTE * 59;
			tv.setWidth(width);
			//tv.setMinimumWidth(width);
			timelineVerticalLines.addView(tv);
			
			cal.add(Calendar.HOUR, 1);
		}
		
		
		updateCurrentTimeline();
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
