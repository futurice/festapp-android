package com.futurice.festapp.domain.to;


import com.futurice.festapp.R;
import android.content.Context;

public enum FestivalDay {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
	FRIDAY,
	SATURDAY,
	SUNDAY;
	
	public String getLocalName(Context context) {
		switch (this) {
        case MONDAY:
            return context.getString(R.string.Monday);
        case TUESDAY:
            return context.getString(R.string.Tuesday);
		case WEDNESDAY:
            return context.getString(R.string.Wednesday);
        case THURSDAY:
            return context.getString(R.string.Thursday);
        case FRIDAY:
			return context.getString(R.string.Friday);
		case SATURDAY:
			return context.getString(R.string.Thursday);
		case SUNDAY:
			return context.getString(R.string.Sunday);
		default:
			return null;
		}
	}
	
}
