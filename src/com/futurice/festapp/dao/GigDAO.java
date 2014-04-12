package com.futurice.festapp.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.futurice.festapp.domain.Gig;
import com.futurice.festapp.domain.GigLocation;
import com.futurice.festapp.domain.to.DaySchedule;
import com.futurice.festapp.domain.to.FestivalDay;
import com.futurice.festapp.domain.to.HTTPBackendResponse;
import com.futurice.festapp.util.GigArtistNameComparator;
import com.futurice.festapp.util.HTTPUtil;
import com.futurice.festapp.util.JSONUtil;
import com.futurice.festapp.util.FestAppConstants;
import com.futurice.festapp.util.StringUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.futurice.festapp.R;

/**
 * Data-access operations for Gigs.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class GigDAO {
	
	private static final String TAG = "GigDAO";
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
	
	private static final String GIGS_QUERY = "SELECT gig.id, gig.artist, gig.description, gig.favorite, gig.active, gig.alerted, gig.youtube, gig.spotify," +
			"location.stage, location.startTime, location.endTime, gig.artistimage FROM gig LEFT JOIN location ON (gig.id = location.id)";
	
	private final static int GIG_ID = 0;
	private final static int GIG_ARTIST = 1;
	private final static int GIG_DESCRIPTION = 2;
	private final static int GIG_FAVORITE = 3;
	private final static int GIG_ACTIVE = 4;
	private final static int GIG_ALERTED = 5;
	private final static int GIG_YOUTUBE = 6;
	private final static int GIG_SPOTIFY = 7;
	private final static int LOCATION_STAGE = 8;
	private final static int LOCATION_START_TIME = 9;
	private final static int LOCATION_END_TIME = 10;
	private final static int GIG_ARTIST_IMAGE = 11;
	
	private static Date startOfFriday = null;
	private static Date startOfSaturday = null;
	private static Date startOfSunday = null;
	private static Date endOfSunday = null;
	
	static {
		try {
			startOfFriday = DB_DATE_FORMATTER.parse("2013-07-05 06:00");
			startOfSaturday = DB_DATE_FORMATTER.parse("2013-07-06 06:00");
			startOfSunday = DB_DATE_FORMATTER.parse("2013-07-07 06:00");
			endOfSunday = DB_DATE_FORMATTER.parse("2013-07-07 23:00");
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
	
	
	public static List<Gig> findAllActive(Context context) {
		Map<String, Gig> gigs = new HashMap<String, Gig>();
		
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			cursor = db.rawQuery(GIGS_QUERY + " WHERE active = 1 ORDER BY gig.artist ASC, location.startTime ASC", null);
			while (cursor.moveToNext()) {
				String id = cursor.getString(GIG_ID);
				Gig gig = (gigs.containsKey(id)) ? gigs.get(id) : convertCursorToGig(cursor, id);
				gig.addLocation(convertCursorToGigLocation(cursor, id));
		        gigs.put(id, gig);
			}
		} finally {
			closeDb(db, cursor);
		}
		List<Gig> gigsList = new ArrayList<Gig>(gigs.values());
		Collections.sort(gigsList, new GigArtistNameComparator());
		return gigsList; 
	}
	
	public static List<Gig> parseFromJson(String json) throws Exception {
		Map<String, Gig> gigs = new HashMap<String, Gig>();
		JSONArray list = new JSONArray(json);
		for (int i=0; i < list.length(); i++) {
			try {
				JSONObject gigObj = list.getJSONObject(i);
								
				String gigId = JSONUtil.getString(gigObj, "id");
				
				Gig gig = new Gig();
				boolean isNewGig = true;
				if (gigs.containsKey(gigId)) {
					gig = gigs.get(gigId);
					isNewGig = false;
				} else {
					gig.setId(gigId);
					gig.setArtist(JSONUtil.getString(gigObj, "name"));
					gig.setDescription(JSONUtil.getString(gigObj, "content"));
					gig.setYoutube(JSONUtil.getString(gigObj,"youtube"));
					gig.setSpotify(JSONUtil.getString(gigObj,"spotify"));
					gig.setArtistImage(JSONUtil.getString(gigObj, "picture"));
				}
								
				Date startTime = parseJsonDate(JSONUtil.getLong(gigObj, "time_start"));
				Date endTime = parseJsonDate(JSONUtil.getLong(gigObj, "time_stop"));
				String stage = JSONUtil.getString(gigObj, "stage");
				if (startTime != null && endTime != null && StringUtil.isNotEmpty(stage)) {
					GigLocation gigLocation = new GigLocation(stage, startTime, endTime);
					gig.addLocation(gigLocation);
				}
				if (isValidGig(gig) && isNewGig) {
					gigs.put(gigId, gig);
				}
			} catch (Exception e) {
				Log.w(TAG, "Received invalid JSON-structure", e);
			}
		}
		return new ArrayList<Gig>(gigs.values());
	}
	
	public static String truncateStageName(String stage) {
		if (stage == null) {
			return null;
		}
		return stage.replaceAll("(?i)[- ]?(lava|stage)$", "");
	}
	
	private static Date parseJsonDate(Long time) {
		if (time == null) {
			return null;
		}
		
		// Backend returns false timestamps
		if (time == 4213275300L) {
			time = 1373220900L;
		}

		
		if(time > endOfSunday.getTime()) {
			time = endOfSunday.getTime();
		}
		return new Date(time * 1000);
	}
	
	public static DaySchedule findDaySchedule(Context context, FestivalDay festivalDay) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		TreeMap<String, List<Gig>> stageGigs = new TreeMap<String, List<Gig>>();
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			//cursor = db.query("gig", GIG_COLUMNS, "active = 1 AND festivalDay = ? AND stage IS NOT NULL", new String[]{festivalDay.name()}, null, null, "stage ASC, startTime ASC");
			cursor = db.rawQuery(GIGS_QUERY + " WHERE gig.active = 1 AND location.festivalDay = ? AND location.stage IS NOT NULL ORDER BY location.stage ASC, location.startTime ASC", new String[]{festivalDay.name()});
			while (cursor.moveToNext()) {
		        Gig gig = convertCursorToGig(cursor, cursor.getString(GIG_ID));
		        GigLocation location = convertCursorToGigLocation(cursor, cursor.getString(GIG_ID));
		        gig.addLocation(location);
		        if (!stageGigs.containsKey(location.getStage())) {
		        	stageGigs.put(location.getStage(), new ArrayList<Gig>());
		        }
		        stageGigs.get(location.getStage()).add(gig);
			}
		} finally {
			closeDb(db, cursor);
		}
		return new DaySchedule(festivalDay, stageGigs);
	}
	
	public static void updateGigsOverHttp(Context context) throws Exception {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.GIGS_JSON_URL);
		if (!response.isValid() || response.getStringContent() == null) {
			return;
		}
		ConfigDAO.setEtagForGigs(context, response.getEtag());
		
		List<Gig> gigs = parseFromJson(response.getStringContent());
		if (gigs != null && gigs.size() >= 2) { // Hackish fail-safe
			SQLiteDatabase db = null;
			try {
				db = (new DatabaseHelper(context)).getWritableDatabase();
				db.beginTransaction();
				db.execSQL("UPDATE gig SET active = 0");
				db.execSQL("DELETE FROM location");
				
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
						
						for (ContentValues cv : GigDAO.convertGigToLocationContentValues(gig)) {
							db.insert("location", null, cv);
						}
					} else {
						invalidGigs++;
					}
				}
				db.setTransactionSuccessful();
				Log.i(TAG, String.format("Successfully updated Gigs via HTTP. Result {received: %d, updated: %d, added: %s, invalid: %s", gigs.size(), updatedGigs, newGigs, invalidGigs));
			} finally {
				db.endTransaction();
				closeDb(db, null);
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

			Date nowDate = new Date();
			String timeInFuture = getDateStringWithMinuteDifference(nowDate, 18);
			String now = DB_DATE_FORMATTER.format(nowDate);
			cursor = db.rawQuery(GIGS_QUERY + " WHERE active = 1 AND favorite = 1 AND alerted = 0 AND datetime(location.startTime) <= datetime(?) AND datetime(location.endTime) > datetime(?)", new String[]{timeInFuture, now});
			while (cursor.moveToNext()) {
				String id = cursor.getString(GIG_ID);
		        Gig gig = convertCursorToGig(cursor, id);
		        gig.addLocation(convertCursorToGigLocation(cursor, id));
		        gigs.add(gig);
			}
			
			for (Gig gig : gigs) {
				db.execSQL("UPDATE gig SET alerted = 1 where id = ?", new Object[]{gig.getId()});
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
		Cursor cursor = db.rawQuery(GIGS_QUERY + " WHERE gig.id = ?", new String[]{id});
		
		Gig gig = null;
		while (cursor.moveToNext()) {
			if (gig == null) {
				gig = convertCursorToGig(cursor, id);
			}
			gig.addLocation(convertCursorToGigLocation(cursor, id));
		}
		cursor.close();
		return gig;
	}
	
	private static Gig convertCursorToGig(Cursor cursor, String id) {
		return new Gig(
				cursor.getString(GIG_ID),
				cursor.getString(GIG_ARTIST),
				cursor.getString(GIG_DESCRIPTION),
				cursor.getInt(GIG_FAVORITE) > 0,
				cursor.getInt(GIG_ACTIVE) > 0,
				cursor.getInt(GIG_ALERTED) > 0,
				cursor.getString(GIG_YOUTUBE),
				cursor.getString(GIG_SPOTIFY),
				cursor.getString(GIG_ARTIST_IMAGE));
				
	}
	
	private static GigLocation convertCursorToGigLocation(Cursor cursor, String id) {
		return new GigLocation(
				cursor.getString(LOCATION_STAGE),
				parseDate(cursor.getString(LOCATION_START_TIME)),
				parseDate(cursor.getString(LOCATION_END_TIME)));
	}
	
	public static ContentValues convertGigToContentValues(Gig gig) {
		ContentValues values = new ContentValues();
		values.put("id", gig.getId());
		values.put("artist", gig.getArtist());
		values.put("description", gig.getDescription());
		values.put("active", gig.isActive());
		values.put("favorite", gig.isFavorite());
		values.put("alerted", gig.isAlerted());
		values.put("youtube", gig.getYoutube());
		values.put("spotify", gig.getSpotify());
		values.put("artistimage", gig.getArtistImage());
		return values;
	}
	
	public static List<ContentValues> convertGigToLocationContentValues(Gig gig) {
		List<ContentValues> cvs = new ArrayList<ContentValues>();
		for (GigLocation location : gig.getLocations()) {
			ContentValues values = new ContentValues();
			String startTime = (location.getStartTime() != null) ? DB_DATE_FORMATTER.format(location.getStartTime()) : null;
			String endTime = (location.getEndTime() != null) ? DB_DATE_FORMATTER.format(location.getEndTime()) : null;
			String stage = location.getStage();
			
			if (StringUtil.isEmpty(gig.getId()) || startTime == null || endTime == null || StringUtil.isEmpty(stage)) {
				continue;
			}
			
			values.put("id", gig.getId());
			values.put("stage", location.getStage());
			values.put("startTime", startTime);
			values.put("endTime", endTime);
			if (location.getFestivalDay() != null) {
				values.put("festivalDay", location.getFestivalDay().name());
			} else {
				values.put("festivalDay", (String) null);
			}
			cvs.add(values);
		}
		return cvs;
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
	
	public static String findNextArtistOnStageMessage(String stageName, Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String artistOnStage = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			String now = DB_DATE_FORMATTER.format(new Date());
			cursor = db.rawQuery(GIGS_QUERY + " WHERE gig.active = 1 AND location.stage IS NOT NULL AND location.startTime IS NOT NULL AND datetime(location.endTime) > datetime(?) ORDER BY location.startTime ASC", new String[]{now});
			while (cursor.moveToNext()) {
				String id = cursor.getString(GIG_ID);
		        Gig gig = convertCursorToGig(cursor, id);
		        GigLocation gigLocation = convertCursorToGigLocation(cursor, id);
		        gig.addLocation(gigLocation);
		        artistOnStage = getArtistOnStageMessage(gig, stageName, context);
		        if (artistOnStage != null) {
		        	break;
		        }
			}
		} finally {
			closeDb(db, cursor);
		}
		
		return artistOnStage;
	}
	

	private static String getArtistOnStageMessage(Gig gig, String stageName, Context context) {
		GigLocation location = gig.getOnlyLocation();

		if (location == null) {
			return null;
		}

		stageName = stageName.toLowerCase(Locale.getDefault()).trim();

		String matchedStage = null;
		if (stageName.equals("mini")) {
			matchedStage = "Minilavalla";
		} else if (stageName.equals("niitty")) {
			matchedStage = "Niittylavalla";
		} else if (stageName.equals("louna")) {
			matchedStage = "Louna-lavalla";
		} else if (stageName.equals("ranta")) {
			matchedStage = "Rantalavalla";
		} else if (stageName.equals("teltta")) {
			matchedStage = "Teltassa";
		} else {
			return null;
		}

		return context.getString(R.string.mapActivity_nextOnStage, matchedStage, location.getTime(), gig.getArtist());
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
