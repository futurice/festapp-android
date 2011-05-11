package fi.ruisrock.android.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Data-access operations for Configuration options.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class ConfigDAO {
	
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String TAG = "ConfigDAO";
	
	public static Date getLastUpdatedNews(Context context) {
		String date = getAttributeValue("news_last_updated", context);
		if (date != null) {
			return getDate(date);
		}
		return null;
	}
	
	private static Date getDate(String date) {
		try {
			return DB_DATE_FORMATTER.parse(date);
		} catch (ParseException e) {
			Log.e(TAG, "Unable to parse date from " + date);
		}
		return null;
	}
	
	private static String getAttributeValue(String attributeName, Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			cursor = db.rawQuery("SELECT attributeName, attributeValue FROM config WHERE attributeName = ?", new String[]{attributeName});
			if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				return cursor.getString(1);
			}
		} finally {
			if (db != null) {
				db.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
	

}
