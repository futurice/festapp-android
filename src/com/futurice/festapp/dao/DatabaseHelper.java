package com.futurice.festapp.dao;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.futurice.festapp.domain.Gig;
import com.futurice.festapp.domain.NewsArticle;
import com.futurice.festapp.util.FestAppConstants;
import com.futurice.festapp.util.StringUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import com.futurice.festapp.R;

/**
 * DatabaseHelper.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "festapp_db";
	private static final int DB_VERSION = 1; // Latest release at the market: 1
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
			createGigLocationTable(db);
			
			createNewsArticlesFromLocalJson(db);
			createGigsFromLocalJson(db);
			createFoodAndDrinkPageFromLocalFile(db);
			createTransportationPageFromLocalFile(db);
			createServicePagesFromLocalFile(db);
			createFrequentlyAskedQuestionsPagesFromLocalFile(db);
			createGeneralInfoPagesFromLocalFile(db);
		} catch (Exception e) {
			Log.e(TAG, "Cannot create DB", e);
			Toast.makeText(context, context.getString(R.string.database_fail), Toast.LENGTH_LONG).show();
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
		List<Gig> gigs = GigDAO.parseFromJson(StringUtil.convertStreamToString(jsonStream));
		for (Gig gig : gigs) {
			if (GigDAO.isValidGig(gig)) {
				ContentValues values = GigDAO.convertGigToContentValues(gig);
				db.insert("gig", "description", values);
				
				for (ContentValues cv : GigDAO.convertGigToLocationContentValues(gig)) {
					db.insert("location", null, cv);
				}
			}
		}
		ContentValues values = ConfigDAO.createConfigContentValues(ConfigDAO.ATTR_ETAG_FOR_GIGS, FestAppConstants.LAST_MODIFIED_GIGS);
		db.insert("config", "attributeValue", values);
	}
	
	private void createNewsArticlesFromLocalJson(SQLiteDatabase db) throws Exception {
		InputStream jsonStream = context.getResources().openRawResource(R.raw.news);
		String json = StringUtil.convertStreamToString(jsonStream);
		List<NewsArticle> articles = NewsDAO.parseFromJson(json);

		for (NewsArticle article : articles) {
			ContentValues values = NewsDAO.convertNewsArticleToContentValues(article);
			db.insert("news", "content", values);
		}
		ContentValues values = ConfigDAO.createConfigContentValues(ConfigDAO.ATTR_ETAG_FOR_NEWS, FestAppConstants.LAST_MODIFIED_NEWS);
		db.insert("config", "attributeValue", values);
	}
	
	private void createFoodAndDrinkPageFromLocalFile(SQLiteDatabase db) throws Exception {
		InputStream is = context.getResources().openRawResource(R.raw.page_foodanddrink);
		String page = StringUtil.convertStreamToString(is);
		
		// Ugly hacks to content_plaintext
		page = page.replace("\\u2028", "").replace("\\u017e", "��");
		while(page.contains("\\r\\n\\r\\n")) {
			page = page.replace("\\r\\n\\r\\n", "\\r\\n");
		}
		
		ContentValues values = ConfigDAO.createConfigContentValues(ConfigDAO.ATTR_PAGE_FOODANDDRINK, "<p>" + page.replace("\\r\\n", "<br /><br />") + "</ p>");
		db.insert("config", "attributeValue", values);
		
		values = ConfigDAO.createConfigContentValues(ConfigDAO.ATTR_ETAG_FOR_FOODANDDRINK, FestAppConstants.LAST_MODIFIED_FOOD_AND_DRINK);
		db.insert("config", "attributeValue", values);
	}
	
	private void createTransportationPageFromLocalFile(SQLiteDatabase db) throws Exception {
		InputStream is = context.getResources().openRawResource(R.raw.page_transportation);
		String page = StringUtil.convertStreamToString(is);
		ContentValues values = ConfigDAO.createConfigContentValues(ConfigDAO.ATTR_PAGE_TRANSPORTATION, page);
		db.insert("config", "attributeValue", values);
//		db.insert("config", "attributeValue", ConfigDAO.createConfigContentValues(ConfigDAO.ATTR_ETAG_FOR_TRANSPORTATION, FestAppConstants.ETAG_TRANSPORTATION));
	}
	
	private void createServicePagesFromLocalFile(SQLiteDatabase db) throws Exception {
		InputStream is = context.getResources().openRawResource(R.raw.services);
		String json = StringUtil.convertStreamToString(is);
		
		Map<String, String> services = ConfigDAO.parseServicesMapFromJson(context, json);
		for (Map.Entry<String, String> entry : services.entrySet()) {
			db.insert("config", "attributeValue", convertMapEntryToContentValues(entry));
		}
//		db.insert("config", "attributeValue", ConfigDAO.createConfigContentValues(ConfigDAO.ATTR_ETAG_FOR_SERVICES, FestAppConstants.ETAG_SERVICES));
	}
	
	private void createFrequentlyAskedQuestionsPagesFromLocalFile(SQLiteDatabase db) throws Exception {
		InputStream is = context.getResources().openRawResource(R.raw.faq);
		String json = StringUtil.convertStreamToString(is);
		ContentValues values = ConfigDAO.createConfigContentValues(ConfigDAO.ATTR_PAGE_GENERALINFO_FREQUENTLY_ASKED, ConfigDAO.parseFromJson(json, "content"));
		db.insert("config", "attributeValue", values);
		db.insert("config", "attributeValue", ConfigDAO.createConfigContentValues(ConfigDAO.ATTR_ETAG_FOR_FREQUENTLY_ASKED_QUESTIONS, FestAppConstants.LAST_MODIFIED_FAQ));	
	}
	
	private void createGeneralInfoPagesFromLocalFile(SQLiteDatabase db) throws Exception {
		InputStream is = context.getResources().openRawResource(R.raw.general_info);
		String json = StringUtil.convertStreamToString(is);
		
		Map<String, String> services = ConfigDAO.parseGeneralInfoMapFromJson(context, json);
		for (Map.Entry<String, String> entry : services.entrySet()) {
			db.insert("config", "attributeValue", convertMapEntryToContentValues(entry));
		}
	}
	
	private ContentValues convertMapEntryToContentValues(Map.Entry<String, String> entry) {
		String attributeName = entry.getKey();
		String attributeValue = entry.getValue();
		return ConfigDAO.createConfigContentValues(attributeName, attributeValue);
	}
	
	
	private void createGigTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS gig");
		String sql = "CREATE TABLE IF NOT EXISTS gig (" +
				"id TEXT PRIMARY KEY, " +
				"artist TEXT, " +
				"description TEXT, " +
				"active BOOLEAN, " +
				"favorite BOOLEAN, " +
				"alerted BOOLEAN, " +
				"youtube TEXT, " +
				"spotify TEXT)";
		db.execSQL(sql);
	}
	
	
	private void createGigLocationTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS location");
		String sql = "CREATE TABLE IF NOT EXISTS location (" +
				"id TEXT NOT NULL, " +
				"startTime DATE, " +
				"endTime DATE, " +
				"festivalDay VARCHAR(63), " +
				"stage VARCHAR(255))";
		db.execSQL(sql);
	}
	
	private void createDateTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS festDate");
		String sql = "CREATE TABLE IF NOT EXISTS festDate (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT, "+
				"festDate DATE";
		db.execSQL(sql);
	}
	
	
	private void createConfigTable(SQLiteDatabase db) throws Exception {
		db.execSQL("DROP TABLE IF EXISTS config");
		String sql = "CREATE TABLE IF NOT EXISTS config (" +
				//"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"attributeName TEXT PRIMARY KEY, " +
				"attributeValue TEXT)";
		db.execSQL(sql);
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
	
	
}
