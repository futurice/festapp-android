package fi.ruisrock.android.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import fi.ruisrock.android.domain.Gig;
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
	
	
	public static List<Gig> findAllActive(Context context) {
		List<Gig> gigs = new ArrayList<Gig>();
		
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			cursor = db.query("gig", GIG_COLUMNS, "active = 1", null, null, null, "artist ASC");
			while (cursor.moveToNext()) {
		        gigs.add(cursorToGig(cursor, cursor.getString(0)));
			}
		} finally {
			closeDb(db, cursor);
		}
		return gigs;
	}
	
	public static void updateGigsOverHttp(Context context) throws Exception {
		HTTPUtil httpUtil = new HTTPUtil();
		String gigsJson = httpUtil.performGet(RuisrockConstants.GIGS_JSON_URL, null, null, null);
		List<Gig> gigs = new ObjectMapper().readValue(gigsJson, new TypeReference<List<Gig>>() {});
		
		if (gigs != null && gigs.size() > 5) { // Hackish fail-safe
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = (new DatabaseHelper(context)).getWritableDatabase();
				db.beginTransaction();
				db.rawQuery("UPDATE gig SET active = false", null);
				
				for (Gig gig : gigs) {
					if (isValidGig(gig)) {
						Gig existingGig = findGig(db, gig.getId());
						if (existingGig != null) {
							gig.setFavorite(existingGig.isFavorite());
							db.update("gig", convertGigToContentValues(gig), "id = ?", new String[] {gig.getId()});
						} else {
							db.insert("gig", "bandLogoUrl", convertGigToContentValues(gig));
						}
					}
				}
				db.setTransactionSuccessful();
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
			return cursorToGig(cursor, id);
		}
		return null;
	}
	
	private static Gig cursorToGig(Cursor cursor, String id) {
		Date startTime = null, endTime = null;
		try {
			startTime = DB_DATE_FORMATTER.parse(cursor.getString(3));
			endTime = DB_DATE_FORMATTER.parse(cursor.getString(4));
		} catch (ParseException e) {
			Log.e(TAG, String.format("Cannot parse dates for Gig {id: %s}", id));
			return null;
		}
		
		return new Gig(cursor.getString(0),
				cursor.getString(1),
				cursor.getString(2),
				startTime,
				endTime,
				cursor.getString(5),
				cursor.getString(6),
				cursor.getString(7),
				cursor.getInt(8) > 0,
				cursor.getInt(9) > 0);
	}
	
	public static ContentValues convertGigToContentValues(Gig gig) {
		ContentValues values = new ContentValues();
		values.put("id", gig.getId());
		values.put("artist", gig.getArtist());
		values.put("bandImageUrl", gig.getBandImageUrl());
		values.put("bandLogoUrl", gig.getBandLogoUrl());
		values.put("description", gig.getDescription());
		values.put("stage", gig.getStage());
		values.put("startTime", DB_DATE_FORMATTER.format(gig.getStartTime()));
		values.put("endTime", DB_DATE_FORMATTER.format(gig.getEndTime()));
		values.put("active", gig.isActive());
		values.put("favorite", gig.isFavorite());
		return values;
	}

	public static void insertOrUpdate(Context context, Gig gig) {
		
	}
	
	
	public static boolean isValidGig(Gig gig) {
		if (gig == null || StringUtil.isEmpty(gig.getId()) || StringUtil.isEmpty(gig.getArtist())) {
			return false;
		}
		if (gig.getStartTime() == null || gig.getEndTime() == null) {
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
	
	private static void closeDb(SQLiteDatabase db, Cursor cursor) {
		if (db != null) {
			db.close();
		}
		if (cursor != null) {
			cursor.close();
		}
	}
	
}
