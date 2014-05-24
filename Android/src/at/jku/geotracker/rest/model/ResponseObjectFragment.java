package at.jku.geotracker.rest.model;

import android.app.Fragment;

public class ResponseObjectFragment {

	private String response;
	private Fragment fragment;
	private int statusCode;

	public ResponseObjectFragment() {
		super();
	}

	public ResponseObjectFragment(String response, Fragment fragment,
			int statusCode) {
		super();
		this.response = response;
		this.fragment = fragment;
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

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}
}
