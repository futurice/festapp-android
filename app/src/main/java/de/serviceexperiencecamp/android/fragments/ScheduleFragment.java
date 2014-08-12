package de.serviceexperiencecamp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.serviceexperiencecamp.android.R;
import de.serviceexperiencecamp.android.models.EventsModel;
import de.serviceexperiencecamp.android.models.pojo.Event;
import de.serviceexperiencecamp.android.utils.SubscriptionUtils;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class ScheduleFragment extends Fragment {
    final private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private EventsModel eventsModel;
    private Observable<Event> firstEvent$ = Observable.empty(); // instead of null as default

    private TextView bookNameTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsModel = EventsModel.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookNameTextView = (TextView) getView().findViewById(R.id.title);
        firstEvent$ = getFirstEvent$();
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribeTextView(getEventTitle(), bookNameTextView);
    }

    private Observable<Event> getFirstEvent$() {
        return eventsModel.getEvents$()
            .map(new Func1<List<Event>, Event>() { @Override public Event call(List<Event> events) {
                return events.get(0);
            }});
    }

    private Observable<String> getEventTitle() {
        return firstEvent$
            .map(new Func1<Event, String>() { @Override public String call(Event event) {
                return event.title;
            }})
            .startWith("Loading...");
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
