package com.futurice.festapp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.futurice.festapp.android.MainActivity;
import com.futurice.festapp.android.R;
import com.futurice.festapp.android.models.DaySchedule;
import com.futurice.festapp.android.models.EventsModel;
import com.futurice.festapp.android.models.pojo.Gig;
import com.futurice.festapp.android.utils.DateUtils;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.futurice.festapp.android.views.EventTimelineView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class ScheduleFragment extends Fragment {

    final private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private EventsModel eventsModel;

    private static final int TIMELINE_END_OFFSET = 30; // minutes
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private int hourMarkerWidthPx = 1;
    private HorizontalScrollView horizontalScrollView;
    private View saturdayButton;
    private View sundayButton;
    private BehaviorSubject<String> selectedDay = BehaviorSubject.create("Saturday");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsModel = EventsModel.getInstance();
        hourMarkerWidthPx = getResources().getDimensionPixelSize(R.dimen.timeline_hour_marker_width);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.timelineScrollView);
        saturdayButton = view.findViewById(R.id.saturday_button);
        sundayButton = view.findViewById(R.id.sunday_button);

        // Gestures
        gestureDetector = new GestureDetector(getActivity(), new GuitarSwipeListener());
        gestureListener = new View.OnTouchListener() { public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }};
        horizontalScrollView.setOnTouchListener(gestureListener);

        saturdayButton.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            selectedDay.onNext("Saturday");
        }});

        sundayButton.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            selectedDay.onNext("Sunday");
        }});

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeSubscription.add(selectedDay
            .flatMap(new Func1<String, Observable<DaySchedule>>() { @Override public Observable<DaySchedule> call(String day) {
                return getDaySchedule$(eventsModel.getEvents$(), day);
            }})
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<DaySchedule>() { @Override public void call(DaySchedule daySchedule) {
                addTimeline(daySchedule);
                addGigs(daySchedule);
                addStages(daySchedule);
                hideLoading();
            }})
        );

        compositeSubscription.add(selectedDay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<String>() { @Override public void call(String day) {
                if ("Saturday".equals(day)) {
                    saturdayButton.setSelected(true);
                    sundayButton.setSelected(false);
                } else if ("Sunday".equals(day)) {
                    saturdayButton.setSelected(false);
                    sundayButton.setSelected(true);
                }
            }})
        );
    }

