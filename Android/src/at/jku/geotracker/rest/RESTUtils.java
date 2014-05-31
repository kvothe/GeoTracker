package at.jku.geotracker.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;
import at.jku.geotracker.rest.model.ResponseObject;

public class RESTUtils {
	private static final String TAG = "GeoTracker";

	public static ResponseObject get(String url) {
		return send(new HttpGet(url));
	}

	public static ResponseObject put(String url, JSONObject data) {
		HttpPut put = new HttpPut(url);
		put.setHeader("content-type", "application/json; charset=UTF-8");
		put.setHeader("Accept", "application/json");
		// --
		StringEntity entity = null;
		try {
			entity = new StringEntity(data.toString());
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		put.setEntity(entity);
		// --
		return send(put);
	}

	public static ResponseObject post(String url, JSONObject data) {
		HttpPost post = new HttpPost(url);
		post.setHeader("content-type", "application/json; charset=UTF-8");
		post.setHeader("Accept", "application/json");
		// --
		StringEntity entity = null;
		try {
			entity = new StringEntity(data.toString());
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		post.setEntity(entity);
		// --
		return send(post);
	}

	public static ResponseObject send(HttpRequestBase request) {
		HttpClient httpclient = HTTPSClient.getNewHttpClient();
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
