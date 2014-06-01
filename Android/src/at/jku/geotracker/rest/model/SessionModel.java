package at.jku.geotracker.rest.model;

import at.jku.geotracker.rest.interfaces.ResponseListener;

public class SessionModel extends AbstractModel {

	private long sessionId;

	public SessionModel(long sessionId, ResponseListener listener) {
		super(listener);
		this.sessionId = sessionId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
}
