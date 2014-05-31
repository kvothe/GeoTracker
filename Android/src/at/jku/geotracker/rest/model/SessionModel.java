package at.jku.geotracker.rest.model;

import at.jku.geotracker.rest.interfaces.ResponseListener;

public class SessionModel extends AbstractModel {

	private String sessionId;

	public SessionModel(String sessionId, ResponseListener listener) {
		super(listener);
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
