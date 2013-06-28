package fi.ruisrock.android.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

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
