package com.futurice.festapp.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.futurice.festapp.R;
import com.futurice.festapp.domain.to.HTTPBackendResponse;
import com.futurice.festapp.domain.to.MapLayerOptions;
import com.futurice.festapp.domain.to.SelectableOption;
import com.futurice.festapp.util.FestAppConstants;
import com.futurice.festapp.util.HTTPUtil;
import com.futurice.festapp.util.JSONUtil;

/**
 * Data-access operations for Configuration options.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class ConfigDAO {
	
//	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private static final String TAG = "ConfigDAO";
	private static final List<String> MAP_LAYER_OPTIONS = new ArrayList<String>();
	
	private static final String ATTR_SELECTED_MAP_LAYERS = "selected_map_layers";
	public static final String ATTR_ETAG_FOR_GIGS = "etag_gigs";
	public static final String ATTR_ETAG_FOR_NEWS = "etag_news";
	public static final String ATTR_ETAG_FOR_FOODANDDRINK = "etag_foodanddrink";
	public static final String ATTR_ETAG_FOR_TRANSPORTATION = "etag_transportation";
	public static final String ATTR_ETAG_FOR_SERVICES = "etag_services";
	public static final String ATTR_ETAG_FOR_FREQUENTLY_ASKED_QUESTIONS = "etag_generalinfo";
	
	public static final String ATTR_PAGE_FOODANDDRINK = "page_foodanddrink";
	public static final String ATTR_PAGE_TRANSPORTATION = "page_transportation";
	
	public static final String ATTR_PAGE_SERVICES_CLOAKROOM = "page_cloakroom";
	public static final String ATTR_PAGE_SERVICES_BIKE_PARK = "page_bike_park";
	public static final String ATTR_PAGE_SERVICES_CAMPING = "page_camping";
	public static final String ATTR_PAGE_SERVICES_MERCHANDISE = "page_merchandise";
	public static final String ATTR_PAGE_SERVICES_ACTIVITIES = "page_activities";
	public static final String ATTR_PAGE_SERVICES_PHONE_CHARGING = "page_phone_charging";
	
	public static final String ATTR_PAGE_GENERALINFO_FREQUENTLY_ASKED = "page_frequently_asked";
	public static final String ATTR_PAGE_GENERALINFO_OPEN_HOURS = "page_open_hours";
	public static final String ATTR_PAGE_GENERALINFO_INFO_STAND = "page_info_stand";
	public static final String ATTR_PAGE_GENERALINFO_LOST_AND_FOUND = "page_lost_and_found";
	public static final String ATTR_PAGE_GENERALINFO_FIRSTAID = "page_firstaid";
	public static final String ATTR_PAGE_GENERALINFO_TICKETS = "page_tickets";
	public static final String ATTR_PAGE_GENERALINFO_ACCESSIBILITY = "page_accessibility";
	public static final String ATTR_PAGE_GENERALINFO_SAFETY_INSTRUCTIONS = "page_safety_instructions";
	
	public static MapLayerOptions findMapLayers(Context context) {
		String values = getAttributeValue(ATTR_SELECTED_MAP_LAYERS, context);
		if (values == null) {
			values = "";
		}
		List<SelectableOption> layers = new ArrayList<SelectableOption>();
		for (String option : getAllMapLayers(context)) {
			layers.add(new SelectableOption(option, values.contains(option)));
		}
		return new MapLayerOptions(layers);
	}
	
	public static void updateMapLayers(Context context, MapLayerOptions mapLayerOptions) {
		setAttributeValue(ATTR_SELECTED_MAP_LAYERS, mapLayerOptions.getSelectedValuesAsConcatenatedString(), context);
	}
	
	public static String getEtagForGigs(Context context) {
		return getAttributeValue(ATTR_ETAG_FOR_GIGS, context);
	}
	
	public static void setEtagForGigs(Context context, String etag) {
		setAttributeValue(ATTR_ETAG_FOR_GIGS, etag, context);
	}
	
	public static String getEtagForFoodAndDrink(Context context) {
		return getAttributeValue(ATTR_ETAG_FOR_FOODANDDRINK, context);
	}
	
	public static void setEtagForFoodAndDrink(Context context, String etag) {
		setAttributeValue(ATTR_ETAG_FOR_FOODANDDRINK, etag, context);
	}
	
	public static String getEtagForNews(Context context) {
		return getAttributeValue(ATTR_ETAG_FOR_NEWS, context);
	}
	
	public static void setEtagForNews(Context context, String etag) {
		setAttributeValue(ATTR_ETAG_FOR_NEWS, etag, context);
	}
	
	public static String getEtagForServices(Context context) {
		return getAttributeValue(ATTR_ETAG_FOR_SERVICES, context);
	}
	
	public static void setEtagForServices(Context context, String etag) {
		setAttributeValue(ATTR_ETAG_FOR_SERVICES, etag, context);
	}
	
	public static String getEtagForFrequentlyAskedQuestions(Context context) {
		return getAttributeValue(ATTR_ETAG_FOR_FREQUENTLY_ASKED_QUESTIONS, context);
	}
	
	public static void setEtagForGeneralInfo(Context context, String etag) {
		setAttributeValue(ATTR_ETAG_FOR_FREQUENTLY_ASKED_QUESTIONS, etag, context);
	}
	
	public static String getPageFoodAndDrink(Context context) {
		return getAttributeValue(ATTR_PAGE_FOODANDDRINK, context);
	}
	
	public static void setPageFoodAndDrink(Context context, String page) {
		setAttributeValue(ATTR_PAGE_FOODANDDRINK, page, context);
	}
	
	
	public static String getEtagForTransportation(Context context) {
		return getAttributeValue(ATTR_ETAG_FOR_TRANSPORTATION, context);
	}
	
	public static void setEtagForTransportation(Context context, String etag) {
		setAttributeValue(ATTR_ETAG_FOR_TRANSPORTATION, etag, context);
	}
	
	public static String getPageTransportation(Context context) {
		return getAttributeValue(ATTR_PAGE_TRANSPORTATION, context);
	}
	
	public static void setPageTransportation(Context context, String page) {
		setAttributeValue(ATTR_PAGE_TRANSPORTATION, page, context);
	}
	
	private static List<String> getAllMapLayers(Context context) {
		return MAP_LAYER_OPTIONS;
	}
	
	
	private static void setAttributeValue(String attributeName, String attributeValue, Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			db.beginTransaction();
			cursor = db.rawQuery("SELECT attributeName, attributeValue FROM config WHERE attributeName = ?", new String[]{attributeName});
			ContentValues cv = createConfigContentValues(attributeName, attributeValue);
			if (cursor.getCount() == 1) {
				db.update("config", cv, "attributeName = ?", new String[]{attributeName});
			} else {
				db.insert("config", "attributeValue", cv);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			closeDb(db, cursor);
		}
	}
	
	private static void setAttributeValues(Map<String, String> values, Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			db.beginTransaction();
			
			for (Map.Entry<String, String> entry : values.entrySet()) {
				String attributeName = entry.getKey();
				String attributeValue = entry.getValue();
				
				cursor = db.rawQuery("SELECT attributeName, attributeValue FROM config WHERE attributeName = ?", new String[]{attributeName});
				ContentValues cv = createConfigContentValues(attributeName, attributeValue);
				if (cursor.getCount() == 1) {
					db.update("config", cv, "attributeName = ?", new String[]{attributeName});
				} else {
					db.insert("config", "attributeValue", cv);
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			closeDb(db, cursor);
		}
	}
	
	public static ContentValues createConfigContentValues(String attributeName, String attributeValue) {
		ContentValues cv = new ContentValues();
		cv.put("attributeName", attributeName);
		cv.put("attributeValue", attributeValue);
		return cv;
	}
	
	public static String getAttributeValue(String attributeName, Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getReadableDatabase();
			cursor = db.rawQuery("SELECT attributeName, attributeValue FROM config WHERE attributeName = ?", new String[]{attributeName});
			if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				return cursor.getString(1);
			}
		} finally {
			closeDb(db, cursor);
		}
		return null;
	}
	
	private static void closeDb(SQLiteDatabase db, Cursor cursor) {
		if (db != null) {
			db.close();
		}
		if (cursor != null) {
			cursor.close();
		}
	}

	public static void updateFoodAndDrinkPageOverHttp(Context context) {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.FOOD_AND_DRINK_HTML_URL);
		if (!response.isValid() || response.getStringContent() == null) {
			return;
		}
		setEtagForFoodAndDrink(context, response.getEtag());

		try {
			String content = null;
			JSONArray arr = new JSONArray(response.getStringContent());
			for(int i = 0; i < arr.length(); i++) {
				JSONObject o = arr.getJSONObject(i);
				if(o.getString("title").equals("Makuelämykset")) {
					content = o.getString("content");
					break;
				}
			}
			if(content == null) {
				Log.w(TAG, "Received invalid JSON-structure. No content found.");
				return;
			}
			
			setPageFoodAndDrink(context, content);
		} catch (Exception e) {
			Log.w(TAG, "Received invalid JSON-structure", e);
		}
	}
	
	public static String parseFromJson(String json, String key) throws Exception {
		String content = "";
		try {
			JSONObject jsonObject = new JSONArray(json).getJSONObject(0);
			content = JSONUtil.getString(jsonObject, key);
		} catch (Exception e) {
			Log.w(TAG, "Received invalid JSON-structure", e);
		}
		return content;
	}

	public static void updateTransportationPageOverHttp(Context context) {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.TRANSPORTATION_HTML_URL);
		if (!response.isValid() || response.getStringContent() == null) {
			return;
		}
		setEtagForTransportation(context, response.getEtag());
		setPageTransportation(context, response.getStringContent());
	}
	
	public static void updateServicePagesOverHttp(Context context) {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.SERVICES_JSON_URL);
		if (!response.isValid() || response.getStringContent() == null) {
			return;
		}
		setAttributeValue(ATTR_ETAG_FOR_SERVICES, response.getEtag(), context);
		try {
			setAttributeValues(parseServicesMapFromJson(context, response.getStringContent()), context);
		} catch (Exception e) {
			Log.e(TAG, "Error parsing Services JSON.", e);
		}
	}
	
	public static void updateFrequentlyAskedQuestionsPagesOverHttp(Context context) {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.FREQUENTLY_ASKED_QUESTIONS_JSON_URL);
		if (!response.isValid() || response.getStringContent() == null) {
			return;
		}
		setAttributeValue(ATTR_ETAG_FOR_FREQUENTLY_ASKED_QUESTIONS, response.getEtag(), context);
		try {
			JSONArray arr = new JSONArray(response.getStringContent());
			String content = null;
			for(int i = 0; i < arr.length(); i++) {
				JSONObject o = arr.getJSONObject(i);
				if(o.getString("title").equals("Usein Kysytyt Kysymykset")) {
					content = o.getString("content");
					break;
				}
			}
			if(content == null) {
				Log.e(TAG, "Error parsing FrequentlyAskedQuestions JSON. No content found.");
			}
			setAttributeValue(ATTR_PAGE_GENERALINFO_FREQUENTLY_ASKED, content, context);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Error parsing FrequentlyAskedQuestions JSON.", e);
		}
	}
	
	public static Map<String,String> parseServicesMapFromJson(Context context, String json) throws Exception {
		JSONObject servicesObj = new JSONObject(json);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(ATTR_PAGE_SERVICES_ACTIVITIES, JSONUtil.getString(servicesObj, context.getString(R.string.service_Activities)));
		map.put(ATTR_PAGE_SERVICES_BIKE_PARK, JSONUtil.getString(servicesObj, context.getString(R.string.service_BikePark)));
		map.put(ATTR_PAGE_SERVICES_CAMPING, JSONUtil.getString(servicesObj, context.getString(R.string.service_Camping)));
		map.put(ATTR_PAGE_SERVICES_CLOAKROOM, JSONUtil.getString(servicesObj, context.getString(R.string.service_Cloakroom)));
		map.put(ATTR_PAGE_SERVICES_MERCHANDISE, JSONUtil.getString(servicesObj, context.getString(R.string.service_Merchandise)));
		map.put(ATTR_PAGE_SERVICES_PHONE_CHARGING, JSONUtil.getString(servicesObj, context.getString(R.string.service_PhoneCharging)));
		return map;
	}
	
	public static Map<String,String> parseGeneralInfoMapFromJson(Context context, String json) throws Exception {
		JSONObject servicesObj = new JSONObject(json);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(ATTR_PAGE_GENERALINFO_FIRSTAID, JSONUtil.getString(servicesObj, context.getString(R.string.generalInfo_Firstaid)));
		map.put(ATTR_PAGE_GENERALINFO_INFO_STAND, JSONUtil.getString(servicesObj, context.getString(R.string.generalInfo_InfoStand)));
		map.put(ATTR_PAGE_GENERALINFO_LOST_AND_FOUND, JSONUtil.getString(servicesObj, context.getString(R.string.generalInfo_LostAndFound)));
		map.put(ATTR_PAGE_GENERALINFO_OPEN_HOURS, JSONUtil.getString(servicesObj, context.getString(R.string.generalInfo_OpenHours)));
		map.put(ATTR_PAGE_GENERALINFO_TICKETS, JSONUtil.getString(servicesObj, context.getString(R.string.generalInfo_Tickets)));
		map.put(ATTR_PAGE_GENERALINFO_ACCESSIBILITY, JSONUtil.getString(servicesObj, context.getString(R.string.generalInfo_Accessibility)));
		map.put(ATTR_PAGE_GENERALINFO_SAFETY_INSTRUCTIONS, JSONUtil.getString(servicesObj, context.getString(R.string.generalInfo_SafetyInstructions)));
		return map;
	}
}
