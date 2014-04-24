package at.jku.se.tracking;

import java.util.HashMap;
import java.util.Map;

public class SessionObserver {
	static final long SESSION_EXPIRATION_DURATION = 1 * 60 * 60 * 1000; // 1 hour of inactivity

	// ------------------------------------------------------------------------

	private static Map<UserSession, WebSocketSession> SESSIONS;

	// ------------------------------------------------------------------------

	static {
		SESSIONS = new HashMap<UserSession, WebSocketSession>();
	}

	// ------------------------------------------------------------------------

	public static void registerSession(UserSession session, WebSocketSession socket) {
		synchronized (SESSIONS) {
			if (SESSIONS.containsKey(session)) {
				// TODO remove old session; might be necessary to inform client
			}
			SESSIONS.put(session, socket);
		}
	}

	// ------------------------------------------------------------------------

	public static void unregisterSession(UserSession session) {
		synchronized (SESSIONS) {
			if (SESSIONS.containsKey(session)) {
				SESSIONS.remove(session);
			}
		}
	}

	// ------------------------------------------------------------------------

	public static boolean hasSession(String sessionId) {
		synchronized (SESSIONS) {
			if (SESSIONS != null) {
				for (UserSession session : SESSIONS.keySet()) {
					if (session.isSession(sessionId)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// ------------------------------------------------------------------------

	public static UserSession getSession(String sessionId) {
		synchronized (SESSIONS) {
			if (SESSIONS != null) {
				for (UserSession session : SESSIONS.keySet()) {
					if (session.isSession(sessionId)) {
						return session;
					}
				}
			}
		}
		return null;
	}

	// ------------------------------------------------------------------------

	public static void pushLocationUpdate(double userId, double newLong, double newLat) {
		// TODO:
		// 1) query database for users currently observing "userId"
		// 2) push new location to these users
	}
}
