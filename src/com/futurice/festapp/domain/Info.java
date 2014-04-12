package com.futurice.festapp.domain;

public class Info {
	private String title;
	private String image;
	private String content;
	private int place;
	
	public Info(){};
	
	public Info(String title, String image, String content, int place) {
		this.title = title;
		this.image = image;
		this.content = content;
		this.place = place;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}
	
	
	
}
