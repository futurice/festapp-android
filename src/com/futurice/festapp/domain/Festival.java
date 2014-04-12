package com.futurice.festapp.domain;

import java.util.Date;
import java.util.List;

public class Festival {
	private String name;
	private String organizer;
	private Date start_date;
	private Date end_date;
	
	private List<String> sponsors;
	
	private String city;
	private String country;
	private Coordinates coordinates;
	
	public Festival() {
		// TODO Auto-generated constructor stub
	}
	
	public Festival(String name, String organizer, Date start_date,
			Date end_date, List<String> sponsors, String city, String country,
			Coordinates coordinates) {
		super();
		this.name = name;
		this.organizer = organizer;
		this.start_date = start_date;
		this.end_date = end_date;
		this.sponsors = sponsors;
		this.city = city;
		this.country = country;
		this.coordinates = coordinates;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrganizer() {
		return organizer;
	}
	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public Date getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	public List<String> getSponsors() {
		return sponsors;
	}
	public void setSponsors(List<String> sponsors) {
		this.sponsors = sponsors;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Coordinates getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	
}
