package com.futurice.festapp.domain;

import java.util.Date;

public class News {
	private String title;
	private String image;
	private String teaser_text;
	private String content;
	private Date time;
	private String status;

	public News(){}

	public News(String title, String image, String teaser_text, String content,
			Date time, String status) {
		this.title = title;
		this.image = image;
		this.teaser_text = teaser_text;
		this.content = content;
		this.time = time;
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTeaser_text() {
		return teaser_text;
	}

	public void setTeaser_text(String teaser_text) {
		this.teaser_text = teaser_text;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	};
	
	
}
