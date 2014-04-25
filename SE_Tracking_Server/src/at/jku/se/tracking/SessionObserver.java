package at.jku.se.tracking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import at.jku.se.tracking.database.UserObject;
import at.jku.se.tracking.messages.MsgNotification;

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

	/**
	 * Completely unregister this session, invalidating its session id.
	 * 
	 * @param session
	 */
	public static void unregisterSession(UserSession session) {
		synchronized (SESSIONS) {
			if (SESSIONS.containsKey(session)) {
				SESSIONS.remove(session);
			}
		}
	}

	/**
	 * Signal that this sessions socket is closed but the session id is still valid.
	 * 
	 * @param session
	 */
	public static void closeSession(UserSession session) {
		synchronized (SESSIONS) {
			if (SESSIONS.containsKey(session)) {
				SESSIONS.put(session, null);
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

	public static void pushLocationUpdate(long userId, double newLong, double newLat) {
		// TODO:
		// 1) query database for users currently observing "userId"
		// 2) push new location to these users
	}

	// ------------------------------------------------------------------------

	public static void pushNotifyStartObservation(long observedId, String observerName) {
		List<WebSocketSession> notifySessions = new ArrayList<WebSocketSession>();
		// collect relevant sessions
		synchronized (SESSIONS) {
			for (Entry<UserSession, WebSocketSession> s : SESSIONS.entrySet()) {
				if (s.getKey().getUserId() == observedId && s.getValue() != null) {
					notifySessions.add(s.getValue());
				}
			}
		}
		// --
		for (WebSocketSession s : notifySessions) {
			try {
				s.sendMessage(new MsgNotification(observerName + " is now observing you"));
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	// ------------------------------------------------------------------------

	public static void pushNotifyStopObservation(long observedId, String observerName) {
		List<WebSocketSession> notifySessions = new ArrayList<WebSocketSession>();
		// collect relevant sessions
		synchronized (SESSIONS) {
			for (Entry<UserSession, WebSocketSession> s : SESSIONS.entrySet()) {
				if (s.getKey().getUserId() == observedId && s.getValue() != null) {
					notifySessions.add(s.getValue());
				}
			}
		}
		// --
		for (WebSocketSession s : notifySessions) {
			try {
				s.sendMessage(new MsgNotification(observerName + " has stopped observing you"));
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
