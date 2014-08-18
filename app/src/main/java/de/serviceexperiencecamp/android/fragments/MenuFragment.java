package de.serviceexperiencecamp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.serviceexperiencecamp.android.MainActivity;
import de.serviceexperiencecamp.android.R;

public class MenuFragment extends Fragment {

    private View agendaView;
    private View keyTalksView;

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
        keyTalksView = view.findViewById(R.id.keytalks);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final MainActivity activity = (MainActivity) getActivity();
        agendaView.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            activity.fragment$.onNext(activity.scheduleFragment);
        }});
        keyTalksView.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            activity.fragment$.onNext(activity.eventListFragment);
        }});
    }

    @Override
    public void onPause() {
        super.onPause();
        agendaView.setOnClickListener(null);
        keyTalksView.setOnClickListener(null);
    }
}
