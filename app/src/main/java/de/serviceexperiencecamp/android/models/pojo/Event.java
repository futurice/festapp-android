package de.serviceexperiencecamp.android.models.pojo;

import android.os.Bundle;

import java.util.List;

public class Event {
    public String title;
    public String start_time;
    public String end_time;
    public String location;
    public String description;
    public String speaker_role;
    public String twitter_handle;
    public String linkedin_url;
    public String image_url;
    public String speaker_image_url;
    public Boolean bar_camp;
    public String day;
    public String artists;
    public Integer starred_count;

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("artists", artists);
        bundle.putString("start_time", start_time);
        bundle.putString("end_time", end_time);
        bundle.putString("image_url", image_url);
        bundle.putString("day", day);
        bundle.putString("location", location);
        bundle.putString("speaker_role", speaker_role);
        bundle.putString("linkedin_url", linkedin_url);
        bundle.putString("twitter_handle", twitter_handle);
        bundle.putString("description", description);
        return bundle;
    }
}
