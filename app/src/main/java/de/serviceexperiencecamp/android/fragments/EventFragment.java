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
import de.serviceexperiencecamp.android.models.pojo.Event;

public class EventFragment extends Fragment {

    private boolean isFavorite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final Bundle bundle = getArguments();
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        // Find the views
        View linkedin = view.findViewById(R.id.linkedin);
        View twitter = view.findViewById(R.id.twitter);
        final TextView favoriteButton = (TextView) view.findViewById(R.id.favorite_button);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView speakerView = (TextView) view.findViewById(R.id.speaker);
        TextView speakerRoleView = (TextView) view.findViewById(R.id.speaker_role);
        TextView timeView = (TextView) view.findViewById(R.id.time);
        TextView dayView = (TextView) view.findViewById(R.id.day);
        TextView locationView = (TextView) view.findViewById(R.id.location);
        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);

        // Set the bundle arguments as the content for the views
        titleView.setText(bundle.getString("title"));
        speakerView.setText(prepareString(bundle.getString("artists")));
        speakerRoleView.setText(prepareString(bundle.getString("speaker_role")));
        timeView.setText(makeTimeString(
                bundle.getString("start_time"), bundle.getString("end_time"))
        );
        dayView.setText(bundle.getString("day"));
        locationView.setText(bundle.getString("location"));
        descriptionView.setText(processDescriptionString(bundle.getString("description")));
        setImageViewContent(imageView, bundle.getString("image_url"));
        setLinkedInContent(linkedin, bundle.getString("linkedin_url"));
        setTwitterContent(twitter, bundle.getString("twitter_handle"));

        final String _id = bundle.getString("_id");
        isFavorite = Event.getIsFavoriteFromPreferences(getActivity(), _id);
        setFavoriteButtonStatus(favoriteButton, isFavorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            isFavorite = !isFavorite;
            setFavoriteButtonStatus(favoriteButton, isFavorite);
            Event.setIsFavoriteFromPreferences(getActivity(), _id, isFavorite);
        }});

        return view;
    }

    private void setFavoriteButtonStatus(TextView favoriteButton, boolean isFavorite) {
        favoriteButton.setSelected(isFavorite);
        if (isFavorite) {
            favoriteButton.setText(getResources().getString(R.string.unfavorite));
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_black,0,0,0);
        }
        else {
            favoriteButton.setText(getResources().getString(R.string.save_as_favorite));
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
        }
    }

    private void setImageViewContent(ImageView imageView, String image_url) {
        if (!isNullOrEmpty(image_url)) {
            Picasso.with(getActivity())
                .load(image_url)
                .error(R.drawable.event_placeholder)
                .into(imageView);
        }
        else {
            Picasso.with(getActivity())
                .load(R.drawable.event_placeholder)
                .into(imageView);
        }
    }

    private void setLinkedInContent(View view, final String linkedin_url) {
        if (!isNullOrEmpty(linkedin_url)) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedin_url));
                    startActivity(browserIntent);
                }
            });
        }
        else {
            view.setVisibility(View.GONE);
        }
    }

    private void setTwitterContent(View view, final String twitter_handle) {
        if (!isNullOrEmpty(twitter_handle)) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String twitter_url = "https://twitter.com/" + twitter_handle;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_url));
                    startActivity(browserIntent);
                }
            });
        }
        else {
            view.setVisibility(View.GONE);
        }
    }

    private static boolean isNullOrEmpty(final String input) {
        return (input == null || input.length() <= 0);
    }

    private static String prepareString(final String input) {
        if (input == null) {
            return "";
        }
        else {
            return input;
        }
    }

    private String processDescriptionString(String s) {
        if (s == null) { return ""; }
        return s.replaceAll("\\\\n", "\n");
    }

    private String makeTimeString(final String startInput, final String endInput) {
        DateTime startDateTime = new DateTime(startInput);
        DateTime endDateTime = new DateTime(endInput);
        String startOutput = startDateTime.toString("HH:mm");
        String endOutput = endDateTime.toString("HH:mm");
        return startOutput + "\u2014" + endOutput;
    }
}
