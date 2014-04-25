package at.jku.se.tracking;

public class UserSession {
	private String sessionId;
	private String username;
	private long userId;
	private long timestamp;

	// ------------------------------------------------------------------------

	public UserSession(String sessionId, String username, long userId, long timestamp) {
		this.sessionId = sessionId;
		this.username = username;
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

	public String getUsername() {
		return username;
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