//    @Deprecated
//    private void flingScrollView() {
//        compositeSubscription.add(
//            Observable.timer(50, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Long>() { @Override public void call(Long aLong) {
//                    if (horizontalScrollView != null) {
//                        horizontalScrollView.pageScroll(View.FOCUS_RIGHT);
//                    }
//                }})
//        );
//    }

    private Observable<DaySchedule> getDaySchedule$(
        Observable<List<Gig>> events$,
        final String day)
    {
        return events$.map(new Func1<List<Gig>, DaySchedule>() {
            @Override
            public DaySchedule call(List<Gig> gigs) {
                return new DaySchedule(day, gigs);
            }
        });
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
        DateTime now = daySchedule.getEarliestTime().plusMinutes(20);  // DateTime.now();
        View nowLine = getView().findViewById(R.id.timelineNowLine);
        nowLine.bringToFront();
        if (now.isAfter(timelineStartMoment) && now.isBefore(timelineEndMoment)) {
            nowLine.setVisibility(View.VISIBLE);
            TextView marginView = (TextView) getView().findViewById(R.id.timelineNowMargin);
            int duration = getDurationInMinutes(timelineStartMoment, now);
            int leftMargin = duration * dpToPx(EventTimelineView.MINUTE_WIDTH) - hourMarkerWidthPx/2 - 3;
            //initialScrollTo = leftMargin - getWindowManager().getDefaultDisplay().getWidth()/2;
            marginView.setWidth(leftMargin);
        } else {
            nowLine.setVisibility(View.GONE);
        }

        nowLine.setVisibility(View.INVISIBLE); // HIDE THIS FEATURE FOR NOW
    }

    private void hideLoading() {
        View loadingView = getView().findViewById(R.id.loading);
        loadingView.setVisibility(View.GONE);
    }

    private void addStages(DaySchedule daySchedule) {
        LinearLayout stagesLayout = (LinearLayout) getView().findViewById(R.id.stageLayout);
        stagesLayout.removeAllViews();
        for (String stageName : DaySchedule.ALL_ROOMS) {
            LayoutInflater layoutInflater = this.getActivity().getLayoutInflater();
            TextView stage = (TextView) layoutInflater.inflate(R.layout.view_stage_label, stagesLayout, false);
            stage.setText(stageName);
            stagesLayout.addView(stage);
        }
    }

    private void addTimeline(DaySchedule daySchedule) {
        DateTime timelineStartMoment = getTimelineStartMoment(daySchedule);
        DateTime timelineEndMoment = getTimelineEndMoment(daySchedule);

        LinearLayout numbersLayout = (LinearLayout) getView().findViewById(R.id.timelineNumbers);
        LinearLayout timelineVerticalLines = (LinearLayout) getView().findViewById(R.id.timelineVerticalLines);
        numbersLayout.removeAllViews();
        timelineVerticalLines.removeAllViews();

        DateTime cursor = timelineStartMoment;

        // Left margin on the earliest time number layout
        int minutes = 60 - cursor.getMinuteOfHour();
        TextView tv = new TextView(getActivity());
        tv.setWidth(dpToPx(EventTimelineView.MINUTE_WIDTH) * minutes);
        tv.setHeight(getResources().getDimensionPixelSize(R.dimen.spacing_large));
        numbersLayout.addView(tv);

        // Left margin on the earliest time vertical line
        tv = new TextView(getActivity());
        tv.setWidth(dpToPx(EventTimelineView.MINUTE_WIDTH) * minutes);
        timelineVerticalLines.addView(tv);
        cursor = cursor.plusMinutes(minutes);

        while (cursor.isBefore(timelineEndMoment)) {
            minutes = getDurationInMinutes(cursor, timelineEndMoment);
            minutes = (minutes < 60) ? minutes : 60;

            // Add the hour text
            tv = new TextView(this.getActivity());
            tv.setTextColor(getResources().getColor(R.color.orange));
            String hour = "" + cursor.getHourOfDay() + ":00";
            if (hour.length() == 4) {
                hour = "0" + hour;
            }
            tv.setText(hour);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setGravity(Gravity.LEFT|Gravity.BOTTOM);
            tv.setWidth(dpToPx(EventTimelineView.MINUTE_WIDTH) * minutes);
            tv.setHeight(getResources().getDimensionPixelSize(R.dimen.spacing_large));
            numbersLayout.addView(tv);

            // Add vertical line
            this.getActivity().getLayoutInflater().inflate(R.layout.timeline_hour_marker, timelineVerticalLines);

            // Add right margin for the vertical line
            tv = new TextView(this.getActivity());
            tv.setWidth(dpToPx(EventTimelineView.MINUTE_WIDTH) * minutes - hourMarkerWidthPx);
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
        String[] locations = DaySchedule.ALL_ROOMS;
        Map<String, List<Gig>> eventsByLocation = daySchedule.getEventsByLocation();
        ViewGroup gigLayout = (ViewGroup) getView().findViewById(R.id.gigLayout);
        gigLayout.removeAllViews();

        gigLayout.addView(makeSubtleHorizontalLine(gigLayout));
        for (String location : locations) {
            LinearLayout locationRow = new LinearLayout(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.height = (getResources().getDimensionPixelSize(R.dimen.touchable_ui_height));
            locationRow.setLayoutParams(params);
            locationRow.setOrientation(LinearLayout.HORIZONTAL);

            DateTime previousTime = getTimelineStartMoment(daySchedule);

            // Render each event
            for (final Gig gig : getSortedEventsOfLocation(eventsByLocation, location)) {
                DateTime eventStartTime = new DateTime(gig.startTime);
                DateTime eventEndTime = new DateTime(gig.endTime);

                // Make left margin
                if (previousTime.isBefore(eventStartTime)) {
                    int duration = getDurationInMinutes(previousTime, eventStartTime);
                    int margin = dpToPx(EventTimelineView.MINUTE_WIDTH) * duration;
                    TextView tv = new TextView(getActivity());
                    tv.setWidth(margin);
                    tv.setHeight(getResources().getDimensionPixelSize(R.dimen.touchable_ui_height));
                    locationRow.addView(tv);
                }

                // Make event view
                locationRow.addView(makeEventView(gig, previousTime));

                // Make right margin
                if (eventEndTime.equals(daySchedule.getLatestTime())) {
                    int margin = dpToPx(EventTimelineView.MINUTE_WIDTH) * TIMELINE_END_OFFSET;
                    TextView tv = new TextView(getActivity());
                    tv.setWidth(margin);
                    tv.setHeight(getResources().getDimensionPixelSize(R.dimen.touchable_ui_height));
                    locationRow.addView(tv);
                }

                previousTime = eventEndTime;
            }

            gigLayout.addView(locationRow);
            gigLayout.addView(makeSubtleHorizontalLine(gigLayout));
        }
    }

    private View makeSubtleHorizontalLine(ViewGroup container) {
        return LayoutInflater.from(getActivity()).inflate(
            R.layout.view_subtle_line,
            container,
            false
        );
    }

    private View makeDottedLine(ViewGroup container) {
        return LayoutInflater.from(getActivity()).inflate(
            R.layout.view_dotted_line,
            container,
            false
        );
    }

    private EventTimelineView makeEventView(final Gig gig, final DateTime previousTime) {
        // Make event view
        EventTimelineView eventView = new EventTimelineView(
            getActivity(),
            null,
            gig,
            previousTime.toDate()
        );

        // When event clicked, tell activity to open EventFragment
        eventView.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            MainActivity activity = (MainActivity) getActivity();
            EventFragment fragment = new EventFragment();
            //fragment.setArguments(gig.getBundle());
            activity.fragment$.onNext(fragment);
        }});

        return eventView;
    }

    private static List<Gig> getSortedEventsOfLocation(
        Map<String, List<Gig>> eventsByLocation,
        String location)
    {
        List<Gig> listGigs = eventsByLocation.get(location);
        DateUtils.sortEventsByStartTime(listGigs);
        return listGigs;
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

    private int dpToPx(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float displayDensity = metrics.density;
        return (int) (dp * displayDensity);
    }

    private int pxToDp(int px) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float displayDensity = metrics.density;
        return (int) (px / displayDensity);
    }

    @Override
    public void onPause() {
        super.onPause();
        compositeSubscription.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (saturdayButton != null) {
            saturdayButton.setOnClickListener(null);
        }
        if (sundayButton != null) {
            sundayButton.setOnClickListener(null);
        }
    }
}
