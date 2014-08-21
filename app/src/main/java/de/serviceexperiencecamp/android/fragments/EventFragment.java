package de.serviceexperiencecamp.android.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        View linkedin = view.findViewById(R.id.linkedin);
        View twitter = view.findViewById(R.id.twitter);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subheaderView = (TextView) view.findViewById(R.id.subheader);
        TextView timeView = (TextView) view.findViewById(R.id.time);
        TextView dayView = (TextView) view.findViewById(R.id.day);
        TextView locationView = (TextView) view.findViewById(R.id.location);
        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);

        // Set the bundle arguments as the content for the views
        final Bundle bundle = getArguments();
        titleView.setText(bundle.getString("title"));
        subheaderView.setText(makeSubheaderString(
            bundle.getString("artists"), bundle.getString("speaker_role"))
        );
        timeView.setText(makeTimeString(
            bundle.getString("start_time"), bundle.getString("end_time"))
        );
        dayView.setText(bundle.getString("day"));
        locationView.setText(bundle.getString("location"));
        descriptionView.setText(bundle.getString("description"));
        if (!isNullOrEmpty(bundle.getString("image_url"))) {
            Picasso.with(getActivity())
                .load(bundle.getString("image_url"))
                .error(R.drawable.event_placeholder)
                .into(imageView);
        }
        else {
            Picasso.with(getActivity())
                .load(R.drawable.event_placeholder)
                .into(imageView);
        }

        final String linkedin_url = bundle.getString("linkedin_url");
        if (!isNullOrEmpty(linkedin_url)) {
            linkedin.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedin_url));
                startActivity(browserIntent);
            }});
        }
        else {
            linkedin.setVisibility(View.GONE);
        }

        final String twitter_handle = bundle.getString("twitter_handle");
        if (!isNullOrEmpty(twitter_handle)) {
            twitter.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
                String twitter_url = "https://twitter.com/" + twitter_handle;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_url));
                startActivity(browserIntent);
            }});
        }
        else {
            twitter.setVisibility(View.GONE);
        }

        return view;
    }



    private static boolean isNullOrEmpty(final String input) {
        return (input == null || input.length() <= 0);
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
