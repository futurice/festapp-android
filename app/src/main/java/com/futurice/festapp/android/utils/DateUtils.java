package com.futurice.festapp.android.utils;

import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.List;

import com.futurice.festapp.android.models.pojo.Gig;
/**
 * Created by amed on 18.08.14.
 */
public class DateUtils {

    public static void sortEventsByStartTime(List<Gig> listGigs) {
        java.util.Collections.sort(listGigs, new Comparator<Gig>() { @Override public int compare(Gig lhs, Gig rhs) {
            DateTime lhsStart = new DateTime(lhs.startTime);
            DateTime rhsStart = new DateTime(rhs.startTime);
            if (lhsStart.isAfter(rhsStart))
                return 1;
            else if (lhsStart.isBefore(rhsStart))
                return -1;
            else
                return 0;
        }});
    }
}
