package de.serviceexperiencecamp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.serviceexperiencecamp.android.MainActivity;
import de.serviceexperiencecamp.android.R;
import de.serviceexperiencecamp.android.utils.SubscriptionUtils;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class MenuFragment extends Fragment {

    final private CompositeSubscription compositeSubscription = new CompositeSubscription();
//    private Observable<Event> firstEvent$ = Observable.empty(); // instead of null as default

    private View agendaView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        agendaView = view.findViewById(R.id.agenda);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        agendaView.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            MainActivity activity = (MainActivity) getActivity();
            activity.fragment$.onNext(activity.scheduleFragment);
        }});
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
