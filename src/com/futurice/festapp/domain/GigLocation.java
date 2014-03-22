package com.futurice.festapp.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.futurice.festapp.dao.GigDAO;
import com.futurice.festapp.domain.to.FestivalDay;
import com.futurice.festapp.util.CalendarUtil;
import com.futurice.festapp.util.StringUtil;


public class GigLocation implements Comparable<GigLocation> {
	
	private static final SimpleDateFormat sdfHoursAndMinutes = new SimpleDateFormat("HH:mm");
	
	private String stage;
	private Date startTime;
	private Date endTime;
	
	public GigLocation(String stage, Date startTime, Date endTime) {
		this.stage = stage;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
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

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public Integer getDuration() {
		if (startTime == null || endTime == null) {
			return null;
		}
		return CalendarUtil.getMinutesBetweenTwoDates(startTime, endTime);
	}
	
	public String getStageAndTime() {
		String stage = (this.stage != null) ? this.stage : "";
		String time = getDayAndTime();
		
		if (StringUtil.isNotEmpty(stage) && StringUtil.isNotEmpty(time)) {
			return String.format("%s, %s", stage, time);
		} else if (StringUtil.isNotEmpty(stage)) {
			return stage;
		} else if (StringUtil.isNotEmpty(time)) {
			return time;
		}
		return "";
	}
	
	public String getDayAndTime() {
		return (startTime != null && endTime != null) ? getStartDay().substring(0, 2).toLowerCase() + " " + getTime() : "";
	}
	
	public String getTime() {
		return (startTime != null && endTime != null) ? sdfHoursAndMinutes.format(startTime) + " - " + sdfHoursAndMinutes.format(endTime) : "";
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
	public int compareTo(GigLocation another) {
		int comparison = this.startTime.compareTo(another.getStartTime());
		return (comparison == 0) ? this.stage.compareTo(another.getStage()) : comparison;
	}


}
