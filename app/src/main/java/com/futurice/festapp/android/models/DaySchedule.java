package com.futurice.festapp.android.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.futurice.festapp.android.models.pojo.Gig;

public class DaySchedule {

    public static final String[] ALL_ROOMS = {"Galerie", "Raum 2", "Raum 3", "Raum 4", "Loft", "Raum 5", "Atelier"};
    private String conferenceDay;
    private Map<String, List<Gig>> eventsByLocation = new TreeMap<String, List<Gig>>();
    private DateTime earliestTime;
    private DateTime latestTime;

    public DaySchedule(String conferenceDay, List<Gig> gigs) {
        this.conferenceDay = conferenceDay;
        this.eventsByLocation = new HashMap<String, List<Gig>>();

        // Initialize eventsByLocation with all the rooms, ordered correctly
        for (String location : ALL_ROOMS) {
            this.eventsByLocation.put(location, new ArrayList<Gig>());
        }

        // Organize given list of events into eventsByLocation
        for (Gig ev : gigs) {
            if (ev.day == null) { continue; }
            if (!ev.day.equals(conferenceDay)) { continue; }

            if (this.eventsByLocation.get(ev.stage) == null) {
                Log.e("DaySchedule", "Unknown/unexpected stage: "+ev.stage);
                continue;
            }
            this.eventsByLocation.get(ev.stage).add(ev);
        }
        setEarliestAndLatestTimes();
    }

    public String getConferenceDay() {
        return conferenceDay;
    }

    public List<Gig> getEvents() {
        ArrayList<Gig> list = new ArrayList<Gig>();
        for (List<Gig> subList : eventsByLocation.values()) {
            list.addAll(subList);
        }
        return list;
    }

    public Map<String, List<Gig>> getEventsByLocation() {
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

        for (Map.Entry<String, List<Gig>> entry : eventsByLocation.entrySet()) {
            for (Gig gig : entry.getValue()) {
                try {
                    DateTime startTime = new DateTime(gig.startTime, DateTimeZone.UTC);
                    DateTime endTime = new DateTime(gig.endTime, DateTimeZone.UTC);
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
