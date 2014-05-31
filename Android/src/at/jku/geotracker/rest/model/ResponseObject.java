package at.jku.geotracker.rest.model;

import at.jku.geotracker.rest.interfaces.ResponseListener;

public class ResponseObject {
	private ResponseListener listener;
	private String response;
	private int statusCode;

	public ResponseObject(ResponseListener listener, String response, int statusCode) {
		this.response = response;
		this.listener = listener;
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

	public ResponseListener getListener() {
		return listener;
	}

	public void setListener(ResponseListener listener) {
		this.listener = listener;
	}
}
