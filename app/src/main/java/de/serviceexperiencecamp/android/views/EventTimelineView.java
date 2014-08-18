package de.serviceexperiencecamp.android.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Date;

import de.serviceexperiencecamp.android.R;
import de.serviceexperiencecamp.android.models.pojo.Event;

public class EventTimelineView extends RelativeLayout {

    public static final int MINUTE_WIDTH = 2; // dp
    private Event event;
    private TextView eventTitle;

//    private ToggleButton starIcon;
//    private CompoundButton.OnCheckedChangeListener favoriteListener = new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            GigDAO.setFavorite(getContext(), gig.getId(), isChecked);
//            setFavorite(isChecked);
//        }
//    };

    public EventTimelineView(Context context, AttributeSet attrs, Event event, Date previousTime) {
        super(context, attrs);
        this.event = event;
        LayoutInflater.from(context).inflate(R.layout.event_timeline_box, this, true);
        eventTitle = (TextView) findViewById(R.id.artistName);
        eventTitle.setText(event.title);

        if (event.bar_camp) {
            this.setBackgroundColor(getResources().getColor(R.color.orange));
        }
        else {
            this.setBackground(getResources().getDrawable(R.drawable.gradient_horiz));
        }

//        starIcon = (ToggleButton) findViewById(R.id.starIcon);
//        starIcon.setChecked(false/*gig.isFavorite()*/);
//        starIcon.setOnCheckedChangeListener(favoriteListener);
//        starIcon.setVisibility(View.GONE);
        /*setFavorite(gig.isFavorite());*/
//        if (getDuration(event) < 20) {
//            starIcon.setVisibility(View.GONE);
//        } else {
//            starIcon.setVisibility(View.VISIBLE);
//        }

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

//    public void setFavorite(boolean fav) {
//        gig.setFavorite(fav);
//        starIcon.setChecked(fav);
//        if (fav) {
//            setBackgroundResource(R.drawable.schedule_gig_favorite);
//            eventTitle.setTextColor(Color.parseColor("#a95800"));
//        } else {
//            setBackgroundResource(R.drawable.schedule_gig);
//            eventTitle.setTextColor(Color.parseColor("#fbf6dd"));
//        }
//        HashMap<String, String> artistMap = new HashMap<String, String>();
//        artistMap.put("artist", gig.getArtist());
//        artistMap.put("favourite", fav ? "true" : "false");
//        artistMap.put("view", "timeline");
//    }

//    public Gig getGig() {
//        return gig;
//    }

}
