package fi.ruisrock2011.android.util;

public class RuisrockConstants {
	
	public static final String RUISROCK_BASE_URL = "http://www.ruisrock.fi/";
	
	public static final String NEWS_JSON_URL = "http://legotius.futurice.com/ruisrock/cache.php?page=news";
	public static final String GIGS_JSON_URL = "http://legotius.futurice.com/ruisrock/cache.php?page=bands";
	
	public static final String SERVICES_JSON_URL = "http://legotius.futurice.com/ruisrock/cache.php?page=services";
	public static final String GENERAL_INFO_JSON_URL = "http://legotius.futurice.com/ruisrock/cache.php?page=general";
	public static final String TRANSPORTATION_HTML_URL = "http://legotius.futurice.com/ruisrock/cache.php?page=arrival";
	public static final String FOOD_AND_DRINK_HTML_URL = "http://legotius.futurice.com/ruisrock/cache.php?page=fooddrinks";
	
	public static final String DRAWABLE_ARTIST_LOGO_PREFIX = "artist_";
	public static final String DRAWABLE_GUITAR_STRING_PREFIX = "guitar_string_";
	
	public static final long SPLASH_SCREEN_TIMEOUT = 4000L; // milliseconds
	
	public static final long SERVICE_FREQUENCY = 5 * 60 * 1000L;
	public static final long SERVICE_INITIAL_WAIT_TIME = 5 * 1000L;
	public static final int SERVICE_NEWS_ALERT_THRESHOLD_IN_MINUTES = 5 * 60;
	
	public static final String PREFERENCE_GLOBAL = "fi.ruisrock2011.android.globalPreference";
	
	// INITIAL ETAG VALUES
	public static final String ETAG_GIGS = "5f1b28578706b53a4850ec76c1d77332";
	public static final String ETAG_TRANSPORTATION = "65afc40ef57359e62246b118407d695a";
	public static final String ETAG_NEWS = "b3d3d843fb72e9ce6652779242ab244c";
	public static final String ETAG_FOOD_AND_DRINK = "077fadec446efd20f8d15337989cc410";
	public static final String ETAG_SERVICES = "369d5f3abd8c9106f2f17e9bc4741e67";
	public static final String ETAG_GENERAL_INFO = "38b9ba53c1b6edb0c5835cd31084f247";
	
	/*
	public static final String ETAG_GIGS = null;
	public static final String ETAG_TRANSPORTATION = null;
	public static final String ETAG_NEWS = null;
	public static final String ETAG_FOOD_AND_DRINK = null;
	public static final String ETAG_SERVICES = null;
	public static final String ETAG_GENERAL_INFO = null;
	*/
	
}
