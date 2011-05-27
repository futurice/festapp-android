package fi.ruisrock2011.android.dao;

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
import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.domain.NewsArticle;
import fi.ruisrock2011.android.util.StringUtil;

/**
 * DatabaseHelper.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "ruisrock2011_db";
	private static final int DB_VERSION = 50;
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
			
			createNewsArticlesFromLocalJson(db);
			createGigsFromLocalJson(db);
		} catch (Exception e) {
			Log.e(TAG, "Cannot create DB", e);
			Toast.makeText(context, "Sovelluksen alustus epäonnistui.", Toast.LENGTH_LONG);
		}
	}
	
	private void createNewsTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS news");
		String sql = "CREATE TABLE IF NOT EXISTS news (" +
				"url TEXT PRIMARY KEY, " +
				"title TEXT, " +
				"newsDate DATE," +
				"content TEXT" +
				")";
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
	
	private void createNewsArticlesFromLocalJson(SQLiteDatabase db) throws Exception {
		InputStream jsonStream = context.getResources().openRawResource(R.raw.news);
		String json = StringUtil.convertStreamToString(jsonStream);
		List<NewsArticle> articles = NewsDAO.parseFromJson(json);

		for (NewsArticle article : articles) {
				ContentValues values = NewsDAO.convertNewsArticleToContentValues(article);
				db.insert("news", "content", values);
		}
	}
	
	
	
	private void createGigTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS gig");
		String sql = "CREATE TABLE IF NOT EXISTS gig (" +
				//"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"id TEXT PRIMARY KEY, " +
				"artist TEXT, " +
				"description TEXT, " +
				"startTime DATE, " +
				"endTime DATE, " +
				"festivalDay VARCHAR(63), " +
				"bandImageUrl VARCHAR(511), " +
				"bandLogoUrl VARCHAR(511), " +
				"active BOOLEAN, " +
				"favorite BOOLEAN, " +
				"alerted BOOLEAN, " +
				"stage VARCHAR(255))";
		db.execSQL(sql);
	}
	
	
	private void createConfigTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS config");
		String sql = "CREATE TABLE IF NOT EXISTS config (" +
				//"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"attributeName TEXT PRIMARY KEY, " +
				"attributevalue TEXT)";
		db.execSQL(sql);
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
	
	
}
