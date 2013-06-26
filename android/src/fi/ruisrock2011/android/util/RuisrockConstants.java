package fi.ruisrock2011.android.util;

public class RuisrockConstants {
	
	public static final String RUISROCK_BASE_URL = "http://www.ruisrock.fi/";
	
	
	public static final String NEWS_JSON_URL = "http://www.ruisrock.fi/json.php?query=uutiset"; //action=news"; //"http://legotius.futurice.com/ruisrock/cache.php?page=news";
	public static final String GIGS_JSON_URL = "http://www.ruisrock.fi/json.php?query=artistit"; //action=bands"; //"http://legotius.futurice.com/ruisrock/cache.php?page=bands";
	
	public static final String FREQUENTLY_ASKED_QUESTIONS_JSON_URL = "http://www.ruisrock.fi/json.php?query=info"; 
	public static final String FOOD_AND_DRINK_HTML_URL =  "http://www.ruisrock.fi/json.php?query=ohjelma";
	
	public static final String DRAWABLE_ARTIST_LOGO_PREFIX = "artist_";
	public static final String DRAWABLE_GUITAR_STRING_PREFIX = "guitar_string_";
	
	public static final long SPLASH_SCREEN_TIMEOUT = 3000L; // milliseconds
	
	public static final long SERVICE_FREQUENCY = 5 * 60 * 1000L;
	public static final long SERVICE_INITIAL_WAIT_TIME = 5 * 1000L;
	public static final int SERVICE_NEWS_ALERT_THRESHOLD_IN_MINUTES = 5 * 60;
	
	public static final String PREFERENCE_GLOBAL = "fi.ruisrock2011.android.globalPreference";
	public static final String PREFERENCE_SHOW_FAVORITE_INFO = "showFavoriteInfo";
	
	
	public static final String LAST_MODIFIED_GIGS = null;
	public static final String LAST_MODIFIED_TRANSPORTATION = null;
	public static final String LAST_MODIFIED_NEWS = null;
	public static final String LAST_MODIFIED_FOOD_AND_DRINK = null;
	public static final String LAST_MODIFIED_SERVICES = null;
	public static final String LAST_MODIFIED_GENERAL_INFO = null;
	public static final String LAST_MODIFIED_FAQ = null;
		
}
