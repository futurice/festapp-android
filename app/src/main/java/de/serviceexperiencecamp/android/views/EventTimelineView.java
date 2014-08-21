package de.serviceexperiencecamp.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Date;

import de.serviceexperiencecamp.android.R;
import de.serviceexperiencecamp.android.models.pojo.Event;

public class EventTimelineView extends RelativeLayout {

    public static final int MINUTE_WIDTH = 2; // dp

    public EventTimelineView(Context context) {
        super(context, null);
    }

    public EventTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventTimelineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EventTimelineView(Context context, AttributeSet attrs, Event event, Date previousTime) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.event_timeline_box, this, true);
        TextView eventTitle = (TextView) findViewById(R.id.artistName);
        eventTitle.setText(event.title);
        View star = findViewById(R.id.star);

        if (event.bar_camp) {
            this.setBackgroundColor(getResources().getColor(R.color.orange));
        }
        else {
            this.setBackgroundColor(getResources().getColor(R.color.pink));
        }

        if (Event.getIsFavoriteFromPreferences(getContext(), event._id)) {
            star.setVisibility(View.VISIBLE);
        }
        else {
            star.setVisibility(View.INVISIBLE);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.width = dpToPx(MINUTE_WIDTH) * getDuration(event);
        setLayoutParams(params);
    }


    private int dpToPx(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float displayDensity = metrics.density;
        return (int) (dp * displayDensity);
    }

    private int pxToDp(int px) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float displayDensity = metrics.density;
        return (int) (px / displayDensity);
    }

    /**
     * @return duration in minutes
     */
    public static int getDuration(Event event) {
        DateTime startTime = new DateTime(event.start_time);
        DateTime endTime = new DateTime(event.end_time);
        Duration duration = new Duration(startTime, endTime);
        return (int) duration.getStandardMinutes();
    }

}
