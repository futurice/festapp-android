package de.serviceexperiencecamp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import de.serviceexperiencecamp.android.R;

public class EventFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        // Find the views
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subheaderView = (TextView) view.findViewById(R.id.subheader);
        TextView timeView = (TextView) view.findViewById(R.id.time);
        TextView dayView = (TextView) view.findViewById(R.id.day);
        TextView locationView = (TextView) view.findViewById(R.id.location);
        TextView descriptionView = (TextView) view.findViewById(R.id.description);

        // Set the bundle arguments as the content for the views
        Bundle bundle = getArguments();
        titleView.setText(bundle.getString("title"));
        subheaderView.setText(makeSubheaderString(
            bundle.getString("artists"), bundle.getString("subheader"))
        );
        timeView.setText(makeTimeString(
            bundle.getString("start_time"), bundle.getString("end_time"))
        );
        dayView.setText(bundle.getString("day"));
        locationView.setText(bundle.getString("location"));
        descriptionView.setText(bundle.getString("description"));

        return view;
    }

    private String makeSubheaderString(final String speaker, final String subheaderContent) {
        return speaker + " \u2014 " + subheaderContent;
    }

    private String makeTimeString(final String startInput, final String endInput) {
        DateTime startDateTime = new DateTime(startInput);
        DateTime endDateTime = new DateTime(endInput);
        String startOutput = startDateTime.toString("HH:mm");
        String endOutput = endDateTime.toString("HH:mm");
        return startOutput + "\u2014" + endOutput;
    }
}
