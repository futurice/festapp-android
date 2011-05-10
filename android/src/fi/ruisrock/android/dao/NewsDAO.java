package fi.ruisrock.android.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.ruisrock.android.domain.NewsArticle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NewsDAO {
	
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final String TAG = "NewsDAO";
	
	public static List<NewsArticle> findAll(Context context) {
		List<NewsArticle> articles = new ArrayList<NewsArticle>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			cursor = db.rawQuery("SELECT _id, title, newsDate, url FROM news", new String[]{});
			while (cursor.moveToNext()) {
		        long id = cursor.getLong(0);
		        String title = cursor.getString(1);
		        Date date = getDate(cursor.getString(2));
		        String url = cursor.getString(3);
		        articles.add(new NewsArticle(id, title, date, url));
			}
		} finally {
			if (db != null) {
				db.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return articles;
	}
	
	public static void replaceAll(Context context, List<NewsArticle> articles) {
		if (articles == null || articles.size() < 1) {
			return;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			db.beginTransaction();
			int deletedArticles = db.delete("news", null, null);
			for (NewsArticle article : articles) {
				db.insert("news", "title", convertNewsArticleToContentValues(article));
			}
			Log.i(TAG, String.format("Successfully replaced %d NewsArticles with %d", deletedArticles, articles.size()));
			db.endTransaction();
		} finally {
			if (db != null) {
				db.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	private static ContentValues convertNewsArticleToContentValues(NewsArticle article) {
		ContentValues values = new ContentValues();
		values.put("title", article.getTitle());
		values.put("newsDate", DB_DATE_FORMATTER.format(article.getDate()));
		values.put("url", article.getUrl());
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

}
