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
import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.domain.to.DaySchedule;
import fi.ruisrock2011.android.domain.to.FestivalDay;
import fi.ruisrock2011.android.domain.to.HTTPBackendResponse;
import fi.ruisrock2011.android.domain.to.StageType;
import fi.ruisrock2011.android.util.CalendarUtil;
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
				String artist = JSONUtil.getString(gigObj, "name");
				gig.setId(JSONUtil.getString(gigObj, "id"));
				gig.setArtist(artist);
				gig.setDescription(JSONUtil.getString(gigObj, "description"));
				gig.setStartTime(parseJsonDate(JSONUtil.getString(gigObj, "start")));
				gig.setEndTime(parseJsonDate(JSONUtil.getString(gigObj, "end")));
				gig.setStage(JSONUtil.getString(gigObj, "stage"));
				gig.setImageId(getImageIdForArtist(artist));
				if (isValidGig(gig)) {
					gigs.add(gig);
				}
			} catch (Exception e) {
				Log.w(TAG, "Received invalid JSON-structure", e);
			}
		}
		return gigs;
	}
	
	public static String truncateStageName(String stage) {
		if (stage == null) {
			return null;
		}
		return stage.replaceAll("(?i)[- ]?(lava|stage)$", "");
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
		if (gigs != null && gigs.size() >= 2) { // Hackish fail-safe
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

			Date nowDate = CalendarUtil.getNow();
			String timeInFuture = getDateStringWithMinuteDifference(nowDate, 18);
			String now = DB_DATE_FORMATTER.format(nowDate);
			cursor = db.query("gig", GIG_COLUMNS, "active = 1 AND favorite = 1 AND alerted = 0 AND datetime(startTime) <= datetime(?) AND datetime(endTime) > datetime(?)", new String[]{timeInFuture, now}, null, null, "startTime ASC");
			while (cursor.moveToNext()) {
		        Gig gig = convertCursorToGig(cursor, cursor.getString(0));
		        gigs.add(gig);
			}
			
			for (Gig gig : gigs) {
				// TODO: Uncomment
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
		Cursor cursor = db.query("gig", GIG_COLUMNS, "id = " + id, null, null, null, null);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			return convertCursorToGig(cursor, id);
		}
		return null;
	}
	
	private static Gig convertCursorToGig(Cursor cursor, String id) {
		return new Gig(cursor.getString(0), // id
				parseInteger(cursor.getString(1)), // imageId
				cursor.getString(2), // artist
				cursor.getString(3), // description
				parseDate(cursor.getString(4)), // startTime
				parseDate(cursor.getString(5)), // endTime
				cursor.getString(6), // stage
				cursor.getInt(7) > 0,
				cursor.getInt(8) > 0,
				cursor.getInt(9) > 0);
	}
	
	private static Integer parseInteger(String integer) {
		try {
			return Integer.valueOf(integer);
		} catch (Exception e) {
			return null;
		}
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
	
	public static String findNextArtistOnStageMessage(StageType stage, Context context) {
		if (stage == null) {
			return null;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			String now = DB_DATE_FORMATTER.format(CalendarUtil.getNow());
			cursor = db.query("gig", GIG_COLUMNS, "active = 1 AND stage IS NOT NULL AND startTime IS NOT NULL AND datetime(endTime) > datetime(?)", new String[]{now}, null, null, "startTime ASC");
			while (cursor.moveToNext()) {
		        Gig gig = convertCursorToGig(cursor, cursor.getString(0));
		        String artistOnStage = getArtistOnStageMessage(gig, stage, context);
		        if (artistOnStage != null) {
		        	return artistOnStage;
		        }
			}
		} finally {
			closeDb(db, cursor);
		}
		
		return null;
	}
	
	private static String getArtistOnStageMessage(Gig gig, StageType stageType, Context context) {
		String stage = gig.getStage();
		if (stage == null) {
			return null;
		}
		stage = stage.toLowerCase().trim();
		String matchedStage = null;
		switch (stageType) {
		case CONVERSE:
			if (stage.startsWith("converse")) {
				matchedStage = "Conversella";
			}
			break;
		case NIITTY:
			if (stage.startsWith("niitty")) {
				matchedStage = "Niittylavalla";
			}
			break;
		case PIKKU:
			if (stage.startsWith("pikku")) {
				matchedStage = "Pikkulavalla";
			}
			break;
		case RANTA:
			if (stage.startsWith("ranta")) {
				matchedStage = "Rantalavalla";
			}
			break;
		case TELTTA:
			if (stage.startsWith("teltta")) {
				matchedStage = "Teltalla";
			}
			break;
		}
		
		return (matchedStage != null) ? context.getString(R.string.mapActivity_nextOnStage, matchedStage, gig.getTime(), "\n" + gig.getArtist()) : null;
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
	
	private static Integer getImageIdForArtist(String artist) {
		if (StringUtil.isEmpty(artist)) {
			return null;
		}
		artist = artist.toLowerCase().trim();
		if (artist.startsWith("amorphis")) {
			return R.drawable.artistimg_amorphis;
		}
		if (artist.startsWith("anna calvi")) {
			return R.drawable.artistimg_anna_calvi;
		}
		if (artist.startsWith("apulanta")) {
			return R.drawable.artistimg_apulanta;
		}
		if (artist.startsWith("bob hund")) {
			return R.drawable.artistimg_bob_hund;
		}
		if (artist.startsWith("bring me the horizon")) {
			return R.drawable.artistimg_bring_me_the_horizon;
		}
		if (artist.startsWith("bullet for my valentine")) {
			return R.drawable.artistimg_bullet_for_my_valentine;
		}
		if (artist.startsWith("carpark north")) {
			return R.drawable.artistimg_carpark_north;
		}
		if (artist.startsWith("circle")) {
			return R.drawable.artistimg_circle;
		}
		if (artist.startsWith("elbow")) {
			return R.drawable.artistimg_elbow;
		}
		if (artist.startsWith("fleet foxes")) {
			return R.drawable.artistimg_fleet_foxes;
		}
		if (artist.startsWith("happoradio")) {
			return R.drawable.artistimg_happoradio;
		}
		if (artist.startsWith("hurts")) {
			return R.drawable.artistimg_hurts;
		}
		if (artist.startsWith("isobel campbell")) {
			return R.drawable.artistimg_isobelc_and_markl;
		}
		if (artist.contains("villegalle")) {
			return R.drawable.artistimg_jare_et_villegalle;
		}
		if (artist.startsWith("jätkäjätkät")) {
			return R.drawable.artistimg_jatkajatkat;
		}
		if (artist.startsWith("jenni vartiainen")) {
			return R.drawable.artistimg_jenni_vartiainen;
		}
		if (artist.startsWith("jukka poika")) {
			return R.drawable.artistimg_jukka_poika;
		}
		if (artist.startsWith("kaija koo")) {
			return R.drawable.artistimg_kaija_koo;
		}
		if (artist.startsWith("kotiteollisuus")) {
			return R.drawable.artistimg_kotiteollisuus;
		}
		if (artist.startsWith("magenta skycode")) {
			return R.drawable.artistimg_magenta_skycode;
		}
		if (artist.startsWith("manu chao")) {
			return R.drawable.artistimg_manu_chao;
		}
		if (artist.startsWith("michael monroe")) {
			return R.drawable.artistimg_michael_monroe;
		}
		if (artist.startsWith("paleface")) {
			return R.drawable.artistimg_paleface;
		}
		if (artist.startsWith("paramore")) {
			return R.drawable.artistimg_paramore;
		}
		if (artist.startsWith("pariisin kev")) {
			return R.drawable.artistimg_pariisin_kevat;
		}
		if (artist.startsWith("pertti kurikan nimi")) {
			return R.drawable.artistimg_pertti_kurikan_np;
		}
		if (artist.startsWith("pmmp")) {
			return R.drawable.artistimg_pmmp;
		}
		if (artist.startsWith("primus")) {
			return R.drawable.artistimg_primus;
		}
		if (artist.startsWith("raappana")) {
			return R.drawable.artistimg_raappana;
		}
		if (artist.startsWith("robyn")) {
			return R.drawable.artistimg_robyn;
		}
		if (artist.startsWith("rubik")) {
			return R.drawable.artistimg_rubik;
		}
		if (artist.startsWith("sabaton")) {
			return R.drawable.artistimg_sabaton;
		}
		if (artist.startsWith("scandinavian music group")) {
			return R.drawable.artistimg_scandinavian_mg;
		}
		if (artist.startsWith("stam1na")) {
			return R.drawable.artistimg_stam1na;
		}
		if (artist.startsWith("sweatmaster")) {
			return R.drawable.artistimg_sweatmaster;
		}
		if (artist.startsWith("the capital beat")) {
			return R.drawable.artistimg_the_capital_beat;
		}
		if (artist.startsWith("the national")) {
			return R.drawable.artistimg_the_national;
		}
		if (artist.startsWith("the prodigy")) {
			return R.drawable.artistimg_the_prodigy;
		}
		if (artist.startsWith("uusi fantasia")) {
			return R.drawable.artistimg_uusi_fantasia;
		}
		if (artist.startsWith("von hertzen brothers")) {
			return R.drawable.artistimg_von_hertzen_brothers;
		}
		return null;
	}
	
}
