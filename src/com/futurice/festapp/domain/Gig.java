package com.futurice.festapp.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Gig {

	private String id;
	private String artist;
	private String description;
	private boolean favorite;
	private boolean active = true;
	private boolean alerted = false;
	private String youtube;
	private String spotify;
	private String artistImage;
	
	private List<GigLocation> locations = new ArrayList<GigLocation>();
	
	public Gig() {
		
	}

	public Gig(String id, String artist, String description, boolean favorite, boolean active, boolean alerted, String youtube, String spotify,String artistImage) {
		this.id = id;
		this.artist = artist;
		this.description = description;
		this.favorite = favorite;
		this.active = active;
		this.alerted = alerted;
		this.youtube = youtube;
		this.spotify = spotify;
		this.artistImage = artistImage;
	}
	
	public List<GigLocation> getLocations() {
		return locations;
	}
	
	public void setLocations(List<GigLocation> locations) {
		this.locations = locations;
	}
	
	public void addLocation(GigLocation location) {
		locations.add(location);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getArtistImage() {
		return artistImage;
	}
	
	public void setArtistImage(String artistImage) {
		this.artistImage = artistImage;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setAlerted(boolean alerted) {
		this.alerted = alerted;
	}

	public boolean isAlerted() {
		return alerted;
	}

	@Override
	public String toString() {
		return String.format("Gig {id: %s, artist: %s}", id, artist);
	}
	
	public GigLocation getOnlyLocation(){
		if (locations.size() == 1) {
			return locations.get(0);
		}
		return null;
	}
	
	public String getYoutube() {
		return youtube;
	}
	
	public void setYoutube(String url) {
		this.youtube = url;
	}
	
	public String getSpotify() {
		return spotify;
	}
	
	public void setSpotify(String url) {
		this.spotify = url;
	}
}
