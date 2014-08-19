package de.serviceexperiencecamp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import de.serviceexperiencecamp.android.MainActivity;
import de.serviceexperiencecamp.android.R;
import de.serviceexperiencecamp.android.models.DaySchedule;
import de.serviceexperiencecamp.android.models.EventsModel;
import de.serviceexperiencecamp.android.models.pojo.Event;
import de.serviceexperiencecamp.android.utils.DateUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class EventListFragment extends Fragment {

    final private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private EventsModel eventsModel;
    private LinearLayout saturdayList;
    private LinearLayout sundayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsModel = EventsModel.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        saturdayList = (LinearLayout) view.findViewById(R.id.saturday_list);
        sundayList = (LinearLayout) view.findViewById(R.id.sunday_list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeSubscription.add(getDaySchedule$("Saturday", eventsModel.getEvents$())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new FillEventListAction())
        );
        compositeSubscription.add(getDaySchedule$("Sunday", eventsModel.getEvents$())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new FillEventListAction())
        );
    }

    private Observable<DaySchedule> getDaySchedule$(final String day, Observable<List<Event>> events$) {
        return events$
            .map(new Func1<List<Event>, DaySchedule>() { @Override public DaySchedule call(List<Event> events) {
                return new DaySchedule(day, events);
            }});
    }

    private class FillEventListAction implements Action1<DaySchedule> {
        @Override
        public void call(DaySchedule daySchedule) {
            LinearLayout dayList;
            if ("Saturday".equals(daySchedule.getConferenceDay())) {
                dayList = saturdayList;
            }
            else if ("Sunday".equals(daySchedule.getConferenceDay())) {
                dayList = sundayList;
            }
            else {
                return;
            }

            dayList.removeAllViews();
            List<Event> listEvents = daySchedule.getEvents();
            DateUtils.sortEventsByStartTime(listEvents);
            for (final Event event : listEvents) {
                if (event.bar_camp == false) {
                    dayList.addView(makeEventListItem(event));
                }
            }
        }
    }

    private View makeEventListItem(final Event event) {
        View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.view_event_list_item, null, false);
        TextView primaryText = (TextView) view.findViewById(R.id.primary);
        TextView secondaryText = (TextView) view.findViewById(R.id.secondary);

        String secondaryString = "";
        if (event.start_time != null && event.start_time.length() > 0) {
            secondaryString += (new DateTime(event.start_time)).toString("HH:mm");
        }
        if (event.speaker_role != null && event.speaker_role.length() > 0) {
            if (secondaryString.length() > 0) {
                secondaryString += " \u2014 ";
            }
            secondaryString += event.speaker_role;
        }

        primaryText.setText(event.title);
        secondaryText.setText(secondaryString);

        view.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            MainActivity activity = (MainActivity) getActivity();
            EventFragment fragment = new EventFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", event.title);
            bundle.putString("artists", event.artists);
            bundle.putString("start_time", event.start_time);
            bundle.putString("end_time", event.end_time);
            bundle.putString("image_url", event.image_url);
            bundle.putString("day", event.day);
            bundle.putString("location", event.location);
            bundle.putString("speaker_role", event.speaker_role);
            bundle.putString("description", event.description);
            fragment.setArguments(bundle);
            activity.fragment$.onNext(fragment);
        }});

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        compositeSubscription.clear();
    }
}
