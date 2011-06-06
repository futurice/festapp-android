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
	public static final String ETAG_GIGS = "b8c56cde125471f275582e8bf8c7e8ed";
	public static final String ETAG_TRANSPORTATION = "65afc40ef57359e62246b118407d695a";
	//public static final String ETAG_NEWS = "d8d3ef527e862d0df58272d53a6c2ab8";
	public static final String ETAG_NEWS = null;
	public static final String ETAG_FOOD_AND_DRINK = "e527a7b7443e9cddc8801c54cf9fa603";
	public static final String ETAG_SERVICES = "e8ba445475e27ba04e8d3012b05ed29e";
	public static final String ETAG_GENERAL_INFO = "51dfa3c9e4b7d04434279bfcf6a54fe4";
	
}
