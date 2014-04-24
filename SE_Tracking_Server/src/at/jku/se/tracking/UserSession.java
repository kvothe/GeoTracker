package at.jku.se.tracking;

public class UserSession {
	private String sessionId;
	private long userId;
	private long timestamp;

	// ------------------------------------------------------------------------

	public UserSession(String sessionId, long userId, long timestamp) {
		this.sessionId = sessionId;
		this.userId = userId;
		this.timestamp = timestamp;
	}

	// ------------------------------------------------------------------------

	public boolean isSession(String sessionId) {
		return this.sessionId.equals(sessionId);
	}

	// ------------------------------------------------------------------------

	public boolean isExpired() {
		return System.currentTimeMillis() > this.timestamp + SessionObserver.SESSION_EXPIRATION_DURATION;
	}

	// ------------------------------------------------------------------------

	public void renew() {
		this.timestamp = System.currentTimeMillis();
	}

	// ------------------------------------------------------------------------

	public long getUserId() {
		return userId;
	}

	// ------------------------------------------------------------------------

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserSession) {
			UserSession o = (UserSession) obj;
			// --
			if (o.sessionId != null && o.sessionId.equals(this.sessionId) && o.userId == this.userId) {
				return true;
			}
		}
		return false;
	}
}
