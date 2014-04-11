package com.futurice.festapp.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import com.futurice.festapp.domain.to.FestivalDay;

public class FestivalDayDAO {
	
	private static final String TAG = "FestivalDayDAO";
	
	public static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

	public static List<FestivalDay> getFestivalDays(){
		Calendar from = Calendar.getInstance(), to = Calendar.getInstance();
		//@TODO: Get start and end dates from config JSON / local db
		try{
			from.setTime(DB_DATE_FORMATTER.parse("2014-04-10"));
			to.setTime(DB_DATE_FORMATTER.parse("2014-04-13"));
		} catch(Exception e){
			Log.e(TAG,"Invalid date format");
		}
		
		
		List<FestivalDay> days = new ArrayList<FestivalDay>();
		
		for (Date date = from.getTime(); !from.after(to); from.add(Calendar.DATE, 1), date = from.getTime()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			days.add(new FestivalDay(cal));
		}
		
		return days;
	}
	
	//@TODO: refactor
	public static FestivalDay getFirstDayOfFestival(){
		return FestivalDayDAO.getFestivalDays().get(0);
	}
	
	public static FestivalDay toFestivalDay(String day){
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(DB_DATE_FORMATTER.parse(day));
		} catch(Exception e) {
			Log.e(TAG,"Invalid date format 2"+day);
		}
		return new FestivalDay(c);
	}
}
