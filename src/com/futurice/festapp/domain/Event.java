package com.futurice.festapp.domain;

import java.util.Date;
import java.util.List;

public class Event {
	private String title;
	private int y;
	private Date start_time;
	private Date end_time;
	private String location;
	private List<Artist> artists;
	private String description;
	
	public Event() {
		// TODO Auto-generated constructor stub
	}
	
	public Event(String title, int y, Date start_time, Date end_time,
			String location, List<Artist> artists, String description) {
		this.title = title;
		this.y = y;
		this.start_time = start_time;
		this.end_time = end_time;
		this.location = location;
		this.artists = artists;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<Artist> getArtists() {
		return artists;
	}

	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
