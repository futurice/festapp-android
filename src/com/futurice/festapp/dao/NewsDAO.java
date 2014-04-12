package com.futurice.festapp.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.futurice.festapp.domain.NewsArticle;
import com.futurice.festapp.domain.to.HTTPBackendResponse;
import com.futurice.festapp.util.HTTPUtil;
import com.futurice.festapp.util.JSONUtil;
import com.futurice.festapp.util.FestAppConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DAO for News-articles.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class NewsDAO {
	
	private static DateFormat RSS_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final String TAG = "NewsDAO";
	private static final String[] NEWS_COLUMNS = { "url", "title", "newsDate", "content" };
	private static final int URL = 0;
	private static final int TITLE = 1;
	private static final int NEWS_DATE = 2;
	private static final int CONTENT = 3;
	
	public static List<NewsArticle> findAll(Context context) {
		List<NewsArticle> articles = new ArrayList<NewsArticle>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			cursor = db.rawQuery("SELECT url, title, newsDate, content FROM news ORDER BY newsDate DESC", new String[]{});
			while (cursor.moveToNext()) {
				String url = cursor.getString(URL);
		        String title = cursor.getString(TITLE);
		        Date date = getDate(cursor.getString(NEWS_DATE));
		        String content = cursor.getString(CONTENT);
		        articles.add(new NewsArticle(url, title, date, content));
			}
		} finally {
			closeDb(db, cursor);
		}
		return articles;
	}
	
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
		} catch (Exception e) {
			Log.e(TAG, "Unable to parse date from " + date);
		}
		return null;
	}

	public static List<NewsArticle> updateNewsOverHttp(Context context) {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.NEWS_JSON_URL);
		if (!response.isValid() || response.getContent() == null) {
			return null;
		}
		ConfigDAO.setAttributeValue(ConfigDAO.ATTR_ETAG_FOR_GIGS, response.getEtag(), context);
		try {
			List<NewsArticle> articles = parseFromJson(response.getContent());
			
			if (articles != null && articles.size() > 0) { // Hackish fail-safe
				SQLiteDatabase db = null;
				Cursor cursor = null;
				List<NewsArticle> newArticles = new ArrayList<NewsArticle>();
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
							db.insert("news", "content", convertNewsArticleToContentValues(article));
							inserted++;
							newArticles.add(article);
						}
					}
					db.setTransactionSuccessful();
					Log.i(TAG, String.format("Successfully updated NewsArticles via HTTP. Result {received: %d, updated: %d, added: %d", articles.size(), updated, inserted));
				} finally {
					db.endTransaction();
					closeDb(db, cursor);
				}
				return newArticles;
			}
			
		} catch (Exception e) {
			Log.w(TAG, "Received invalid JSON-structure", e);
		}
		return null;
	}
	
	private static NewsArticle findNewsArticle(SQLiteDatabase db, String url) {
		Cursor cursor = db.query("news", NEWS_COLUMNS, "url = ?", new String[]{url}, null, null, null);
		NewsArticle article = null;
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			article = convertCursorToNewsArticle(cursor);
		}
		if (cursor != null) {
			cursor.close();
		}
		return article;
	}
	
	public static NewsArticle findNewsArticle(Context context, String url) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			return findNewsArticle(db, url);
		} finally {
			closeDb(db, cursor);
		}
	}
	
	private static NewsArticle convertCursorToNewsArticle(Cursor cursor) {
		return new NewsArticle(
				cursor.getString(URL),
				cursor.getString(TITLE),
				getDate(cursor.getString(NEWS_DATE)),
				cursor.getString(CONTENT));
	}
	
	public static List<NewsArticle> parseFromJson(String json) throws Exception {
		List<NewsArticle> articles = new ArrayList<NewsArticle>();
		JSONArray list = new JSONArray(json);
		for (int i=0; i < list.length(); i++) {
			try {
				JSONObject newsObject = list.getJSONObject(i);
				Date date = new Date(Long.valueOf(JSONUtil.getString(newsObject, "time")) * 1000);
				String titleString = JSONUtil.getString(newsObject, "title");
				String dateString = RSS_DATE_FORMATTER.format(date);
				NewsArticle article = new NewsArticle(titleString + dateString, titleString, date, "<p>" + JSONUtil.getString(newsObject, "content").replace("  ", "<br /><br />") + "</ p>");
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
