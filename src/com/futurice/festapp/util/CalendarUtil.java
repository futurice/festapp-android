package com.futurice.festapp.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.futurice.festapp.R;
import android.content.Context;

public class CalendarUtil {
	
	private static final Map<Integer, String> weekDays = new HashMap<Integer, String>();
	
	public static void load(Context context) {
		weekDays.put(Calendar.MONDAY, context.getResources().getString(R.string.calendar_Monday));
		weekDays.put(Calendar.TUESDAY, context.getResources().getString(R.string.calendar_Tuesday));
		weekDays.put(Calendar.WEDNESDAY, context.getResources().getString(R.string.calendar_Wednesday));
		weekDays.put(Calendar.THURSDAY, context.getResources().getString(R.string.calendar_Thursday));
		weekDays.put(Calendar.FRIDAY, context.getResources().getString(R.string.calendar_Friday));
		weekDays.put(Calendar.SATURDAY, context.getResources().getString(R.string.calendar_Saturday));
		weekDays.put(Calendar.SUNDAY, context.getResources().getString(R.string.calendar_Sunday));
	}
	
	public static String getFullWeekdayName(int weekday) {
		return weekDays.get(weekday);
	}
	
	public static String getShortWeekdayName(int weekday) {
		return weekDays.get(weekday).substring(0, 1);
	}
	
	public static int getMinutesBetweenTwoDates(Date start, Date end) {
		long diff = Math.abs(end.getTime() - start.getTime());
		return (int) (diff / 1000 / 60);
	}

}
