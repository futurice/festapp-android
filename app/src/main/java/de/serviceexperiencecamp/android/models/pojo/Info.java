package de.serviceexperiencecamp.android.models.pojo;

import android.os.Bundle;
public class Info {
    public String _id;
    public String title;
    public String image;
    public String content;
    public String place;

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("_id", _id);
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("image", image);
        bundle.putString("place", place);
        return bundle;
    }
}
