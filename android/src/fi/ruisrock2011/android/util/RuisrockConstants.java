package fi.ruisrock2011.android.util;

public class RuisrockConstants {
	
	public static final String RUISROCK_BASE_URL = "http://www.ruisrock.fi/";
	
	
	// TODO: Changed urls for Ruisrock2012
	public static final String NEWS_JSON_URL = "http://www.ruisrock.fi/json.php?action=news"; //"http://legotius.futurice.com/ruisrock/cache.php?page=news";
	public static final String GIGS_JSON_URL = "http://www.ruisrock.fi/json.php?action=bands"; //"http://legotius.futurice.com/ruisrock/cache.php?page=bands";
	//public static final String GIGS_JSON_URL = "http://legotius.futurice.com/debug_ruisrock/gigs";
	
	public static final String SERVICES_JSON_URL = "http://legotius.futurice.com/ruisrock/cache.php?page=services";
	public static final String FREQUENTLY_ASKED_QUESTIONS_JSON_URL = "http://www.ruisrock.fi/json.php?action=faq"; //"http://legotius.futurice.com/ruisrock/cache.php?page=general";
	public static final String TRANSPORTATION_HTML_URL = "http://legotius.futurice.com/ruisrock/cache.php?page=arrival";
	public static final String FOOD_AND_DRINK_HTML_URL =  "http://www.ruisrock.fi/json.php?action=food"; //"http://legotius.futurice.com/ruisrock/cache.php?page=fooddrinks";
	
	public static final String DRAWABLE_ARTIST_LOGO_PREFIX = "artist_";
	public static final String DRAWABLE_GUITAR_STRING_PREFIX = "guitar_string_";
	
	// TODO: Shortened splash timeout for Ruisrock2012
	public static final long SPLASH_SCREEN_TIMEOUT = 3000L; // milliseconds
	
	public static final long SERVICE_FREQUENCY = 5 * 60 * 1000L;
	public static final long SERVICE_INITIAL_WAIT_TIME = 5 * 1000L;
	public static final int SERVICE_NEWS_ALERT_THRESHOLD_IN_MINUTES = 5 * 60;
	
	public static final String PREFERENCE_GLOBAL = "fi.ruisrock2011.android.globalPreference";
	public static final String PREFERENCE_SHOW_FAVORITE_INFO = "showFavoriteInfo";
	
	// INITIAL ETAG VALUES
	public static final String ETAG_GIGS = "f15d2662740b17df94451df386004fb9";
	public static final String ETAG_TRANSPORTATION = "1d28fee3a6f9f647e370015808eb65d2";
	public static final String ETAG_NEWS = "00e489b34b656da83211e8c1de343b59";
	public static final String ETAG_FOOD_AND_DRINK = "0e24b1d90b3184ebe50fc0ef6476ddbb";
	public static final String ETAG_SERVICES = "369d5f3abd8c9106f2f17e9bc4741e67";
	public static final String ETAG_FREQUENTLY_ASKED_QUESTIONS = "1fdabf7ad8f234a1835476d97ef4a0a9";
	
	/*
	public static final String ETAG_GIGS = null;
	public static final String ETAG_TRANSPORTATION = null;
	public static final String ETAG_NEWS = null;
	public static final String ETAG_FOOD_AND_DRINK = null;
	public static final String ETAG_SERVICES = null;
	public static final String ETAG_GENERAL_INFO = null;
	*/	
}
