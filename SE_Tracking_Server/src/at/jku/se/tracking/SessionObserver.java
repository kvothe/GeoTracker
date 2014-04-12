package at.jku.se.tracking;

import java.util.HashMap;
import java.util.Map;

public class SessionObserver {
	private static Map<Double, WebSocketSession> SESSIONS;

	// ------------------------------------------------------------------------

	static {
		SESSIONS = new HashMap<Double, WebSocketSession>();
	}

	// ------------------------------------------------------------------------

	public static void registerSession(double userId, WebSocketSession session) {
		synchronized (SESSIONS) {
			if (SESSIONS.containsKey(userId)) {
				// TODO remove old session; might be necessary to inform client
			}
			SESSIONS.put(userId, session);
		}
	}

	// ------------------------------------------------------------------------

	public static void unregisterSession(double userId) {
		synchronized (SESSIONS) {
			if (SESSIONS.containsKey(userId)) {
				SESSIONS.remove(userId);
			}
		}
	}

	// ------------------------------------------------------------------------

	public static void pushLocationUpdate(double userId, double newLong, double newLat) {
		// TODO:
		// 1) query database for users currently observing "userId"
		// 2) push new location to these users
	}
}
