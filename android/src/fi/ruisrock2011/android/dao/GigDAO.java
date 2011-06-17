package fi.ruisrock2011.android.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import fi.ruisrock2011.android.domain.GigLocation;
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
	//private static final String[] GIG_COLUMNS = { "id", "artist", "description", "favorite", "active", "alerted" };
	
	private static final String GIGS_QUERY = "SELECT gig.id, gig.artist, gig.description, gig.favorite, gig.active, gig.alerted, " +
			"location.stage, location.startTime, location.endTime FROM gig LEFT JOIN location ON (gig.id = location.id)";
	
	// gig.id				0
	// gig.artist			1
	// gig.description		2
	// gig.favorite			3
	// gig.active			4
	// gig.alerted			5
	// location.stage		6
	// location.startTime	7
	// location.endTime		8
	
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
		Map<String, Gig> gigs = new HashMap<String, Gig>();
		
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			//cursor = db.query("gig", GIG_COLUMNS, "active = 1", null, null, null, "artist ASC");
			cursor = db.rawQuery(GIGS_QUERY + " WHERE active = 1 ORDER BY gig.artist ASC, location.startTime ASC", null);
			while (cursor.moveToNext()) {
				String id = cursor.getString(0);
				Gig gig = (gigs.containsKey(id)) ? gigs.get(id) : convertCursorToGig(cursor, id);
				gig.addLocation(convertCursorToGigLocation(cursor, id));
		        gigs.put(id, gig);
			}
		} finally {
			closeDb(db, cursor);
		}
		return new ArrayList<Gig>(gigs.values());
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
					gig.setDescription(JSONUtil.getString(gigObj, "description"));
				}
				
				Date startTime = parseJsonDate(JSONUtil.getString(gigObj, "start"));
				Date endTime = parseJsonDate(JSONUtil.getString(gigObj, "end"));
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
			//cursor = db.query("gig", GIG_COLUMNS, "active = 1 AND festivalDay = ? AND stage IS NOT NULL", new String[]{festivalDay.name()}, null, null, "stage ASC, startTime ASC");
			cursor = db.rawQuery(GIGS_QUERY + " WHERE gig.active = 1 AND location.festivalDay = ? AND location.stage IS NOT NULL ORDER BY gig.id", new String[]{festivalDay.name()});
			while (cursor.moveToNext()) {
		        Gig gig = convertCursorToGig(cursor, cursor.getString(0));
		        GigLocation location = convertCursorToGigLocation(cursor, cursor.getString(0));
		        gig.addLocation(location);
		        if (!stageGigs.containsKey(gig.getOnlyStage())) {
		        	stageGigs.put(gig.getOnlyStage(), new ArrayList<Gig>());
		        }
		        stageGigs.get(gig.getOnlyStage()).add(gig);
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
			//cursor = db.query("gig", GIG_COLUMNS, "active = 1 AND favorite = 1 AND alerted = 0 AND datetime(startTime) <= datetime(?) AND datetime(endTime) > datetime(?)", new String[]{timeInFuture, now}, null, null, "startTime ASC");
			cursor = db.rawQuery(GIGS_QUERY + " WHERE active = 1 AND favorite = 1 AND alerted = 0 AND datetime(location.startTime) <= datetime(?) AND datetime(location.endTime) > datetime(?)", new String[]{timeInFuture, now});
			while (cursor.moveToNext()) {
				String id = cursor.getString(0);
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
		//Cursor cursor = db.query("gig", GIG_COLUMNS, "id = " + id, null, null, null, null);
		Cursor cursor = db.rawQuery(GIGS_QUERY + " WHERE gig.id = ?", new String[]{id});
		
		Gig gig = null;
		while (cursor.moveToNext()) {
			if (gig == null) {
				gig = convertCursorToGig(cursor, id);
			}
			gig.addLocation(convertCursorToGigLocation(cursor, id));
		}
		return gig;
	}
	
	private static Gig convertCursorToGig(Cursor cursor, String id) {
		return new Gig(cursor.getString(0), // id
				cursor.getString(1), // artist
				cursor.getString(2), // description
				cursor.getInt(3) > 0,
				cursor.getInt(4) > 0,
				cursor.getInt(5) > 0);
	}
	
	private static GigLocation convertCursorToGigLocation(Cursor cursor, String id) {
		return new GigLocation(
				cursor.getString(6), // stage
				parseDate(cursor.getString(7)), // startTime
				parseDate(cursor.getString(8))); // endTime
	}
	
	public static ContentValues convertGigToContentValues(Gig gig) {
		ContentValues values = new ContentValues();
		//String startTime = (gig.getStartTime() != null) ? DB_DATE_FORMATTER.format(gig.getStartTime()) : null;
		//String endTime = (gig.getEndTime() != null) ? DB_DATE_FORMATTER.format(gig.getEndTime()) : null;
		values.put("id", gig.getId());
		values.put("artist", gig.getArtist());
		values.put("description", gig.getDescription());
		//values.put("stage", gig.getStage());
		//values.put("startTime", startTime);
		//values.put("endTime", endTime);
		values.put("active", gig.isActive());
		values.put("favorite", gig.isFavorite());
		values.put("alerted", gig.isAlerted());
		/*
		if (gig.getFestivalDay() != null) {
			values.put("festivalDay", gig.getFestivalDay().name());
		} else {
			values.put("festivalDay", (String) null);
		}
		*/
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
	
	public static String findNextArtistOnStageMessage(StageType stage, Context context) {
		if (stage == null) {
			return null;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			String now = DB_DATE_FORMATTER.format(CalendarUtil.getNow());
			//cursor = db.query("gig", GIG_COLUMNS, "active = 1 AND stage IS NOT NULL AND startTime IS NOT NULL AND datetime(endTime) > datetime(?)", new String[]{now}, null, null, "startTime ASC");
			cursor = db.rawQuery(GIGS_QUERY + " WHERE gig.active = 1 AND location.stage IS NOT NULL AND location.startTime IS NOT NULL AND datetime(location.endTime) > datetime(?) ORDER BY location.startTime ASC", new String[]{now});
			while (cursor.moveToNext()) {
				String id = cursor.getString(0);
		        Gig gig = convertCursorToGig(cursor, id);
		        GigLocation gigLocation = convertCursorToGigLocation(cursor, id);
		        gig.addLocation(gigLocation);
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
		String stage = gig.getOnlyStage();
		if (stage == null) {
			return null;
		}
		stage = stage.toLowerCase().trim();
		String matchedStage = null;
		switch (stageType) {
		case CONVERSE:
			if (stage.startsWith("converse")) {
				matchedStage = "Converse-lavalla";
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
				matchedStage = "Teltassa";
			}
			break;
		}
		
		return (matchedStage != null) ? context.getString(R.string.mapActivity_nextOnStage, matchedStage, gig.getLocations().get(0).getTime(), gig.getArtist()) : null;
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
	
	public static Integer getImageIdForArtist(String artist) {
		if (StringUtil.isEmpty(artist)) {
			return null;
		}
		artist = artist.toLowerCase().trim();
		artist = artist.replaceAll("\\([a-z]+\\)$", "");
		artist = artist.trim();
		if (artist.startsWith("amorphis")) {
			return R.drawable.artistimg_amorphis;
		}
		if (artist.startsWith("anna calvi")) {
			return R.drawable.artistimg_anna_calvi;
		}
		if (artist.startsWith("anssi 8000")) {
			return R.drawable.artistimg_anssi_8000;
		}
		if (artist.startsWith("apulanta")) {
			return R.drawable.artistimg_apulanta;
		}
		if (artist.startsWith("betty bloomerz")) {
			return R.drawable.artistimg_betty_bloomerz;
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
		if (artist.startsWith("cmx")) {
			return R.drawable.artistimg_cmx;
		}
		if (artist.startsWith("graveyard")) {
			return R.drawable.artistimg_graveyard;
		}
		if (artist.startsWith("elbow")) {
			return R.drawable.artistimg_elbow;
		}
		if (artist.startsWith("felix zenger")) {
			return R.drawable.artistimg_felix_zenger;
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
		if (artist.startsWith("jarkko martikainen")) {
			return R.drawable.artistimg_jarkko_martikainen;
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
		if (artist.startsWith("kuuma ankanpoikanen")) {
			return R.drawable.artistimg_kuuma_ankanpoikanen;
		}
		if (artist.equals("lama")) {
			return R.drawable.artistimg_lama;
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
		if (artist.startsWith("mirel wagner")) {
			return R.drawable.artistimg_mirel_wagner;
		}
		if (artist.startsWith("olavi uusivirta")) {
			return R.drawable.artistimg_olavi_uusivirta;
		}
		if (artist.startsWith("paleface")) {
			return R.drawable.artistimg_paleface;
		}
		if (artist.startsWith("paradise oskar")) {
			return R.drawable.artistimg_paradise_oskar;
		}
		if (artist.startsWith("paramore")) {
			return R.drawable.artistimg_paramore;
		}
		if (artist.startsWith("pariisin kev")) {
			return R.drawable.artistimg_pariisin_kevat;
		}
		if (artist.startsWith("pelle miljoona")) {
			return R.drawable.artistimg_pelle_miljoona;
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
		if (artist.startsWith("the freza")) {
			return R.drawable.artistimg_the_freza;
		}
		if (artist.startsWith("the national")) {
			return R.drawable.artistimg_the_national;
		}
		if (artist.startsWith("the prodigy") || artist.equals("prodigy")) {
			return R.drawable.artistimg_the_prodigy;
		}
		if (artist.startsWith("tuomari nurmio")) {
			return R.drawable.artistimg_tuomari_nurmio;
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
