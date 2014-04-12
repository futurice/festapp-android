package com.futurice.festapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;
import android.util.Log;

public class URLUtil {

	private static URLUtil instance = null;

	public static final int WEBSITE_BASE_URL = 0;
	public static final int NEWS_JSON_URL = 1;
	public static final int GIGS_JSON_URL = 2;
	public static final int TRANSPORTATION_HTML_URL = 3;
	public static final int SERVICES_JSON_URL = 4;
	public static final int FREQUENTLY_ASKED_QUESTIONS_JSON_URL = 5;
	public static final int FOOD_AND_DRINK_HTML_URL = 6;

	private static final String URL_OVERRIDE_PATH = "url_overrides.json";

	private static final String[] urlNames = { "website_base_url",
			"news_json_url", "gigs_json_url", "transportation_html_url",
			"services_json_url", "frequently_asked_questions_json_url",
			"food_and_drink_html_url", };

	private String[] urls = { "http://festapp-server.herokuapp.com/",
			"/api/news", "/api/artists", "/api/arrival", "/api/services",
			"/api/info", "/api/program", };

	private static void readUrlOverride(JsonReader reader,
			HashMap<String, String> results) throws IOException {
		String name = reader.nextName();
		String value = reader.nextString();

		results.put(name, value);
	}

	private static HashMap<String, String> getUrlOverrides(InputStream stream)
			throws IOException {
		HashMap<String, String> overrides = new HashMap<String, String>();
		JsonReader reader = new JsonReader(new InputStreamReader(stream));

		reader.beginObject();

		while (reader.hasNext()) {
			readUrlOverride(reader, overrides);
		}

		reader.endObject();

		return overrides;
	}

	private URLUtil(Context context) {
		AssetManager assets = context.getAssets();
		InputStream stream = null;

		try {
			stream = assets.open(URL_OVERRIDE_PATH);
		} catch (IOException ex) {
			// Nothing to do here, just use the default URLs.
			Log.v("URLUtil", "URL override file not found, using defaults.");
			return;
		}

		try {
			HashMap<String, String> overrides = getUrlOverrides(stream);

			for (int urlIdx = 0; urlIdx < urlNames.length; urlIdx++) {
				String urlName = urlNames[urlIdx];

				if (overrides.containsKey(urlName)) {
					urls[urlIdx] = overrides.get(urlName);
				}
			}

			stream.close();
		} catch (IOException e) {
			// Ignored.
		}
	}

	public static URLUtil getInstance(Context context) {
		if (instance == null) {
			instance = new URLUtil(context);
		}

		return instance;
	}

	public String getUrl(int urlIndex) {
		return urls[urlIndex];
	}
}
