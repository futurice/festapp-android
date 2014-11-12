package com.futurice.festapp.android.views;

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

import com.futurice.festapp.android.R;
import com.futurice.festapp.android.models.pojo.Gig;

public class EventTimelineView extends RelativeLayout {

    public static final int MINUTE_WIDTH = 3; // dp

    public EventTimelineView(Context context) {
        super(context, null);
    }

    public EventTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventTimelineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EventTimelineView(Context context, AttributeSet attrs, Gig gig, Date previousTime) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.event_timeline_box, this, true);
        TextView eventTitle = (TextView) findViewById(R.id.artistName);
        eventTitle.setText(gig.name);
        View star = findViewById(R.id.star);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.width = dpToPx(MINUTE_WIDTH) * getDuration(gig);
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
    public static int getDuration(Gig gig) {
        DateTime startTime = new DateTime(gig.startTime);
        DateTime endTime = new DateTime(gig.endTime);
        Duration duration = new Duration(startTime, endTime);
        return (int) duration.getStandardMinutes();
    }

}
