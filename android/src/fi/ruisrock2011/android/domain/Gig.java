package fi.ruisrock2011.android.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.domain.to.FestivalDay;
import fi.ruisrock2011.android.util.CalendarUtil;
import fi.ruisrock2011.android.util.StringUtil;

public class Gig {
	
	private static final SimpleDateFormat sdfHoursAndMinutes = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat parcelableDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private String id;
	private String artist;
	private String description;
	private Date startTime;
	private Date endTime;
	private String stage;
	private Integer imageId;
	
	private boolean favorite;
	private boolean active = true;
	private boolean alerted = false;
	
	public Gig() {
		
	}

	public Gig(String id, Integer imageId, String artist, String description, Date startTime, Date endTime, String stage,
			boolean favorite, boolean active, boolean alerted) {
		this.id = id;
		this.imageId = imageId;
		this.artist = artist;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.stage = stage;
		this.favorite = favorite;
		this.active = active;
		this.alerted = alerted;
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

	public Date getStartTime() {
		return startTime;
	}

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

	public void setAlerted(boolean alerted) {
		this.alerted = alerted;
	}

	public boolean isAlerted() {
		return alerted;
	}
	
	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public Integer getImageId() {
		return imageId;
	}

	@Override
	public String toString() {
		return String.format("Gig {id: %s, artist: %s, stage: %s, startTime: %s}", id, artist, stage, startTime);
	}
	
	public String getStageAndTime() {
		String stage = (this.stage != null) ? this.stage : "";
		String time = getTime();
		
		if (StringUtil.isNotEmpty(stage) && StringUtil.isNotEmpty(time)) {
			return String.format("%s, %s", stage, time);
		} else if (StringUtil.isNotEmpty(stage)) {
			return stage;
		} else if (StringUtil.isNotEmpty(time)) {
			return time;
		}
		return "";
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

}
