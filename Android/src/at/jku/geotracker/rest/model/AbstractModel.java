package at.jku.geotracker.rest.model;

import at.jku.geotracker.rest.interfaces.ResponseListener;

abstract class AbstractModel {
	ResponseListener listener;

	AbstractModel(ResponseListener listener) {
		this.listener = listener;
	}

	public ResponseListener getListener() {
		return listener;
	}

	public void setListener(ResponseListener listener) {
		this.listener = listener;
	}
}
