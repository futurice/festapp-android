package fi.ruisrock.android.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import fi.ruisrock.android.domain.Gig;
import fi.ruisrock.android.domain.to.DaySchedule;
import fi.ruisrock.android.domain.to.FestivalDay;
import fi.ruisrock.android.util.HTTPUtil;
import fi.ruisrock.android.util.RuisrockConstants;
import fi.ruisrock.android.util.StringUtil;

/**
 * Data-access operations for Gigs.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class GigDAO {
	
	private static final String TAG = "NewsDAO";
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final String[] GIG_COLUMNS = { "id", "artist", "description",
		"startTime", "endTime", "stage", "bandImageUrl", "bandLogoUrl", "favorite", "active" };
	
	private static Date beginningOfSaturday = null;
	private static Date endOfSaturday = null;
	
	static {
		try {
			beginningOfSaturday = DB_DATE_FORMATTER.parse("2011-07-09 06:00");
			endOfSaturday = DB_DATE_FORMATTER.parse("2011-07-10 06:00");
		} catch (ParseException e) {
			Log.e(TAG, "Error setting festival day intervals.");
		}
	}
	
	public static Date getBeginningOfSaturday() {
		return beginningOfSaturday;
	}
	
	public static Date getEndOfSaturday() {
		return endOfSaturday;
	}
	
	
	public static List<String> findDistinctStages(Context context) {
		List<String> stages = new ArrayList<String>();

		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			cursor = db.query(true, "gig", new String[] {"stage"}, "active = 1", null, null, null, "stage ASC", null);
			while (cursor.moveToNext()) {
				stages.add(cursor.getString(0));
			}
		} finally {
			closeDb(db, cursor);
		}
		return stages;
	}
	
	
	public static List<Gig> findAllActive(Context context) {
		List<Gig> gigs = new ArrayList<Gig>();
		
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			cursor = db.query("gig", GIG_COLUMNS, "active = 1", null, null, null, "artist ASC");
			while (cursor.moveToNext()) {
		        gigs.add(convertCursorToGig(cursor, cursor.getString(0)));
			}
		} finally {
			closeDb(db, cursor);
		}
		return gigs;
	}
	
	public static DaySchedule findDaySchedule(Context context, FestivalDay festivalDay) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		Map<String, List<Gig>> stageGigs = new HashMap<String, List<Gig>>();
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			cursor = db.query("gig", GIG_COLUMNS, "active = 1 AND festivalDay = ? AND stage IS NOT NULL", new String[]{festivalDay.name()}, null, null, "stage ASC, startTime ASC");
			while (cursor.moveToNext()) {
		        Gig gig = convertCursorToGig(cursor, cursor.getString(0));
		        if (!stageGigs.containsKey(gig.getStage())) {
		        	stageGigs.put(gig.getStage(), new ArrayList<Gig>());
		        }
		        stageGigs.get(gig.getStage()).add(gig);
			}
		} finally {
			closeDb(db, cursor);
		}
		return new DaySchedule(festivalDay, stageGigs);
	}
	
	public static void updateGigsOverHttp(Context context) throws Exception {
		HTTPUtil httpUtil = new HTTPUtil();
		String gigsJson = httpUtil.performGet(RuisrockConstants.GIGS_JSON_URL, null, null, null);
		List<Gig> gigs = new ObjectMapper().readValue(gigsJson, new TypeReference<List<Gig>>() {});
		
		if (gigs != null && gigs.size() > 1) { // Hackish fail-safe
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = (new DatabaseHelper(context)).getWritableDatabase();
				db.beginTransaction();
				db.rawQuery("UPDATE gig SET active = 0", null);
				
				int invalidGigs = 0, newGigs = 0, updatedGigs = 0;
				for (Gig gig : gigs) {
					if (isValidGig(gig)) {
						Gig existingGig = findGig(db, gig.getId());
						if (existingGig != null) {
							gig.setFavorite(existingGig.isFavorite());
							db.update("gig", convertGigToContentValues(gig), "id = ?", new String[] {gig.getId()});
							updatedGigs++;
						} else {
							db.insert("gig", "bandLogoUrl", convertGigToContentValues(gig));
							newGigs++;
						}
					} else {
						invalidGigs++;
					}
				}
				db.setTransactionSuccessful();
				Log.i(TAG, String.format("Successfully updated Gigs via HTTP. Result {received: %d, updated: %d, added: %s, invalid: %s", gigs.size(), updatedGigs, newGigs, invalidGigs));
			} finally {
				db.endTransaction();
				closeDb(db, cursor);
			}
		} else {
			Log.w(TAG, "Could not update Gigs.");
		}
	}
	
	public static Gig findGig(Context context, String id) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			return findGig(db, id);
		} finally {
			closeDb(db, cursor);
		}
	}
	
	
	
	private static Gig findGig(SQLiteDatabase db, String id) {
		Cursor cursor = db.query("gig", GIG_COLUMNS, "id = " + id, null, null, null, null);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			return convertCursorToGig(cursor, id);
		}
		return null;
	}
	
	private static Gig convertCursorToGig(Cursor cursor, String id) {
		return new Gig(cursor.getString(0),
				cursor.getString(1),
				cursor.getString(2),
				parseDate(cursor.getString(3)),
				parseDate(cursor.getString(4)),
				cursor.getString(5),
				cursor.getString(6),
				cursor.getString(7),
				cursor.getInt(8) > 0,
				cursor.getInt(9) > 0);
	}
	
	public static ContentValues convertGigToContentValues(Gig gig) {
		ContentValues values = new ContentValues();
		String startTime = (gig.getStartTime() != null) ? DB_DATE_FORMATTER.format(gig.getStartTime()) : null;
		String endTime = (gig.getEndTime() != null) ? DB_DATE_FORMATTER.format(gig.getEndTime()) : null;
		values.put("id", gig.getId());
		values.put("artist", gig.getArtist());
		values.put("bandImageUrl", gig.getBandImageUrl());
		values.put("bandLogoUrl", gig.getBandLogoUrl());
		values.put("description", gig.getDescription());
		values.put("stage", gig.getStage());
		values.put("startTime", startTime);
		values.put("endTime", endTime);
		values.put("active", gig.isActive());
		values.put("favorite", gig.isFavorite());
		if (gig.getFestivalDay() != null) {
			values.put("festivalDay", gig.getFestivalDay().name());
		} else {
			values.put("festivalDay", (String) null);
		}
		return values;
	}
	
	
	public static boolean isValidGig(Gig gig) {
		if (gig == null || StringUtil.isEmpty(gig.getId()) || StringUtil.isEmpty(gig.getArtist())) {
			return false;
		}
		return true;
	}
	
	public static int removeAll(Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			return db.delete("gig", null, null);
		} finally {
			closeDb(db, cursor);
		}
	}
	
	public static FestivalDay getFestivalDay(Date startTime) {
		if (startTime.after(GigDAO.getBeginningOfSaturday()) && startTime.before(GigDAO.getEndOfSaturday())) {
			return FestivalDay.SATURDAY;
		}
		if (startTime.before(GigDAO.getBeginningOfSaturday())) {
			return FestivalDay.FRIDAY;
		}
		return FestivalDay.SUNDAY;
	}
	
	private static Date parseDate(String date) {
		try {
			return DB_DATE_FORMATTER.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	private static void closeDb(SQLiteDatabase db, Cursor cursor) {
		if (db != null) {
			db.close();
		}
		if (cursor != null) {
			cursor.close();
		}
	}
	
}
