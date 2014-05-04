package at.jku.se.tracking;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.TrackingSessionObject;
import at.jku.se.tracking.messages.MsgLocationUpdate;
import at.jku.se.tracking.messages.MsgNotification;
import at.jku.se.tracking.messages.serialization.AMessage;

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

	public static void pushLocationUpdate(long timestamp, long userId, String username, double newLong, double newLat, double newAccuracy) {
		try {
			MsgLocationUpdate locationUpdate = new MsgLocationUpdate(username, newLat, newLong, newAccuracy, timestamp); // TODO:
																															// support
																															// altitude,
																															// speed,
																															// heading
			// --
			List<Long> notifyUsers = new ArrayList<Long>();
			List<TrackingSessionObject> observations = DatabaseService.queryTrackingSessions(userId, false, true, true);
			for (TrackingSessionObject s : observations) {
				long observer = s.getObserver();
				if (!notifyUsers.contains(observer)) {
					notifyUsers.add(observer);
				}
			}
			long[] tmp = new long[notifyUsers.size()];
			for (int i = 0; i < notifyUsers.size(); i++) {
				tmp[i] = notifyUsers.get(i);
			}
			sendMessageTo(locationUpdate, tmp);
			// --
			System.out.println("PUSHING LOCATION UPDATE");
		} catch (SQLException e) {
			System.err.println("Error while pushing location update...");
			e.printStackTrace();
		}
	}
	// ------------------------------------------------------------------------

	public static void pushNotifyStartObservation(long observedId, String observerName) {
		sendMessageTo(new MsgNotification(observerName + " is now observing you"), observedId);
	}

	// ------------------------------------------------------------------------

	public static void pushNotifyStopObservation(long observedId, String observerName) {
		sendMessageTo(new MsgNotification(observerName + " has stopped observing you"), observedId);
	}

	// ------------------------------------------------------------------------

	private static void sendMessageTo(AMessage msg, long... receipients) {
		List<WebSocketSession> notifySessions = new ArrayList<WebSocketSession>();
		// collect relevant sessions
		synchronized (SESSIONS) {
			for (Entry<UserSession, WebSocketSession> s : SESSIONS.entrySet()) {
				for (long receipient : receipients) {
					if (s.getKey().getUserId() == receipient && s.getValue() != null) {
						if (!notifySessions.contains(s.getValue())) {
							notifySessions.add(s.getValue());
						}
					}
				}
			}
		}
		// --
		for (WebSocketSession s : notifySessions) {
			try {
				s.sendMessage(msg);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
