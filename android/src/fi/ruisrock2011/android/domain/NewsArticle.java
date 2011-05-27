package fi.ruisrock2011.android.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import fi.ruisrock2011.android.rss.RSSItem;
import fi.ruisrock2011.android.rss.RSSReader;

public class NewsArticle {
	
	private String url;
	private String title;
	private Date date;
	private String content;

	public NewsArticle() {

	}

	public NewsArticle(String url, String title, Date date, String content) {
		this.url = url;
		this.title = title;
		this.date = date;
		this.content = content;
	}

	/*
	public NewsArticle(RSSItem rssItem) {
		title = rssItem.getTitle();
		url = rssItem.getLink();
		try {
			date = RSSReader.RSS_DATE_FORMATTER.parse(rssItem.getPubDate());
		} catch (ParseException e) {
			Log.w("NewsArticle", "Unable to parse pubDate " + rssItem.getPubDate());
		}
	}
	*/

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	public String getDateString() {
		try {
			return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date);
		} catch (Exception e) {
			return "-";
		}
	}
	
	@Override
	public String toString() {
		return String.format("NewsArticle {url=%s, title=%s}", url, title);
	}

}
