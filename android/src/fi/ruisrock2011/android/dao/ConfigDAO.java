package fi.ruisrock2011.android.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.domain.to.HTTPBackendResponse;
import fi.ruisrock2011.android.domain.to.MapLayerOptions;
import fi.ruisrock2011.android.domain.to.SelectableOption;
import fi.ruisrock2011.android.util.HTTPUtil;
import fi.ruisrock2011.android.util.RuisrockConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Data-access operations for Configuration options.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class ConfigDAO {
	
	private static final DateFormat DB_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String TAG = "ConfigDAO";
	private static final List<String> MAP_LAYER_OPTIONS = new ArrayList<String>();
	
	private static final String ATTR_SELECTED_MAP_LAYERS = "selected_map_layers";
	public static final String ATTR_ETAG_FOR_GIGS = "etag_gigs";
	public static final String ATTR_ETAG_FOR_NEWS = "etag_news";
	public static final String ATTR_ETAG_FOR_FOODANDDRINK = "etag_foodanddrink";
	public static final String ATTR_ETAG_FOR_TRANSPORTATION = "etag_transportation";
	
	public static final String ATTR_PAGE_FOODANDDRINK = "page_foodanddrink";
	public static final String ATTR_PAGE_TRANSPORTATION = "page_transportation";
	
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
		if (MAP_LAYER_OPTIONS.size() == 0) {
			MAP_LAYER_OPTIONS.add(context.getString(R.string.mapActivity_layer_gps));
			//MAP_LAYER_OPTIONS.add(context.getString(R.string.mapActivity_layer_test));
		}
		return MAP_LAYER_OPTIONS;
	}
	
	private static Date getDate(String date) {
		try {
			return DB_DATE_FORMATTER.parse(date);
		} catch (ParseException e) {
			Log.e(TAG, "Unable to parse date from " + date);
		}
		return null;
	}
	
	private static void setAttributeValue(String attributeName, String attributeValue, Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			db.beginTransaction();
			cursor = db.rawQuery("SELECT attributeName, attributeValue FROM config WHERE attributeName = ?", new String[]{attributeName});
			ContentValues cv = createContentValues(attributeName, attributeValue);
			if (cursor.getCount() == 1) {
				db.update("config", cv, "attributeName = ?", new String[]{attributeName});
			} else {
				db.insert("config", "attributeValue", cv);
			}
			Log.i(TAG, String.format("Set %s ==> %s", attributeName, attributeValue));
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			closeDb(db, cursor);
		}
	}
	
	private static ContentValues createContentValues(String attributeName, String attributeValue) {
		ContentValues cv = new ContentValues();
		cv.put("attributeName", attributeName);
		cv.put("attributeValue", attributeValue);
		return cv;
	}
	
	private static String getAttributeValue(String attributeName, Context context) {
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
		HTTPBackendResponse response = httpUtil.performGet(RuisrockConstants.FOOD_AND_DRINK_HTML_URL);
		if (!response.isValid() || response.getContent() == null) {
			return;
		}
		setEtagForFoodAndDrink(context, response.getEtag());
		setPageFoodAndDrink(context, response.getContent());
	}
	
	public static void updateTransportationPageOverHttp(Context context) {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(RuisrockConstants.TRANSPORTATION_HTML_URL);
		if (!response.isValid() || response.getContent() == null) {
			return;
		}
		setEtagForTransportation(context, response.getEtag());
		setPageTransportation(context, response.getContent());
	}
	

}
