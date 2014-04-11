package com.futurice.festapp.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.futurice.festapp.ContextRetriever;
import com.futurice.festapp.R;

public class CalendarUtil {
	
	private static final Map<Integer, String> weekDays = new HashMap<Integer, String>();
	
	static {
		weekDays.put(Calendar.MONDAY, ContextRetriever.getContext().getResources().getString(R.string.cMonday));
		weekDays.put(Calendar.TUESDAY, ContextRetriever.getContext().getResources().getString(R.string.cTuesday));
		weekDays.put(Calendar.WEDNESDAY, ContextRetriever.getContext().getResources().getString(R.string.cWednesday));
		weekDays.put(Calendar.THURSDAY, ContextRetriever.getContext().getResources().getString(R.string.cThursday));
		weekDays.put(Calendar.FRIDAY, ContextRetriever.getContext().getResources().getString(R.string.cFriday));
		weekDays.put(Calendar.SATURDAY, ContextRetriever.getContext().getResources().getString(R.string.cSaturday));
		weekDays.put(Calendar.SUNDAY, ContextRetriever.getContext().getResources().getString(R.string.cSunday));
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
