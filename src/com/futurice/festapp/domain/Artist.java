package com.futurice.festapp.domain;

import java.util.List;

public class Artist {
	private List<String> albums;
	private String contant_info;
	private String content;
	private String credits;
	private boolean featured;
	private int founded;
	private List<String> genres;
	private List<String> highlights;
	private List<String> members;
	private String name;
	private String picture;
	private int place;
	private String press_image;
	private String quote;
	private String spotify;
	private String status;
	private String youtube;

	public Artist() {
		
	}
	
	public Artist(List<String> albums, String contant_info, String content,
			String credits, boolean featured, int founded, List<String> genres,
			List<String> highlights, List<String> members, String name,
			String picture, int place, String press_image, String quote,
			String spotify, String status, String youtube) {
		this.albums = albums;
		this.contant_info = contant_info;
		this.content = content;
		this.credits = credits;
		this.featured = featured;
		this.founded = founded;
		this.genres = genres;
		this.highlights = highlights;
		this.members = members;
		this.name = name;
		this.picture = picture;
		this.place = place;
		this.press_image = press_image;
		this.quote = quote;
		this.spotify = spotify;
		this.status = status;
		this.youtube = youtube;
	}
	
	public List<String> getAlbums() {
		return albums;
	}
	public void setAlbums(List<String> albums) {
		this.albums = albums;
	}
	public String getContant_info() {
		return contant_info;
	}
	public void setContant_info(String contant_info) {
		this.contant_info = contant_info;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCredits() {
		return credits;
	}
	public void setCredits(String credits) {
		this.credits = credits;
	}
	public boolean isFeatured() {
		return featured;
	}
	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
	public int getFounded() {
		return founded;
	}
	public void setFounded(int founded) {
		this.founded = founded;
	}
	public List<String> getGenres() {
		return genres;
	}
	public void setGenres(List<String> genres) {
		this.genres = genres;
	}
	public List<String> getHighlights() {
		return highlights;
	}
	public void setHighlights(List<String> highlights) {
		this.highlights = highlights;
	}
	public List<String> getMembers() {
		return members;
	}
	public void setMembers(List<String> members) {
		this.members = members;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public int getPlace() {
		return place;
	}
	public void setPlace(int place) {
		this.place = place;
	}
	public String getPress_image() {
		return press_image;
	}
	public void setPress_image(String press_image) {
		this.press_image = press_image;
	}
	public String getQuote() {
		return quote;
	}
	public void setQuote(String quote) {
		this.quote = quote;
	}
	public String getSpotify() {
		return spotify;
	}
	public void setSpotify(String spotify) {
		this.spotify = spotify;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getYoutube() {
		return youtube;
	}
	public void setYoutube(String youtube) {
		this.youtube = youtube;
	}
	
	
	
}

