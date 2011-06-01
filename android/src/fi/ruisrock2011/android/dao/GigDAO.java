package fi.ruisrock2011.android.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.domain.to.DaySchedule;
import fi.ruisrock2011.android.domain.to.FestivalDay;
import fi.ruisrock2011.android.domain.to.HTTPBackendResponse;
import fi.ruisrock2011.android.util.HTTPUtil;
import fi.ruisrock2011.android.util.JSONUtil;
import fi.ruisrock2011.android.util.RuisrockConstants;
import fi.ruisrock2011.android.util.StringUtil;

/**
 * Data-access operations for Gigs.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class GigDAO {
	
	private static final String TAG = "GigDAO";
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final String[] GIG_COLUMNS = { "id", "imageId", "artist", "description",
		"startTime", "endTime", "stage", "favorite", "active", "alerted" };
	
	private static Date startOfFriday = null;
	private static Date startOfSaturday = null;
	private static Date startOfSunday = null;
	private static Date endOfSunday = null;
	
	static {
		try {
			startOfFriday = DB_DATE_FORMATTER.parse("2011-07-08 06:00");
			startOfSaturday = DB_DATE_FORMATTER.parse("2011-07-09 06:00");
			startOfSunday = DB_DATE_FORMATTER.parse("2011-07-10 06:00");
			endOfSunday = DB_DATE_FORMATTER.parse("2011-07-11 06:00");
		} catch (ParseException e) {
			Log.e(TAG, "Error setting festival day intervals.");
		}
	}
	
	public static Date getStartOfFriday() {
		return startOfFriday;
	}
	
	public static Date getStartOfSaturday() {
		return startOfSaturday;
	}
	
	public static Date getStartOfSunday() {
		return startOfSunday;
	}
	
	public static Date getEndOfSunday() {
		return endOfSunday;
	}
	
	
	public static void setFavorite(Context context, String gigId, boolean favorite) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("favorite", favorite);
			db.update("gig", values, "id = ?", new String[] {String.valueOf(gigId)});
		} finally {
			closeDb(db, cursor);
		}
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
	
	public static List<Gig> parseFromJson(String json) throws Exception {
		List<Gig> gigs = new ArrayList<Gig>();
		JSONArray list = new JSONArray(json);
		
		for (int i=0; i < list.length(); i++) {
			try {
				JSONObject gigObj = list.getJSONObject(i);
				Gig gig = new Gig();
				gig.setId(JSONUtil.getString(gigObj, "id"));
				gig.setArtist(JSONUtil.getString(gigObj, "name"));
				gig.setDescription(JSONUtil.getString(gigObj, "description"));
				gig.setStartTime(parseJsonDate(JSONUtil.getString(gigObj, "start")));
				gig.setEndTime(parseJsonDate(JSONUtil.getString(gigObj, "end")));
				gig.setStage(JSONUtil.getString(gigObj, "stage"));
				if (gigObj.has("imageId")) {
					gig.setImageId(JSONUtil.getString(gigObj, "imageId"));
				}
				gigs.add(gig);
			} catch (Exception e) {
				Log.w(TAG, "Received invalid JSON-structure", e);
			}
		}
		
		return gigs;
	}
	
	private static Date parseJsonDate(String date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		try {
			return sdf.parse(date);
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public static DaySchedule findDaySchedule(Context context, FestivalDay festivalDay) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		TreeMap<String, List<Gig>> stageGigs = new TreeMap<String, List<Gig>>();
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
		HTTPBackendResponse response = httpUtil.performGet(RuisrockConstants.GIGS_JSON_URL);
		if (!response.isValid() || response.getContent() == null) {
			return;
		}
		ConfigDAO.setEtagForGigs(context, response.getEtag());
		
		List<Gig> gigs = parseFromJson(response.getContent());
		if (gigs != null && gigs.size() > 3) { // Hackish fail-safe
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = (new DatabaseHelper(context)).getWritableDatabase();
				db.beginTransaction();
				db.execSQL("UPDATE gig SET active = 0");
				
				int invalidGigs = 0, newGigs = 0, updatedGigs = 0;
				for (Gig gig : gigs) {
					if (isValidGig(gig)) {
						Gig existingGig = findGig(db, gig.getId());
						if (existingGig != null) {
							gig.setFavorite(existingGig.isFavorite());
							gig.setAlerted(existingGig.isAlerted());
							db.update("gig", convertGigToContentValues(gig), "id = ?", new String[] {gig.getId()});
							updatedGigs++;
						} else {
							db.insert("gig", "stage", convertGigToContentValues(gig));
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
	
	public static List<Gig> findGigsToAlert(Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		List<Gig> gigs = new ArrayList<Gig>();
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			db.beginTransaction();
			
			String timeInFuture = "";
			String now = "";
			try {
				Date d = DB_DATE_FORMATTER.parse("2011-07-08 17:55");
				timeInFuture = getDateStringWithMinuteDifference(d, 15);
				now = DB_DATE_FORMATTER.format(d);
			} catch (Exception e) {
			}

			// TODO: Use these, and remove from above!
			//String timeInFuture = getDateStringWithMinuteDifference(new Date(), 15);
			//String now = DB_DATE_FORMATTER.format(new Date()); 
			cursor = db.query("gig", GIG_COLUMNS, "active = 1 AND favorite = 1 AND alerted = 0 AND datetime(startTime) <= datetime(?) AND datetime(endTime) > datetime(?)", new String[]{timeInFuture, now}, null, null, "startTime ASC");
			while (cursor.moveToNext()) {
		        Gig gig = convertCursorToGig(cursor, cursor.getString(0));
		        gigs.add(gig);
			}
			
			for (Gig gig : gigs) {
				// TODO: Uncomment
				//db.execSQL("UPDATE gig SET alerted = 1 where id = ?", new Object[]{gig.getId()});
			}
			
			Log.i(TAG, String.format("Successfully found and marked %d Gigs as alerted.", gigs.size()));
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			closeDb(db, cursor);
		}
		return gigs;
	}
	
	private static String getDateStringWithMinuteDifference(Date date, int minuteDifference) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minuteDifference);
		
		return DB_DATE_FORMATTER.format(cal.getTime());
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
		return new Gig(cursor.getString(0), // id
				cursor.getString(1), // imageId
				cursor.getString(2), // artist
				cursor.getString(3), // description
				parseDate(cursor.getString(4)), // startTime
				parseDate(cursor.getString(5)), // endTime
				cursor.getString(6), // stage
				cursor.getInt(7) > 0,
				cursor.getInt(8) > 0,
				cursor.getInt(9) > 0);
	}
	
	public static ContentValues convertGigToContentValues(Gig gig) {
		ContentValues values = new ContentValues();
		String startTime = (gig.getStartTime() != null) ? DB_DATE_FORMATTER.format(gig.getStartTime()) : null;
		String endTime = (gig.getEndTime() != null) ? DB_DATE_FORMATTER.format(gig.getEndTime()) : null;
		values.put("id", gig.getId());
		values.put("imageId", gig.getImageId());
		values.put("artist", gig.getArtist());
		values.put("description", gig.getDescription());
		values.put("stage", gig.getStage());
		values.put("startTime", startTime);
		values.put("endTime", endTime);
		values.put("active", gig.isActive());
		values.put("favorite", gig.isFavorite());
		values.put("alerted", gig.isAlerted());
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
		if (startTime.after(GigDAO.getStartOfFriday()) && startTime.before(GigDAO.getStartOfSaturday())) {
			return FestivalDay.FRIDAY;
		}
		if (startTime.after(GigDAO.getStartOfSaturday()) && startTime.before(GigDAO.getStartOfSunday())) {
			return FestivalDay.SATURDAY;
		}
		if (startTime.after(GigDAO.getStartOfSunday()) && startTime.before(GigDAO.getEndOfSunday())) {
			return FestivalDay.SUNDAY;
		}
		return null;
	}
	
	private static Date parseDate(String date) {
		try {
			return DB_DATE_FORMATTER.parse(date);
		} catch (Exception e) {
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
