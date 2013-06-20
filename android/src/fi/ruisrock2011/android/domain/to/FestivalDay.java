package fi.ruisrock2011.android.domain.to;

public enum FestivalDay {
	FRIDAY,
	SATURDAY,
	SUNDAY;
	
	public String getFinnishName() {
		switch (this) {
		case FRIDAY:
			return "PERJANTAI";
		case SATURDAY:
			return "LAUANTAI";
		case SUNDAY:
			return "SUNNUNTAI";
		default:
			return null;
		}
	}
	
}
