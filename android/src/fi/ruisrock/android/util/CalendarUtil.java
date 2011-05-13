package fi.ruisrock.android.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CalendarUtil {
	
	private static final Map<Integer, String> weekDays = new HashMap<Integer, String>();
	
	static {
		weekDays.put(Calendar.MONDAY, "Maanantai");
		weekDays.put(Calendar.TUESDAY, "Tiistai");
		weekDays.put(Calendar.WEDNESDAY, "Keskiviikko");
		weekDays.put(Calendar.THURSDAY, "Torstai");
		weekDays.put(Calendar.FRIDAY, "Perjantai");
		weekDays.put(Calendar.SATURDAY, "Lauantai");
		weekDays.put(Calendar.SUNDAY, "Sunnuntai");
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
