package com.futurice.festapp.domain.to;

import com.futurice.festapp.R;
import android.content.Context;

public enum FestivalDay {
	MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

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
			return context.getString(R.string.Saturday);
		case SUNDAY:
			return context.getString(R.string.Sunday);
		default:
			return null;
		}
	}

	public String getLocalAbbrv(Context context) {
		switch (this) {
		case MONDAY:
			return context.getString(R.string.Mon);
		case TUESDAY:
			return context.getString(R.string.Tue);
		case WEDNESDAY:
			return context.getString(R.string.Wed);
		case THURSDAY:
			return context.getString(R.string.Thu);
		case FRIDAY:
			return context.getString(R.string.Fri);
		case SATURDAY:
			return context.getString(R.string.Sat);
		case SUNDAY:
			return context.getString(R.string.Sun);
		default:
			return null;
		}
	}
}
