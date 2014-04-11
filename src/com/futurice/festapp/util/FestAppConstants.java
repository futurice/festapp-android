package com.futurice.festapp.util;


public class FestAppConstants {
	
	public static final String WEBSITE_BASE_URL = UrlConfiguration.getInstance().getUrl("WEBSITE_BASE_URL");
	
	public static final String BASE_URL = UrlConfiguration.getInstance().getUrl("BASE_URL");
	public static final String NEWS_JSON_URL = UrlConfiguration.getInstance().getUrl("NEWS_JSON_URL");
	public static final String GIGS_JSON_URL = UrlConfiguration.getInstance().getUrl("GIGS_JSON_URL");
	public static final String TRANSPORTATION_HTML_URL = UrlConfiguration.getInstance().getUrl("TRANSPORTATION_HTML_URL");
	public static final String SERVICES_JSON_URL = UrlConfiguration.getInstance().getUrl("SERVICES_JSON_URL");
	
	public static final String FREQUENTLY_ASKED_QUESTIONS_JSON_URL = UrlConfiguration.getInstance().getUrl("FREQUENTLY_ASKED_QUESTIONS_JSON_URL");
	public static final String FOOD_AND_DRINK_HTML_URL =  UrlConfiguration.getInstance().getUrl("FOOD_AND_DRINK_HTML_URL");
	
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
		
}
