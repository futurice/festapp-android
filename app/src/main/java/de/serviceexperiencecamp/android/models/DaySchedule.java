package de.serviceexperiencecamp.android.models;

import android.util.Log;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;

import de.serviceexperiencecamp.android.models.pojo.Event;

public class DaySchedule {

    public static final String[] ALL_ROOMS = {"Galerie", "Raum 2", "Raum 3", "Raum 4", "Raum 5", "Atelier", "Loft"};
    private String conferenceDay;
    private Map<String, List<Event>> eventsByLocation = new TreeMap<String, List<Event>>();
    private DateTime earliestTime;
    private DateTime latestTime;

    public DaySchedule(String conferenceDay, List<Event> events) {
        this.conferenceDay = conferenceDay;
        this.eventsByLocation = new HashMap<String, List<Event>>();

        // Initialize eventsByLocation with all the rooms, ordered correctly
        for (String location : ALL_ROOMS) {
            this.eventsByLocation.put(location, new ArrayList<Event>());
        }

        // Organize given list of events into eventsByLocation
        for (Event ev : events) {
            if (!ev.day.equals(conferenceDay)) continue;

            if (this.eventsByLocation.get(ev.location) == null) {
                Log.e("DaySchedule", "Unknown/unexpected location: "+ev.location);
                continue;
            }
            this.eventsByLocation.get(ev.location).add(ev);
        }
        setEarliestAndLatestTimes();
    }

    public String getConferenceDay() {
        return conferenceDay;
    }

    public List<Event> getEvents() {
        ArrayList<Event> list = new ArrayList<Event>();
        for (List<Event> subList : eventsByLocation.values()) {
            list.addAll(subList);
        }
        return list;
    }

    public Map<String, List<Event>> getEventsByLocation() {
        return eventsByLocation;
    }

    public DateTime getEarliestTime() {
        return earliestTime;
    }

    public DateTime getLatestTime() {
        return latestTime;
    }

    public List<String> getStages() {
        return new ArrayList<String>(eventsByLocation.keySet());
    }

    private void setEarliestAndLatestTimes() {
        earliestTime = new DateTime(2050,1,1,0,0);
        latestTime = new DateTime(1980,1,1,0,0);

        for (Map.Entry<String, List<Event>> entry : eventsByLocation.entrySet()) {
            for (Event event : entry.getValue()) {
                try {
                    DateTime startTime = new DateTime(event.start_time);
                    DateTime endTime = new DateTime(event.end_time);
                    if (startTime.isBefore(earliestTime)) {
                        earliestTime = startTime;
                    }
                    if (endTime.isAfter(latestTime)) {
                        latestTime = endTime;
                    }
                }
                catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
