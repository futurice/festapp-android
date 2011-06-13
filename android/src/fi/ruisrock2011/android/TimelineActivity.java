package fi.ruisrock2011.android;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.domain.to.DaySchedule;
import fi.ruisrock2011.android.domain.to.FestivalDay;
import fi.ruisrock2011.android.ui.GigTimelineWidget;
import fi.ruisrock2011.android.util.CalendarUtil;
import fi.ruisrock2011.android.util.RuisrockConstants;
import fi.ruisrock2011.android.util.UIUtil;

/**
 * View for showing the Schedule.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class TimelineActivity extends Activity {
	
	private static final String TAG = "TimelineActivity";
	
	private static final long NOW_MARKER_FREQUENCY = 60 * 1000L;
	
	private FestivalDay festivalDay;
	private DaySchedule daySchedule;
	private LinearLayout stageLayout;
	private LinearLayout gigLayout;
	private HorizontalScrollView scrollView;
	private Vibrator vibrator;
	private LayoutInflater inflater;
	private Date timelineStartMoment;
	private Date timelineEndMoment;
	private Integer initialScrollTo;
	
	private static final int TIMELINE_END_OFFSET = 30;
	private static final int HOUR_MARKER_WIDTH = 24;
	private static int ROW_HEIGHT = 66;
	
	// GESTURES
	private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
	
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			updateCurrentTimeline();
			handler.postDelayed(this, NOW_MARKER_FREQUENCY);
		}
	};
	private Handler handler = new Handler();
	private GigTimelineWidget gigWidget;
	
	private OnClickListener gigWidgetClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v instanceof GigTimelineWidget) {
				GigTimelineWidget gigWidget = (GigTimelineWidget) v;
				gigWidget.setBackgroundResource(R.drawable.schedule_gig_hilight);
				vibrator.vibrate(50l);
				Intent artistInfo = new Intent(getBaseContext(), ArtistInfoActivity.class);
				TimelineActivity.this.gigWidget = gigWidget;
			    artistInfo.putExtra("gig.id", gigWidget.getGig().getId());
			    startActivityForResult(artistInfo, 0);
			}
		}
	};
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (gigWidget != null) {
				Gig gig = GigDAO.findGig(this, gigWidget.getGig().getId());
				gigWidget.setFavorite(gig.isFavorite());
			}
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		inflater = LayoutInflater.from(this);
		ROW_HEIGHT = (int) getResources().getDimension(R.dimen.timeline_gigHeight);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		setFestivalDay();
		daySchedule = GigDAO.findDaySchedule(this, festivalDay);
		findViewById(R.id.timelineNowLine).setVisibility(View.GONE);
		scrollView = (HorizontalScrollView) findViewById(R.id.timelineScrollView);
		setTimelineStartAndEndMoments();
		constructUiElements();
		
		handler.postDelayed(runnable, NOW_MARKER_FREQUENCY);
		
		// Gestures
        gestureDetector = new GestureDetector(new GuitarSwipeListener());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        scrollView.setOnTouchListener(gestureListener);
        
		if (initialScrollTo != null && initialScrollTo > 0) {
			scrollView.post(new Runnable() {
				public void run() {
					scrollView.smoothScrollTo(initialScrollTo, 0);
				}
			});
		}
		
		showInitialFavoriteInfoOnFirstVisit(this);
	}
	
	private void showInitialFavoriteInfoOnFirstVisit(Context context) {
		SharedPreferences pref = context.getSharedPreferences(RuisrockConstants.PREFERENCE_GLOBAL, Context.MODE_PRIVATE);
		final String key = RuisrockConstants.PREFERENCE_SHOW_FAVORITE_INFO;
		
		// TODO: Start DEBUG
		/*
		Editor ed = pref.edit();
		ed.putBoolean(key, true);
		ed.commit();
		*/
		// END DEBUG
		
		if (pref.getBoolean(key, true)) {
			Editor editor = pref.edit();
			editor.putBoolean(key, false);
			editor.commit();
			UIUtil.showDialog(context.getString(R.string.timelineActivity_initialInfo_title), context.getString(R.string.timelineActivity_initialInfo_msg), context);
		}
	}
	
	private void setTimelineStartAndEndMoments() {
		Calendar cal = Calendar.getInstance();
		if (daySchedule.getEarliestTime() != null) {
			cal.setTime(daySchedule.getEarliestTime());
			cal.add(Calendar.MINUTE, -30);
			this.timelineStartMoment = cal.getTime();
		}
		if (daySchedule.getLatestTime() != null) {
			cal.setTime(daySchedule.getLatestTime());
			cal.add(Calendar.MINUTE, TIMELINE_END_OFFSET);
			this.timelineEndMoment = cal.getTime();
		}
	}

	private void updateCurrentTimeline() {
		Date now = CalendarUtil.getNow();
		if (timelineStartMoment == null || timelineEndMoment == null) {
			return;
		}
		View line = findViewById(R.id.timelineNowLine);
		line.bringToFront();
		if (now.after(timelineStartMoment) && now.before(timelineEndMoment)) {
			line.setVisibility(View.VISIBLE);
			TextView marginView = (TextView) findViewById(R.id.timelineNowMargin);
			int leftMargin = CalendarUtil.getMinutesBetweenTwoDates(timelineStartMoment, now) * GigTimelineWidget.PIXELS_PER_MINUTE - HOUR_MARKER_WIDTH/2 - 3;
			initialScrollTo = leftMargin - getWindowManager().getDefaultDisplay().getWidth()/2;
			marginView.setWidth(leftMargin);
		} else {
			line.setVisibility(View.GONE);
		}
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
		textView.setHeight(ROW_HEIGHT);
		textView.setPadding(1, 10, 1, 1);
		gigLayout.addView(textView);
		int row = 1;
		for (String stage : stageGigs.keySet()) {
			LinearLayout stageRow = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 2, 0, 2);
			stageRow.setLayoutParams(params);
			stageRow.setOrientation(LinearLayout.HORIZONTAL);

			Date previousTime = timelineStartMoment;
			for (Gig gig : stageGigs.get(stage)) {
				if (previousTime.before(gig.getStartTime())) {
					int margin = GigTimelineWidget.PIXELS_PER_MINUTE * CalendarUtil.getMinutesBetweenTwoDates(previousTime, gig.getStartTime());
					TextView tv = new TextView(this);
					tv.setHeight(ROW_HEIGHT);
					tv.setWidth(margin);
					stageRow.addView(tv);
				}
				
				GigTimelineWidget gigWidget = new GigTimelineWidget(this, null, gig, previousTime);
				stageRow.addView(gigWidget);
				if (gig.getEndTime().equals(daySchedule.getLatestTime())) {
					int margin = GigTimelineWidget.PIXELS_PER_MINUTE * TIMELINE_END_OFFSET;
					TextView tv = new TextView(this);
					tv.setHeight(ROW_HEIGHT);
					tv.setWidth(margin);
					stageRow.addView(tv);
				}
				
				gigWidget.setOnClickListener(gigWidgetClickListener);
				previousTime = gig.getEndTime();
			}
			gigLayout.addView(getGuitarString(row++));
			gigLayout.addView(stageRow);
		}
		if (row > 1) {
			gigLayout.addView(getGuitarString(row++));
		}
	}
	
	private View getGuitarString(int i) {
		LinearLayout llAlso = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.timeline_guitarStringHeight));
		llAlso.setLayoutParams(params);
		llAlso.setOrientation(LinearLayout.HORIZONTAL);
		
		if (i > 6) {
			i = 6;
		}
		if (i < 1) {
			i = 1;
		}
		int imageId = getResources().getIdentifier(RuisrockConstants.DRAWABLE_GUITAR_STRING_PREFIX + i, "drawable", getPackageName());
		llAlso.setBackgroundResource(imageId);
		
		return llAlso;
	}


	
	private void addTimeline() {
		if (timelineStartMoment == null || timelineEndMoment == null) {
			return;
		}
		
		LinearLayout numbersLayout = (LinearLayout) findViewById(R.id.timelineNumbers);
		LinearLayout timelineVerticalLines = (LinearLayout) findViewById(R.id.timelineVerticalLines);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(timelineStartMoment);
		
		int minutes = 60 - cal.get(Calendar.MINUTE);
		TextView tv = new TextView(this);
		tv.setHeight(ROW_HEIGHT);
		tv.setWidth(GigTimelineWidget.PIXELS_PER_MINUTE * minutes - getResources().getDimensionPixelSize(R.dimen.timeline_hourText_offset));
		numbersLayout.addView(tv);
		
		tv = new TextView(this);
		tv.setWidth(GigTimelineWidget.PIXELS_PER_MINUTE * minutes - HOUR_MARKER_WIDTH/2);
		timelineVerticalLines.addView(tv);
		cal.add(Calendar.MINUTE, minutes);
		
		while (cal.getTime().before(timelineEndMoment)) {
			tv = new TextView(this);
			String hour = cal.get(Calendar.HOUR_OF_DAY) + ":00";
			if (hour.length() == 4) {
				hour = "0" + hour;
			}
			tv.setText(hour);
			tv.setMinHeight(ROW_HEIGHT);
			minutes = CalendarUtil.getMinutesBetweenTwoDates(cal.getTime(), timelineEndMoment);
			minutes = (minutes < 60) ? minutes : 60;
			tv.setMinWidth(GigTimelineWidget.PIXELS_PER_MINUTE * minutes);
			numbersLayout.addView(tv);
			
			inflater.inflate(R.layout.timeline_hour_marker, timelineVerticalLines);
			
			tv = new TextView(this);
			tv.setText("");
			int width = GigTimelineWidget.PIXELS_PER_MINUTE * minutes - (HOUR_MARKER_WIDTH);
			tv.setWidth(width);
			timelineVerticalLines.addView(tv);
			
			cal.add(Calendar.HOUR, 1);
		}
		
		
		updateCurrentTimeline();
	}

	private void addStages() {
		List<String> stages = daySchedule.getStages();
		if (stages != null && !stages.isEmpty()) {
			for (int i = 0; i < stages.size(); i++) {
				addStageName(GigDAO.truncateStageName(stages.get(i)));
			}
		}
		stageLayout.bringToFront();
	}
	
	private void addStageName(String name) {
		View parent = inflater.inflate(R.layout.stage_timeline_box, stageLayout, false);
		TextView textView = (TextView) parent.findViewWithTag("stageName");
		textView.setText(name);
		stageLayout.addView(parent);
	}


	private void setFestivalDay() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			FestivalDay festivalDay = (FestivalDay) getIntent().getExtras().get("festivalDay");
			this.festivalDay = festivalDay;
		}
	}
	
	class GuitarSwipeListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
            	if(Math.abs(e1.getY() - e2.getY()) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            		Random random = new Random();
            		MediaPlayer mp = null;
            		if (random.nextBoolean()) {
            			mp = MediaPlayer.create(getBaseContext(), R.raw.guitar1);
            		} else {
            			mp = MediaPlayer.create(getBaseContext(), R.raw.guitar2);
            		}
            		vibrator.vibrate(150l);
            		mp.start();
            		mp.setOnCompletionListener(new OnCompletionListener() {
            			@Override
            			public void onCompletion(MediaPlayer mp) {
            				mp.release();
            			}
            		});
            	}
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }

}
