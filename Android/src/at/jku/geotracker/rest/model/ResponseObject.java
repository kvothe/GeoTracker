package at.jku.geotracker.rest.model;

import android.app.Activity;

public class ResponseObject {

	private String response;
	private Activity activity;
	private int statusCode;

	public ResponseObject() {
		super();
	}

	public ResponseObject(String response, Activity activity, int statusCode) {
		super();
		this.response = response;
		this.activity = activity;
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

}
