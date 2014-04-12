package com.futurice.festapp.domain.to;

import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.futurice.festapp.util.StringUtil;

public class HTTPBackendResponse {
	private static final String TAG = "HTTPBackendResponse";

	private InputStream content;
	private boolean valid;
	private String eTag;

	public HTTPBackendResponse() {

	}

	public InputStream getContent() {
		return content;
	}

	public String getStringContent() {
		try {
			return StringUtil.convertStreamToString(content);
		} catch (IOException e) {
			Log.e(TAG, "Cannot convert HTTP response to string.");
			return "";
		}
	}

	public void setContent(InputStream inputStream) {
		this.content = inputStream;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getEtag() {
		return eTag;
	}

	public void setEtag(String eTag) {
		this.eTag = eTag;
	}

}
