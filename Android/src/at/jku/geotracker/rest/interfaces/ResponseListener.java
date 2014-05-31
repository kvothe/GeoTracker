package at.jku.geotracker.rest.interfaces;

import at.jku.geotracker.rest.model.ResponseObject;

public interface ResponseListener {
	public void receivedResponse(ResponseObject response);
}
