package fi.ruisrock.android.domain.to;

public enum FestivalDay {
	FRIDAY,
	SATURDAY,
	SUNDAY;
	
	public String getFinnishName() {
		switch (this) {
		case FRIDAY:
			return "Perjantai";
		case SATURDAY:
			return "Lauantai";
		case SUNDAY:
			return "Sunnuntai";
		default:
			return null;
		}
	}
	
}
