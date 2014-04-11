package com.futurice.festapp.domain.to;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.futurice.festapp.dao.FestivalDayDAO;


public class FestivalDay {
	
	private Calendar day;
	
	public FestivalDay(Calendar day){
		this.day = day;
	}
	
	public String toString(){
		return FestivalDayDAO.DB_DATE_FORMATTER.format(day.getTime());
	}
	
	
	public String getFinnishName() {
		switch(this.day.get(Calendar.DAY_OF_WEEK)){
		case 2:
			return "MAANANTAI";
		case 3:
			return "TIISTAI";
		case 4:
			return "KESKIVIIKKO";
		case 5:
			return "TORSTAI";
		case 6:
			return "PERJANTAI";
		case 7:
			return "LAUANTAI";
		case 1:
			return "SUNNUNTAI";
		}
		return "ABC";
	}
	
	public Calendar getDate(){
		return this.day;
	}
	
	public boolean equals(FestivalDay other){
		return other.getDate().equals(this.getDate());
	}
	
}
