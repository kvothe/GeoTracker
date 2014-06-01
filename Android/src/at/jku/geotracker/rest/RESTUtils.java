package at.jku.geotracker.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;
import at.jku.geotracker.application.Globals;
import at.jku.geotracker.rest.model.ResponseObject;

public class RESTUtils {
	private static final String TAG = "GeoTracker";

	public static ResponseObject get(String url) {
		return send(new HttpGet(url), null);
	}

	public static ResponseObject put(String url, JSONObject data) {
		return send(new HttpPut(url), data);
	}

	public static ResponseObject post(String url, JSONObject data) {
		return send(new HttpPost(url), data);
	}

	private static ResponseObject send(HttpRequestBase request, JSONObject data) {
		HttpClient httpclient = HTTPSClient.getNewHttpClient();
		// --
		request.setHeader("content-type", "application/json; charset=UTF-8");
		request.setHeader("Accept", "application/json");
		// --
		if (data != null && request instanceof HttpEntityEnclosingRequestBase) {
			try {
				((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(data.toString()));
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		// --
		if (Globals.getSessionId() != null) {
			if (request.containsHeader("Cookie")) {
				Header[] cookies = request.getHeaders("Cookie");
				if (cookies.length > 0) {
					Log.d("GeoTracker", "cookie: " + cookies[0].getValue());
					request.setHeader("Cookie", cookies[0].getValue() + ";" + "session-token=" + Globals.getSessionId());
				}
			} else {
				request.setHeader("Cookie", "session-token=" + Globals.getSessionId());
			}
		}
		// --
		int responseStatus = 500;
		String responseContent = null;
		HttpResponse response = null;
		// --
		try {
			response = httpclient.execute(request);
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		// --
		if (response != null) {
			try {
				if (response.getEntity() != null) {
					responseContent = EntityUtils.toString(response.getEntity());
				}
				// --
				if (response.getStatusLine() != null) {
					responseStatus = response.getStatusLine().getStatusCode();
					Log.d("GeoTracker", "response status: " + responseStatus);
				}
			} catch (ParseException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		// --
		return new ResponseObject(null, responseContent, responseStatus);
	}
}
