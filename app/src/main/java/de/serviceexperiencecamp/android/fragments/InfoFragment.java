package de.serviceexperiencecamp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.serviceexperiencecamp.android.R;

public class InfoFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final Bundle bundle = getArguments();
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // Find the views
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView contentView = (TextView) view.findViewById(R.id.content);

        // Set the bundle arguments as the content for the views
        titleView.setText(processString(bundle.getString("title")));
        contentView.setText(processString(bundle.getString("content")));

        return view;
    }

    private String processString(String s) {
        if (s == null) { return ""; }
        return s.replaceAll("\\\\n", "\n");
    }
}
