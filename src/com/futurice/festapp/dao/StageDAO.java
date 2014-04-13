package com.futurice.festapp.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.futurice.festapp.domain.Stage;
import com.futurice.festapp.domain.to.HTTPBackendResponse;
import com.futurice.festapp.util.FestAppConstants;
import com.futurice.festapp.util.HTTPUtil;

public class StageDAO {

	private static final String TAG = "StageDAO";

	public static List<Stage> findAll(Context context) {
		List<Stage> stages = new ArrayList<Stage>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = (new DatabaseHelper(context)).getWritableDatabase();
			cursor = db.rawQuery("SELECT name, x, y, width, height FROM stages", new String[]{});
			while (cursor.moveToNext()) {
				String name = cursor.getString(0);
				int x = cursor.getInt(1);
				int y = cursor.getInt(2);
				int width = cursor.getInt(3);
				int height = cursor.getInt(4);
				stages.add(new Stage(name, x, y, width, height));
			}
		} finally {
			closeDb(db, cursor);
		}

		return stages;
	}

	/**
	 * TODO: Original issue is https://github.com/futurice/festapp-android/issues/77
	 * 
	 * If this function is called at the same time twice, it will break everything, so prevent
	 * concurrent access to function by synchronized keyword. (yuck!) Should fix original issue.
	 * 
	 * Fixed in https://github.com/futurice/festapp-android/pull/81, but not yet merged.
	 */
	public synchronized static boolean updateStagesOverHttp(Context context) {
		HTTPUtil httpUtil = new HTTPUtil();
		HTTPBackendResponse response = httpUtil.performGet(FestAppConstants.BASE_URL + FestAppConstants.STAGES_JSON_URL);
		if (!response.isValid() || response.getStringContent() == null) {
			return false;
		}

		try {
			List<Stage> stages = parseFromJson(response.getStringContent());
			if (stages != null) {
				SQLiteDatabase db = null;
				try {
					db = (new DatabaseHelper(context)).getWritableDatabase();
					// we can expect here that given json is valid, so drop the data
					db.execSQL("DELETE FROM stages");
					db.beginTransaction();
					for (Stage stage : stages) {
						ContentValues values = StageDAO.convertStageToContentValues(stage);
						db.insert("stages", null, values);
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
					closeDb(db, null);
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static List<Stage> parseFromJson(String json) throws Exception {
		List<Stage> stages = new ArrayList<Stage>();
		try {
			JSONArray list = new JSONArray(json);
			for (int i=0; i<list.length(); ++i) {
				JSONObject stageObject = list.getJSONObject(i);
				String name = stageObject.getString("name");
				int x = (int) stageObject.getLong("x");
				int y = (int) stageObject.getLong("y");
				int width = (int) stageObject.getLong("width");
				int height = (int) stageObject.getLong("height");
				stages.add(new Stage(name, x, y, width, height));
			}
		} catch (JSONException e) {
			Log.w(TAG, "Received invalid JSON-structure", e);
			return null;
		}

		return stages;
	}

	public static ContentValues convertStageToContentValues(Stage stage) {
		ContentValues values = new ContentValues();
		values.put("name", stage.getName());
		values.put("x", stage.getX());
		values.put("y", stage.getY());
		values.put("width", stage.getWidth());
		values.put("height", stage.getHeight());
		return values;
	}

	private static void closeDb(SQLiteDatabase db, Cursor cursor) {
		if (db != null) {
			db.close();
		}
		if (cursor != null) {
			cursor.close();
		}
	}

}
