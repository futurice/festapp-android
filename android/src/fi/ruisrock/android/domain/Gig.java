package fi.ruisrock.android.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import fi.ruisrock.android.util.CalendarUtil;
import fi.ruisrock.android.util.TimezonelessDeserializer;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Gig {
	
	private static final SimpleDateFormat sdfHoursAndMinutes = new SimpleDateFormat("HH:mm");

	private String id;
	private String artist;
	private String description;
	private Date startTime;
	private Date endTime;
	private String stage;
	private String bandImageUrl;
	private String bandLogoUrl;
	
	private boolean favorite;
	private boolean active = true;
	
	public Gig() {
		
	}

	public Gig(String id, String artist, String description, Date startTime, Date endTime, String stage,
			String bandImageUrl, String bandLogoUrl, boolean favorite, boolean active) {
		this.id = id;
		this.artist = artist;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.stage = stage;
		this.bandImageUrl = bandImageUrl;
		this.bandLogoUrl = bandLogoUrl;
		this.favorite = favorite;
		this.active = active;
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

	@JsonSetter("name")
	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}

	@JsonSetter("start") @JsonDeserialize(using=TimezonelessDeserializer.class)
	@JsonIgnore
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	@JsonSetter("end") @JsonDeserialize(using=TimezonelessDeserializer.class)
	@JsonIgnore
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
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

	public String getBandImageUrl() {
		return bandImageUrl;
	}

	@JsonSetter("band_image")
	public void setBandImageUrl(String bandImageUrl) {
		this.bandImageUrl = bandImageUrl;
	}

	public String getBandLogoUrl() {
		return bandLogoUrl;
	}

	@JsonSetter("band_logo")
	public void setBandLogoUrl(String bandLogoUrl) {
		this.bandLogoUrl = bandLogoUrl;
	}

	@Override
	public String toString() {
		return String.format("Gig {id: %s, artist: %s}", id, artist);
	}
	
	public String getStageAndTime() {
		String time = getStartDay().substring(0, 2) + " klo " + sdfHoursAndMinutes.format(startTime) + " - " + sdfHoursAndMinutes.format(endTime);
		return String.format("%s: %s", stage, time);
	}
	
	public String getStartDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		return CalendarUtil.getFullWeekdayName(cal.get(Calendar.DAY_OF_WEEK));
	}

}
