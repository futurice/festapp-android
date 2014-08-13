package de.serviceexperiencecamp.android.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Date;
import java.util.HashMap;

import de.serviceexperiencecamp.android.R;
import de.serviceexperiencecamp.android.models.pojo.Event;

public class EventTimelineView extends RelativeLayout {

    public static final int PIXELS_PER_MINUTE = 5;
    private Event event;
//    private ToggleButton starIcon;
    private TextView artistLabel;
    private CompoundButton.OnCheckedChangeListener favoriteListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            GigDAO.setFavorite(getContext(), gig.getId(), isChecked);
//            setFavorite(isChecked);
        }
    };

    public EventTimelineView(Context context, AttributeSet attrs, Event event, Date previousTime) {
        super(context, attrs);
        this.event = event;
        LayoutInflater.from(context).inflate(R.layout.event_timeline_box, this, true);
        artistLabel = (TextView) findViewById(R.id.artistName);
        artistLabel.setTextColor(Color.parseColor("#FFFFFF"));
        artistLabel.setText(event.artists);

        this.setBackgroundColor(Color.parseColor("#000000"));
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

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int width = PIXELS_PER_MINUTE * getDuration(event);
        params.width = width;
        int height = (int) getResources().getDimension(R.dimen.timeline_event_height);
        params.height = height;
        setLayoutParams(params);
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
//            artistLabel.setTextColor(Color.parseColor("#a95800"));
//        } else {
//            setBackgroundResource(R.drawable.schedule_gig);
//            artistLabel.setTextColor(Color.parseColor("#fbf6dd"));
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
