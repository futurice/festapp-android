package com.futurice.festapp.util;

public class FestAppConstants {
	
	public static final String WEBSITE_BASE_URL = "http://festapp-server.herokuapp.com/";
	
	public static final String BASE_URL = "http://festapp-server.herokuapp.com";
	public static final String NEWS_JSON_URL = "/api/news";
	public static final String GIGS_JSON_URL = "/api/artists";
	public static final String TRANSPORTATION_HTML_URL = "/api/arrival";
	public static final String SERVICES_JSON_URL = "/api/services";
	public static final String STAGES_JSON_URL = "/api/v1/locations";
	
	public static final String FREQUENTLY_ASKED_QUESTIONS_JSON_URL = "/api/info";
	public static final String FOOD_AND_DRINK_HTML_URL =  "/api/program";
	
	public static final String DRAWABLE_ARTIST_LOGO_PREFIX = "artist_";
	public static final String DRAWABLE_GUITAR_STRING_PREFIX = "guitar_string_";
	
	public static final long SPLASH_SCREEN_TIMEOUT = 3000L; // milliseconds
	
	public static final long SERVICE_FREQUENCY = 5 * 60 * 1000L;
	public static final long SERVICE_INITIAL_WAIT_TIME = 5 * 1000L;
	public static final int SERVICE_NEWS_ALERT_THRESHOLD_IN_MINUTES = 5 * 60;
	
	public static final String PREFERENCE_GLOBAL = "com.futurice.festapp.globalPreference";
	public static final String PREFERENCE_SHOW_FAVORITE_INFO = "showFavoriteInfo";
	
	
	public static final String LAST_MODIFIED_GIGS = null;
	public static final String LAST_MODIFIED_TRANSPORTATION = null;
	public static final String LAST_MODIFIED_NEWS = null;
	public static final String LAST_MODIFIED_FOOD_AND_DRINK = null;
	public static final String LAST_MODIFIED_SERVICES = null;
	public static final String LAST_MODIFIED_GENERAL_INFO = null;
	public static final String LAST_MODIFIED_FAQ = null;
		
	// DEBUG variables
	public final static boolean F_DEBUG = false;
	public volatile static boolean F_FORCE_DATA_FETCH = false;
}
