package de.serviceexperiencecamp.android.models;

import de.serviceexperiencecamp.android.models.pojo.Event;
import de.serviceexperiencecamp.android.network.SecApi;

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
        return SecApi.getInstance().getAllEvents();
    }

}
