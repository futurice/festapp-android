package fi.ruisrock.android.dao;

import java.io.InputStream;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import fi.ruisrock.android.R;
import fi.ruisrock.android.domain.Gig;

/**
 * DatabaseHelper.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "futurice_ruisrock_db";
	private static final int DB_VERSION = 32;
	private static final String TAG = "DatabaseHelper";
	
	private Context context;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			createNewsTable(db);
			createConfigTable(db);
			createGigTable(db);
			createGigsFromLocalJson(db);
		} catch (Exception e) {
			Log.e(TAG, "Cannot create DB", e);
			Toast.makeText(context, "Sovelluksen alustus epäonnistui.", Toast.LENGTH_LONG);
		}
	}
	
	private void createNewsTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS news");
		String sql = "CREATE TABLE IF NOT EXISTS news (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"title VARCHAR(255), " +
				"url VARCHAR(1023) UNIQUE, " +
				"newsDate DATE)";
		db.execSQL(sql);
	}
	
	private void createGigsFromLocalJson(SQLiteDatabase db) throws Exception {
		InputStream jsonStream = context.getResources().openRawResource(R.raw.gigs);
		List<Gig> gigs = new ObjectMapper().readValue(jsonStream, new TypeReference<List<Gig>>() {});

		for (Gig gig : gigs) {
			if (GigDAO.isValidGig(gig)) {
				ContentValues values = GigDAO.convertGigToContentValues(gig);
				db.insert("gig", "date", values);
			}
		}
	}
	
	
	
	private void createGigTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS gig");
		String sql = "CREATE TABLE IF NOT EXISTS gig (" +
				//"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"id VARCHAR(255) PRIMARY KEY, " +
				"artist VARCHAR(255), " +
				"description TEXT, " +
				"startTime DATE, " +
				"endTime DATE, " +
				"festivalDay VARCHAR(63), " +
				"bandImageUrl VARCHAR(511), " +
				"bandLogoUrl VARCHAR(511), " +
				"active BOOLEAN, " +
				"favorite BOOLEAN, " +
				"stage VARCHAR(255))";
		db.execSQL(sql);
	}
	
	
	private void createConfigTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS config");
		String sql = "CREATE TABLE IF NOT EXISTS config (" +
				//"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"attributeName VARCHAR(127) PRIMARY KEY, " +
				"attributevalue VARCHAR(255))";
		db.execSQL(sql);
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
	
	
}
