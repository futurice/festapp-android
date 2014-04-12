package com.futurice.festapp.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.futurice.festapp.domain.Gig;
import com.futurice.festapp.domain.to.FestivalDay;
import com.futurice.festapp.util.GigArtistNameComparator;

public class FestivalDayDAO {
	
	private static final String TAG = "FestivalDayDAO";
	
	public static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

	public static List<FestivalDay> getFestivalDays(Context c) {
		List<FestivalDay> festivalDays = new ArrayList<FestivalDay>();
		
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String str = "";
		try {
			db = (new DatabaseHelper(c)).getReadableDatabase();
			cursor = db.rawQuery("SELECT festdate FROM festival", null);
			while (cursor.moveToNext()) {
				Calendar date = Calendar.getInstance();
				str = cursor.getString(0);
				date.setTime(FestivalDayDAO.DB_DATE_FORMATTER.parse(str));
				festivalDays.add(new FestivalDay(date));
			}
		} 
		catch(Exception e){
			Log.e(TAG,"Invalid date format XXXXXX "+str);
		}
		finally {
			closeDb(db, cursor);
		}
		return festivalDays;
	}
	
	private static void closeDb(SQLiteDatabase db, Cursor cursor) {
		if (db != null) {
			db.close();
		}
		if (cursor != null) {
			cursor.close();
		}
	}
	
	/*public static List<FestivalDay> getFestivalDays(){
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
	}*/
	
	//@TODO: refactor
	public static FestivalDay getFirstDayOfFestival(Context c){
		return FestivalDayDAO.getFestivalDays(c).get(0);
	}
	
	public static FestivalDay getLastDayOfFestival(Context c){
		List<FestivalDay> festivalDays = FestivalDayDAO.getFestivalDays(c);
		return festivalDays.get(festivalDays.size()-1);
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
