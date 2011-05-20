package fi.ruisrock2011.android.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import android.os.Parcel;
import android.os.Parcelable;

import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.domain.to.FestivalDay;
import fi.ruisrock2011.android.util.CalendarUtil;
import fi.ruisrock2011.android.util.TimezonelessDeserializer;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Gig implements Parcelable {
	
	private static final SimpleDateFormat sdfHoursAndMinutes = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat parcelableDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
	private boolean alerted = false;
	
	public Gig() {
		
	}

	public Gig(String id, String artist, String description, Date startTime, Date endTime, String stage,
			String bandImageUrl, String bandLogoUrl, boolean favorite, boolean active, boolean alerted) {
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
		this.alerted = alerted;
	}

	public Gig(Parcel in) {
		String[] data = new String[3];
		in.readStringArray(data);
		this.id = data[0];
		this.artist = data[1];
		this.stage = data[2];
		this.startTime = getDateFromParcelable(data[3]);
		this.endTime = getDateFromParcelable(data[4]);
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
	//@JsonIgnore
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	
	public Integer getDuration() {
		if (startTime == null || endTime == null) {
			return null;
		}
		return CalendarUtil.getMinutesBetweenTwoDates(startTime, endTime);
	}

	@JsonSetter("end") @JsonDeserialize(using=TimezonelessDeserializer.class)
	//@JsonIgnore
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

	public void setAlerted(boolean alerted) {
		this.alerted = alerted;
	}

	public boolean isAlerted() {
		return alerted;
	}

	@Override
	public String toString() {
		return String.format("Gig {id: %s, artist: %s, stage: %s, startTime: %s}", id, artist, stage, startTime);
	}
	
	public String getStageAndTime() {
		String stage = (this.stage != null) ? this.stage : "";
		return String.format("%s, %s", stage, getTime());
	}
	
	public String getTime() {
		return (startTime != null && endTime != null) ? getStartDay().substring(0, 2).toLowerCase() + " klo " + sdfHoursAndMinutes.format(startTime) + " - " + sdfHoursAndMinutes.format(endTime) : "";
	}
	
	public String getStartDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		return CalendarUtil.getFullWeekdayName(cal.get(Calendar.DAY_OF_WEEK));
	}
	
	public FestivalDay getFestivalDay() {
		if (startTime == null || endTime == null) {
			return null;
		}
		return GigDAO.getFestivalDay(startTime);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {this.id, this.artist, this.stage, getParcelableDateString(startTime), getParcelableDateString(endTime)});
	}
	
	private String getParcelableDateString(Date date) {
		try {
			return parcelableDateFormat.format(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	private Date getDateFromParcelable(String str) {
		try {
			return parcelableDateFormat.parse(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		public Gig createFromParcel(Parcel in) {
			return new Gig(in);
		}

		public Gig[] newArray(int size) {
			return new Gig[size];
		}

	};
	

}
