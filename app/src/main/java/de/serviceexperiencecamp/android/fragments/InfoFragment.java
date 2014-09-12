package de.serviceexperiencecamp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
        WebView contentView = (WebView) view.findViewById(R.id.content);

        // Set the bundle arguments as the content for the views
        titleView.setText(processString(bundle.getString("title")));

        String html = "<html><head>"
            + "<meta http-equiv='Content-Type' content='text/html' charset='UTF-8' />"
            + "<style type=\"text/css\">"
            + "body {color: #FFFFFF; background-color: #000000;}"
            + "p {margin: 0; padding: 0;}"
            + "a {color: #f08e0c}"
            + "</style>"
            + "</head>"
            + "<body>"
            + bundle.getString("content")
            + "</body></html>";
        contentView.loadData(html, "text/html; charset=UTF-8", "UTF-8");
        return view;
    }

    private String processString(String s) {
        if (s == null) { return ""; }
        return s.replaceAll("\\\\n", "\n");
    }
}
