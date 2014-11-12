package com.futurice.festapp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.futurice.festapp.android.MainActivity;
import com.futurice.festapp.android.R;
import com.futurice.festapp.android.models.DaySchedule;
import com.futurice.festapp.android.models.EventsModel;
import com.futurice.festapp.android.models.pojo.Gig;
import com.futurice.festapp.android.utils.DateUtils;
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

    private Observable<DaySchedule> getDaySchedule$(final String day, Observable<List<Gig>> events$) {
        return events$
            .map(new Func1<List<Gig>, DaySchedule>() { @Override public DaySchedule call(List<Gig> gigs) {
                return new DaySchedule(day, gigs);
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
            List<Gig> listGigs = daySchedule.getEvents();
            DateUtils.sortEventsByStartTime(listGigs);
            boolean firstWasRendered = false;
            for (final Gig gig : listGigs) {
                if (firstWasRendered) {
                    dayList.addView(makeHorizontalLine(dayList));
                }
                dayList.addView(makeEventListItem(gig));
                firstWasRendered = true;
            }
        }
    }

    private View makeHorizontalLine(final ViewGroup container) {
        return LayoutInflater.from(getActivity()).inflate(
            R.layout.view_horizontal_line,
            container,
            false
        );
    }

    private View makeEventListItem(final Gig gig) {
        View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.view_event_list_item, null, false);
        TextView primaryText = (TextView) view.findViewById(R.id.primary);
        TextView secondaryText = (TextView) view.findViewById(R.id.secondary);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        View star = view.findViewById(R.id.star);

        primaryText.setText(gig.name);
        secondaryText.setText(makeSecondaryString(gig));

        view.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            MainActivity activity = (MainActivity) getActivity();
            EventFragment fragment = new EventFragment();
            //fragment.setArguments(gig.getBundle());
            activity.fragment$.onNext(fragment);
        }});

        return view;
    }

    private static String makeSecondaryString(Gig gig) {
        String secondaryString = "";
        if (gig.artist != null && gig.artist.name != null) {
            secondaryString += gig.artist;
        }
        return secondaryString;
    }

    @Override
    public void onPause() {
        super.onPause();
        compositeSubscription.clear();
    }
}
