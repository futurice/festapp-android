package fi.ruisrock2011.android.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {
	
	public static String getString(JSONObject j, String name) throws JSONException {
		if (!j.has(name) || j.isNull(name)) {
			return null;
		}
		return j.getString(name);
	}

}
