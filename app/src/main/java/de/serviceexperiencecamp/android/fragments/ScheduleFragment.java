package de.serviceexperiencecamp.android.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import de.serviceexperiencecamp.android.R;
import de.serviceexperiencecamp.android.models.DaySchedule;
import de.serviceexperiencecamp.android.models.EventsModel;
import de.serviceexperiencecamp.android.models.pojo.Event;
import de.serviceexperiencecamp.android.utils.SubscriptionUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.serviceexperiencecamp.android.views.EventTimelineView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class ScheduleFragment extends Fragment {

    final private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private EventsModel eventsModel;
    private Observable<Event> firstEvent$ = Observable.empty(); // instead of null as default

    private TextView bookNameTextView;

    private static final int HOUR_MARKER_WIDTH = 24;
    private static int ROW_HEIGHT = 66;
    private static final int TIMELINE_END_OFFSET = 30;
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsModel = EventsModel.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        HorizontalScrollView scrollView = (HorizontalScrollView) view.findViewById(R.id.timelineScrollView);

        // Gestures
        gestureDetector = new GestureDetector(getActivity(), new GuitarSwipeListener());
        gestureListener = new View.OnTouchListener() { public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }};
        scrollView.setOnTouchListener(gestureListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookNameTextView = (TextView) getView().findViewById(R.id.title);
        firstEvent$ = getFirstEvent$(eventsModel.getEvents$());
        getDaySchedule$(eventsModel.getEvents$())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<DaySchedule>() { @Override public void call(DaySchedule daySchedule) {
                addTimeline(daySchedule);
                addGigs(daySchedule);
            }});
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribeTextView(getEventTitle$(firstEvent$), bookNameTextView);
    }

    private static Observable<Event> getFirstEvent$(Observable<List<Event>> events$) {
        return events$
            .map(new Func1<List<Event>, Event>() { @Override public Event call(List<Event> events) {
                return events.get(0);
            }});
    }

    private static Observable<String> getEventTitle$(Observable<Event> event$) {
        return event$
            .map(new Func1<Event, String>() { @Override public String call(Event event) {
                return event.title;
            }})
            .startWith("Loading...");
    }

    private Observable<DaySchedule> getDaySchedule$(Observable<List<Event>> events$) {
        return events$
            .map(new Func1<List<Event>, DaySchedule>() { @Override public DaySchedule call(List<Event> events) {
                return new DaySchedule("Saturday", events);
            }});
    }

    private DateTime getTimelineStartMoment(DaySchedule daySchedule) {
        return daySchedule.getEarliestTime().minusMinutes(TIMELINE_END_OFFSET);
    }

    private DateTime getTimelineEndMoment(DaySchedule daySchedule) {
        return daySchedule.getLatestTime().plusMinutes(TIMELINE_END_OFFSET);
    }

    private void updateCurrentTimeline(DaySchedule daySchedule) {
        DateTime timelineStartMoment = getTimelineStartMoment(daySchedule);
        DateTime timelineEndMoment = getTimelineEndMoment(daySchedule);
        DateTime now = DateTime.now();
        View line = getView().findViewById(R.id.timelineNowLine);
        line.bringToFront();
        if (now.isAfter(timelineStartMoment) && now.isBefore(timelineEndMoment)) {
            line.setVisibility(View.VISIBLE);
            TextView marginView = (TextView) getView().findViewById(R.id.timelineNowMargin);
            int duration = getDurationInMinutes(timelineStartMoment, now);
            int leftMargin = duration * EventTimelineView.PIXELS_PER_MINUTE - HOUR_MARKER_WIDTH/2 - 3;
            //initialScrollTo = leftMargin - getWindowManager().getDefaultDisplay().getWidth()/2;
            marginView.setWidth(leftMargin);
        } else {
            line.setVisibility(View.GONE);
        }
    }

    private void addTimeline(DaySchedule daySchedule) {
        DateTime timelineStartMoment = getTimelineStartMoment(daySchedule);
        DateTime timelineEndMoment = getTimelineEndMoment(daySchedule);

        LinearLayout numbersLayout = (LinearLayout) getView().findViewById(R.id.timelineNumbers);
        LinearLayout timelineVerticalLines = (LinearLayout) getView().findViewById(R.id.timelineVerticalLines);

        DateTime cursor = timelineStartMoment;

        int minutes = 60 - cursor.getMinuteOfHour();
        TextView tv = new TextView(getActivity());
        tv.setHeight(ROW_HEIGHT);
        tv.setWidth(EventTimelineView.PIXELS_PER_MINUTE * minutes - getResources().getDimensionPixelSize(R.dimen.timeline_hourText_offset));
        numbersLayout.addView(tv);

        tv = new TextView(getActivity());
        tv.setWidth(EventTimelineView.PIXELS_PER_MINUTE * minutes - HOUR_MARKER_WIDTH / 2);
        timelineVerticalLines.addView(tv);
        cursor = cursor.plusMinutes(minutes);

        while (cursor.isBefore(timelineEndMoment)) {
            tv = new TextView(this.getActivity());
            tv.setTextColor(Color.parseColor("#e32c22"));
            tv.setTypeface(null, Typeface.BOLD);

            String hour = "" + cursor.getHourOfDay() + ":00";
            if (hour.length() == 4) {
                hour = "0" + hour;
            }
            tv.setText(hour);
            tv.setMinHeight(ROW_HEIGHT);
            minutes = getDurationInMinutes(cursor, timelineEndMoment);
            minutes = (minutes < 60) ? minutes : 60;
            tv.setMinWidth(EventTimelineView.PIXELS_PER_MINUTE * minutes);
            numbersLayout.addView(tv);

            this.getActivity().getLayoutInflater().inflate(R.layout.timeline_hour_marker, timelineVerticalLines);

            tv = new TextView(this.getActivity());
            tv.setTextColor(Color.parseColor("#e32c22"));
            tv.setTypeface(null, Typeface.BOLD);

            tv.setText("");
            int width = EventTimelineView.PIXELS_PER_MINUTE * minutes - (HOUR_MARKER_WIDTH);
            tv.setWidth(width);
            timelineVerticalLines.addView(tv);

            cursor = cursor.plusHours(1);
        }
        updateCurrentTimeline(daySchedule);
    }

    private static int getDurationInMinutes(DateTime before, DateTime after) {
        if (before.isAfter(after)) {
            throw new InvalidParameterException("Dont invert the order of duration DateTime params");
        }
        return (int) (new Duration(before, after).getStandardMinutes());
    }

    private void addGigs(DaySchedule daySchedule) {
        DateTime timelineStartMoment = getTimelineStartMoment(daySchedule);
        Map<String, List<Event>> eventsByLocation = daySchedule.getEventsByLocation();

        ViewGroup gigLayout = (ViewGroup) getView().findViewById(R.id.gigLayout);
        TextView textView = new TextView(this.getActivity());
        textView.setText("");
        textView.setHeight(ROW_HEIGHT);
        textView.setPadding(1, 10, 1, 1);
        gigLayout.addView(textView);

        List<String> locations = new ArrayList<String>(eventsByLocation.keySet());

        for (String location : locations) {
            LinearLayout stageRow = new LinearLayout(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
//            params.setMargins(0, 2, 0, 2);
            stageRow.setLayoutParams(params);
            stageRow.setOrientation(LinearLayout.HORIZONTAL);

            DateTime previousTime = timelineStartMoment;
            for (Event event : eventsByLocation.get(location)) {
                DateTime eventStartTime = new DateTime(event.start_time);
                DateTime eventEndTime = new DateTime(event.end_time);
                if (previousTime.isBefore(eventStartTime)) {
                    int duration = getDurationInMinutes(previousTime, eventStartTime);
                    int margin = EventTimelineView.PIXELS_PER_MINUTE * duration;
                    TextView tv = new TextView(getActivity());
                    tv.setHeight(ROW_HEIGHT);
                    tv.setWidth(margin);
                    stageRow.addView(tv);
                }

                EventTimelineView gigWidget = new EventTimelineView(
                    getActivity(),
                    null,
                    event,
                    previousTime.toDate()
                );
                stageRow.addView(gigWidget);
                if (eventEndTime.equals(daySchedule.getLatestTime())) {
                    int margin = EventTimelineView.PIXELS_PER_MINUTE * TIMELINE_END_OFFSET;
                    TextView tv = new TextView(getActivity());
                    tv.setHeight(ROW_HEIGHT);
                    tv.setWidth(margin);
                    stageRow.addView(tv);
                }

                //gigWidget.setOnClickListener(gigWidgetClickListener);
                previousTime = eventEndTime;
            }
            gigLayout.addView(stageRow);
        }
    }

    class GuitarSwipeListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float distanceX = Math.abs(e1.getX() - e2.getX());
                float distanceY = Math.abs(e1.getY() - e2.getY());
                if (distanceX == 0 || distanceY / distanceX > 5) {
                    if (distanceY > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                    {
                        boolean upwardMotion = e1.getY() - e2.getY() > 0;
                        HashMap<String, String> swipeMap = new HashMap<String, String>();
                        swipeMap.put("direction", upwardMotion ? "up" : "down");
                    }
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }

    private void subscribeTextView(Observable<String> observable, final TextView textView) {
        compositeSubscription.add(SubscriptionUtils.subscribeTextViewText(observable, textView));
    }

    @Override
    public void onPause() {
        super.onPause();
        compositeSubscription.clear();
    }
}
