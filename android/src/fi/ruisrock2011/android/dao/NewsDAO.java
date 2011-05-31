package fi.ruisrock2011.android.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import fi.ruisrock2011.android.domain.NewsArticle;
import fi.ruisrock2011.android.domain.to.HTTPBackendResponse;
import fi.ruisrock2011.android.util.HTTPUtil;
import fi.ruisrock2011.android.util.RuisrockConstants;

/**
 * DAO for News-articles.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class NewsDAO {
	
	private static DateFormat RSS_DATE_FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final String TAG = "NewsDAO";
	private static final String[] NEWS_COLUMNS = { "url", "title", "date", "content" };
	
	public static List<NewsArticle> findAll(Context context) {
		List<NewsArticle> articles = new ArrayList<NewsArticle>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			cursor = db.rawQuery("SELECT url, title, newsDate, content FROM news ORDER BY newsDate DESC", new String[]{});
			while (cursor.moveToNext()) {
				String url = cursor.getString(0);
		        String title = cursor.getString(1);
		        Date date = getDate(cursor.getString(2));
		        String content = cursor.getString(3);
		        articles.add(new NewsArticle(url, title, date, content));
			}
		} finally {
			closeDb(db, cursor);
		}
		return articles;
	}
	
	/*
	public static int deleteAll(Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			int deletedArticles = db.delete("news", null, null);
			return deletedArticles;
		} finally {
			if (db != null) {
				db.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	*/
	/*
	public static void replaceAll(Context context, List<NewsArticle> articles) {
		if (articles == null || articles.size() < 1) {
			return;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			int deletedArticles = db.delete("news", null, null);
			for (NewsArticle article : articles) {
				db.insert("news", "date", convertNewsArticleToContentValues(article));
			}
			Log.i(TAG, String.format("Successfully replaced %d NewsArticles with %d", deletedArticles, articles.size()));
		} finally {
			if (db != null) {
				db.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	*/
	
	public static ContentValues convertNewsArticleToContentValues(NewsArticle article) {
		ContentValues values = new ContentValues();
		values.put("url", article.getUrl());
		values.put("title", article.getTitle());
		values.put("newsDate", DB_DATE_FORMATTER.format(article.getDate()));
		values.put("content", article.getContent());
		return values;
	}
	
	private static Date getDate(String date) {
		try {
			return DB_DATE_FORMATTER.parse(date);
		} catch (ParseException e) {
			Log.e(TAG, "Unable to parse date from " + date);
		}
		return null;
	}

	public static void updateNewsOverHttp(Context context) {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(RuisrockConstants.NEWS_JSON_URL);
		if (!response.isValid() || response.getContent() == null) {
			return;
		}
		ConfigDAO.setEtagForGigs(context, response.getEtag());
		try {
			List<NewsArticle> articles = parseFromJson(response.getContent());
			
			if (articles != null && articles.size() > 0) { // Hackish fail-safe
				SQLiteDatabase db = null;
				Cursor cursor = null;
				try {
					db = (new DatabaseHelper(context)).getWritableDatabase();
					db.beginTransaction();
					
					int inserted = 0, updated = 0;
					for (NewsArticle article : articles) {
						NewsArticle existingArticle = findNewsArticle(db, article.getUrl());
						if (existingArticle != null) {
							db.update("news", convertNewsArticleToContentValues(article), "url = ?", new String[] {article.getUrl()});
							updated++;
						} else {
							db.insert("gig", "content", convertNewsArticleToContentValues(article));
							inserted++;
						}
					}
					db.setTransactionSuccessful();
					Log.i(TAG, String.format("Successfully updated NewsArticles via HTTP. Result {received: %d, updated: %d, added: %d", articles.size(), updated, inserted));
				} finally {
					db.endTransaction();
					closeDb(db, cursor);
				}
			}
			
		} catch (Exception e) {
			Log.w(TAG, "Received invalid JSON-structure", e);
		}
	}
	
	private static NewsArticle findNewsArticle(SQLiteDatabase db, String url) {
		Cursor cursor = db.query("news", NEWS_COLUMNS, "url = " + url, null, null, null, null);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			return convertCursorToNewsArticle(cursor);
		}
		return null;
	}
	
	private static NewsArticle convertCursorToNewsArticle(Cursor cursor) {
		return new NewsArticle(
				cursor.getString(0),
				cursor.getString(1),
				getDate(cursor.getString(2)),
				cursor.getString(3));
	}
	
	public static List<NewsArticle> parseFromJson(String json) throws Exception {
		List<NewsArticle> articles = new ArrayList<NewsArticle>();
		JSONArray list = new JSONArray(json);
		for (int i=0; i < list.length(); i++) {
			try {
				JSONObject newsObject = list.getJSONObject(i);
				Date date = RSS_DATE_FORMATTER.parse(newsObject.getString("pubDate"));
				NewsArticle article = new NewsArticle(newsObject.getString("link"), newsObject.getString("title"), date, newsObject.getString("content"));
				articles.add(article);
			} catch (Exception e) {
				Log.w(TAG, "Received invalid JSON-structure", e);
			}
		}
		
		return articles;
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
