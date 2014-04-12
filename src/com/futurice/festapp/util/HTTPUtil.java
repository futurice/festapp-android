package com.futurice.festapp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.futurice.festapp.domain.to.HTTPBackendResponse;

import android.util.Log;

/**
 * Apache HttpClient helper class for performing HTTP requests.
 * 
 * @see Original file at
 *      http://code.google.com/p/and-bookworm/source/browse/trunk/src/com/totsp/bookworm/data/HttpHelper.java
 * @author Pyry-Samuli Lahti / Futurice
 */
public class HTTPUtil {

	private static final String TAG = "HTTPUtil";

	private static final String CONTENT_TYPE = "Content-Type";
	private static final int POST_TYPE = 1;
	private static final int GET_TYPE = 2;
	private static final String GZIP = "gzip";
	private static final String ACCEPT_ENCODING = "Accept-Encoding";

	public static final String MIME_FORM_ENCODED = "application/x-www-form-urlencoded";
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String HTTP_RESPONSE = "HTTP_RESPONSE";
	public static final String HTTP_RESPONSE_ERROR = "HTTP_RESPONSE_ERROR";
	
	public static final String LANG = Locale.getDefault().getLanguage();


	// Establish client once, as static field with static setup block.
	// (This is a best practice in HttpClient docs - but will leave reference until *process* stopped on Android.)
	private static final DefaultHttpClient client;
	static {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
		params.setParameter(CoreProtocolPNames.USER_AGENT, "Apache-HttpClient/Android");
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
		params.setParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		client = new DefaultHttpClient(cm, params);
		// add gzip decompressor to handle gzipped content in responses 
		// (default we *do* always send accept encoding gzip header in request)
		HTTPUtil.client.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
				HttpEntity entity = response.getEntity();
				Header contentEncodingHeader = entity.getContentEncoding();
				if (contentEncodingHeader != null) {
					HeaderElement[] codecs = contentEncodingHeader.getElements();
					for (int i = 0; i < codecs.length; i++) {
						if (codecs[i].getName().equalsIgnoreCase(HTTPUtil.GZIP)) {
							response.setEntity(new GzipDecompressingEntity(response.getEntity()));
							return;
						}
					}
				}
			}
		});
	}

	/**
	 * Constructor.
	 *
	 */
	public HTTPUtil() {
	}

	/**
	 * Perform a simple HTTP GET operation.
	 *
	 */
	public HTTPBackendResponse performGet(final String url) {
		return performRequest(null, url, null, null, null, null, HTTPUtil.GET_TYPE);
	}

	public static boolean isContentUpdated(String urlString, String previousEtag) throws Exception {
		if (FestAppConstants.F_FORCE_DATA_FETCH){
			Log.d(TAG, "ETAG ignored!");
			return true;
		}

		if (previousEtag == null || previousEtag.length() == 0) {
			return true;
		}
		URL url = new URL(constructURL(urlString, true) );

		Socket socket = null;
		PrintWriter writer = null;
		BufferedReader reader = null;

		boolean contentChanged = true;
		try {
			socket = new Socket(url.getHost(), 80);
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.println("HEAD "+url.getFile()+" HTTP/1.1");
			writer.println("Host: " + url.getHost());
			writer.println(""); // Important, else the server will expect that there's more into the request.
			writer.flush();

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String line = null;
			while ((line = reader.readLine()) != null) {
				String etag = "Last-Modified: ";
				if (line.startsWith(etag)) {
					String newEtag = line.replaceFirst(etag, "").replace("\"", "");
					if (newEtag.equals(previousEtag)) {
						contentChanged = false;
						break;
					}
				}
			}
		} finally {
			if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {} 
			if (writer != null) { writer.close(); }
			if (socket != null) try { socket.close(); } catch (IOException logOrIgnore) {} 
		}

		return contentChanged;
	}

	/**
	 * Perform an HTTP GET operation with user/pass and headers.
	 *
	 */
	public HTTPBackendResponse performGet(final String url, final String user, final String pass,
			final Map<String, String> additionalHeaders) {
		return performRequest(null, url, user, pass, additionalHeaders, null, HTTPUtil.GET_TYPE);
	}

	/**
	 * Perform a simplified HTTP POST operation.
	 *
	 */
	public HTTPBackendResponse performPost(final String url, final Map<String, String> params) {
		return performRequest(HTTPUtil.MIME_FORM_ENCODED, url, null, null, null, params, HTTPUtil.POST_TYPE);
	}

	/**
	 * Perform an HTTP POST operation with user/pass, headers, request
   parameters,
	 * and a default content-type of "application/x-www-form-urlencoded."
	 *
	 */
	public HTTPBackendResponse performPost(final String url, final String user, final String pass,
			final Map<String, String> additionalHeaders, final Map<String, String> params) {
		return performRequest(HTTPUtil.MIME_FORM_ENCODED, url, user, pass, additionalHeaders, params,
				HTTPUtil.POST_TYPE);
	}

	/**
	 * Perform an HTTP POST operation with flexible parameters (the
   complicated/flexible version of the method).
	 *
	 */
	public HTTPBackendResponse performPost(final String contentType, final String url, final String user, final String pass,
			final Map<String, String> additionalHeaders, final Map<String, String> params) {
		return performRequest(contentType, url, user, pass, additionalHeaders, params, HTTPUtil.POST_TYPE);
	}

	//
	// private methods
	//
	private HTTPBackendResponse performRequest(final String contentType, String url, final String user, final String pass,
			final Map<String, String> headers, final Map<String, String> params, final int requestType) {

		// add user and pass to client credentials if present
		if ((user != null) && (pass != null)) {
			HTTPUtil.client.getCredentialsProvider().setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(user, pass));
		}

		// process headers using request interceptor
		final Map<String, String> sendHeaders = new HashMap<String, String>();
		// add encoding header for gzip if not present
		if (!sendHeaders.containsKey(HTTPUtil.ACCEPT_ENCODING)) {
			sendHeaders.put(HTTPUtil.ACCEPT_ENCODING, HTTPUtil.GZIP);
		}
		if ((headers != null) && (headers.size() > 0)) {
			sendHeaders.putAll(headers);
		}
		if (requestType == HTTPUtil.POST_TYPE) {
			sendHeaders.put(HTTPUtil.CONTENT_TYPE, contentType);
		}
		if (sendHeaders.size() > 0) {
			HTTPUtil.client.addRequestInterceptor(new HttpRequestInterceptor() {
				public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
					for (String key : sendHeaders.keySet()) {
						if (!request.containsHeader(key)) {
							request.addHeader(key, sendHeaders.get(key));
						}
					}
				}
			});
		}

		// handle POST or GET request respectively
		HttpRequestBase method = null;
		if (requestType == HTTPUtil.POST_TYPE) {
			method = new HttpPost(constructURL(url, false));
			// data - name/value params
			List<NameValuePair> nvps = null;
			if ((params != null) && (params.size() > 0)) {
				nvps = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			if (nvps != null) {
				try {
					HttpPost methodPost = (HttpPost) method;
					methodPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("Error peforming HTTP request: " + e.getMessage(), e);
				}
			}
		} else if (requestType == HTTPUtil.GET_TYPE) {
			method = new HttpGet(constructURL(url, true));
		}

		// execute request
		return execute(method);
	}

	private synchronized HTTPBackendResponse execute(final HttpRequestBase method) {
		// execute method returns?!? (rather than async) - do it here sync, and wrap async elsewhere
		HTTPBackendResponse httpBackendResponse = new HTTPBackendResponse();
		try {
			HttpResponse httpResponse = HTTPUtil.client.execute(method);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode != 200) {
				String url = method.getURI().toURL().toExternalForm();
				Log.e(TAG, String.format("Invalid response-code %s received from %s", responseCode, url));
				httpBackendResponse.setValid(false);
				return httpBackendResponse;
			}
			httpBackendResponse.setContent(httpResponse.getEntity().getContent());

			Header[] headers = httpResponse.getHeaders("ETag");
			if(headers == null || headers.length == 0) {
				headers = httpResponse.getHeaders("Etag");
			}
			if (headers != null && headers.length > 0) {
				String etag = headers[0].getValue();
				if (etag != null) {
					etag = etag.replace("\"", "");
				}
				httpBackendResponse.setEtag(etag);
			}
			httpBackendResponse.setValid(true);
			httpResponse.getEntity().consumeContent();
		} catch (Exception e) {
			Log.e(TAG, "Cannot execute HTTP request", e);
			httpBackendResponse.setValid(false);
		}
		return httpBackendResponse;
	}
	
	private static String constructURL(String apiPath, boolean isGetRequest){
		String url = FestAppConstants.BASE_URL + apiPath;
		if(isGetRequest){
			url += "?lang=" + LANG;
		}
		return url;
		
	}

	static class GzipDecompressingEntity extends HttpEntityWrapper {
		public GzipDecompressingEntity(final HttpEntity entity) {
			super(entity);
		}

		@Override
		public InputStream getContent() throws IOException, IllegalStateException {
			// the wrapped entity's getContent() decides about repeatability
			InputStream wrappedin = wrappedEntity.getContent();
			return new GZIPInputStream(wrappedin);
		}

		@Override
		public long getContentLength() {
			// length of ungzipped content is not known
			return -1;
		}
	}
}
