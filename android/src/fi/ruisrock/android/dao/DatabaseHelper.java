package fi.ruisrock.android.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * DatabaseHelper.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "futurice_ruisrock_db";
	private static final int DB_VERSION = 3;
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
	
	private void createConfigTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS config");
		String sql = "CREATE TABLE IF NOT EXISTS news (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"attributeName VARCHAR(127), " +
				"attributevalue VARCHAR(255))";
		db.execSQL(sql);
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
	
	
}
