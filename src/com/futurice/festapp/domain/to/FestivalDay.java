package com.futurice.festapp.domain.to;

public enum FestivalDay {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
	FRIDAY,
	SATURDAY,
	SUNDAY;
	
	public String getFinnishName() {
		switch (this) {
         case MONDAY:
            return R.string.Monday;
         case TUESDAY:
            return R.string.Tuesday;
		case WEDNESDAY:
            return R.string.Wednesday;
        case THURSDAY:
            return R.string.Thursday;
        case FRIDAY:
			return R.string.Friday;
		case SATURDAY:
			return R.string.Thursday;
		case SUNDAY:
			return R.string.Sunday;
		default:
			return null;
		}
	}
	
}
