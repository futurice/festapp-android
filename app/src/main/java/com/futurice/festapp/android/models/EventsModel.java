package com.futurice.festapp.android.models;

import com.futurice.festapp.android.models.pojo.Event;
import com.futurice.festapp.android.network.FestAppApi;

import java.util.List;

import rx.Observable;

public class EventsModel {
    static private EventsModel instance;

    static public EventsModel getInstance() {
        if (instance == null) {
            instance = new EventsModel();
        }
        return instance;
    }

    private EventsModel() { }

    public Observable<List<Event>> getEvents$() {
        return FestAppApi.getInstance().getAllEvents();
    }

}
