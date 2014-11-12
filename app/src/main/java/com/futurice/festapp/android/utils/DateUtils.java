package com.futurice.festapp.android.utils;

import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.List;

import com.futurice.festapp.android.models.pojo.Event;
/**
 * Created by amed on 18.08.14.
 */
public class DateUtils {

    public static void sortEventsByStartTime(List<Event> listEvents) {
        java.util.Collections.sort(listEvents, new Comparator<Event>() { @Override public int compare(Event lhs, Event rhs) {
            DateTime lhsStart = new DateTime(lhs.start_time);
            DateTime rhsStart = new DateTime(rhs.start_time);
            if (lhsStart.isAfter(rhsStart))
                return 1;
            else if (lhsStart.isBefore(rhsStart))
                return -1;
            else
                return 0;
        }});
    }
}
