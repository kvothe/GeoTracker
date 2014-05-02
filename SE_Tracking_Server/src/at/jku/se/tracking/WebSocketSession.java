package at.jku.se.tracking;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.GeolocationObject;
import at.jku.se.tracking.database.TrackingSessionObject;
import at.jku.se.tracking.database.UserObject;
import at.jku.se.tracking.messages.MsgError;
import at.jku.se.tracking.messages.MsgLocationUpdate;
import at.jku.se.tracking.messages.MsgLogin;
import at.jku.se.tracking.messages.MsgLogout;
import at.jku.se.tracking.messages.MsgOk;
import at.jku.se.tracking.messages.MsgRegister;
import at.jku.se.tracking.messages.MsgRequestSessionList;
import at.jku.se.tracking.messages.MsgRequestSessionTrack;
import at.jku.se.tracking.messages.MsgRequestSetSettings;
import at.jku.se.tracking.messages.MsgRequestSettings;
import at.jku.se.tracking.messages.MsgRequestUserList;
import at.jku.se.tracking.messages.MsgResponseList;
import at.jku.se.tracking.messages.MsgSession;
import at.jku.se.tracking.messages.MsgStartObservation;
import at.jku.se.tracking.messages.MsgStopObservation;
import at.jku.se.tracking.messages.serialization.AMessage;
import at.jku.se.tracking.messages.serialization.InvalidMessageException;
import at.jku.se.tracking.messages.serialization.MarshallingService;
import at.jku.se.tracking.utils.PasswordEncryptionService;

import com.json.exceptions.JSONParsingException;

@WebSocket
public class WebSocketSession {
	private RemoteEndpoint remote;
	private UserSession session;

	// ------------------------------------------------------------------------

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.println("WebSocket Opened");
		session.setIdleTimeout(SessionObserver.SESSION_EXPIRATION_DURATION);
		// --
		this.remote = session.getRemote();
	}

	// ------------------------------------------------------------------------

	@OnWebSocketMessage
	public void onMessage(String message) {
		// Store authenticated users remote session for push messages
		System.out.println("--> " + message);
		// --
		/*
		 * try { remote.sendString(message); } catch (IOException e) { e.printStackTrace(); }
		 */
		// --
		try {
			AMessage m = MarshallingService.fromJSON(message);
			switch (m.getType()) {
			case REGISTRATION:
				MsgRegister register = (MsgRegister) m;
				handleRegistration(register);
				break;
			case LOGIN:
				MsgLogin login = (MsgLogin) m;
				handleLogin(login);
				break;
			case SESSION:
				MsgSession sessionCheck = (MsgSession) m;
				handleSessionCheck(sessionCheck);
				break;
			case LOGOUT:
				MsgLogout logout = (MsgLogout) m;
				handleLogout(logout);
				break;
			case LOCATION_UPDATE:
				MsgLocationUpdate locUpdate = (MsgLocationUpdate) m;
				handleLocationUpdate(locUpdate);
				break;
			case USER_LIST:
				MsgRequestUserList userList = (MsgRequestUserList) m;
				handleUserList(userList);
				break;
			case SESSION_LIST:
				MsgRequestSessionList requestTrackingSessions = (MsgRequestSessionList) m;
				handleSessionList(requestTrackingSessions);
				break;
			case SESSION_POINTS:
				MsgRequestSessionTrack requestSessionTrack = (MsgRequestSessionTrack) m;
				handleSessionPoints(requestSessionTrack);
				break;
			case START_OBSERVATION:
				MsgStartObservation startObservation = (MsgStartObservation) m;
				handleStartObservation(startObservation);
				break;
			case STOP_OBSERVATION:
				MsgStopObservation stopObservation = (MsgStopObservation) m;
				handleStopObservation(stopObservation);
				break;
			case GET_SETTINGS:
				MsgRequestSettings requestSettings = (MsgRequestSettings) m;
				handleGetSettings(requestSettings);
				break;
				
			case SET_SETTINGS:
				MsgRequestSetSettings requestSetSettings = (MsgRequestSetSettings) m;
				handleSetSettings(requestSetSettings);
				break;
			
			default:
				break;
			}
		} catch (InvalidMessageException e) {
			e.printStackTrace();
			// for now do nothing
		} catch (JSONParsingException e) {
			e.printStackTrace();
			// for now do nothing
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ------------------------------------------------------------------------

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.println("WebSocket Closed. Code:" + statusCode);
		SessionObserver.closeSession(session);
	}

	// ------------------------------------------------------------------------

	public void sendMessage(AMessage message) throws IOException {
		String jsonString = MarshallingService.toJSON(message);
		System.out.println("<-- " + jsonString);
		remote.sendString(jsonString);
	}

	// ------------------------------------------------------------------------

	private void handleRegistration(MsgRegister registration) throws IOException {
		try {
			String username = registration.getUsername().trim();
			String password = registration.getPassword().trim();
			// --
			UserObject user = DatabaseService.queryUser(username);
			if (user != null) {
				sendMessage(new MsgError(registration.getConversationId(), "username already taken"));
			} else {
				// Encrypt password
				byte[] salt = PasswordEncryptionService.generateSalt();
				byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword(password, salt);
				// Instantiate User Object
				user = new UserObject(username, encryptedPassword, salt, registration.isObservable());
				// Store user
				long userId = DatabaseService.insertUser(user);
				// perform login
				if (userId != -1) {
					String sessionId = UUID.randomUUID().toString();
					this.session = new UserSession(sessionId, user.getName(), userId, System.currentTimeMillis());
					// --
					System.out.println("User <" + userId + "> registered session <" + sessionId + ">");
					SessionObserver.registerSession(this.session, this);
					// --
					sendMessage(new MsgOk(registration.getConversationId(), sessionId));
				} else {
					sendMessage(new MsgError(registration.getConversationId(), "problem adding new user"));
				}
			}
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			sendMessage(new MsgError(registration.getConversationId(), "Something went terribly wrong, try again if you dare..."));
		} catch (SQLException e) {
			e.printStackTrace();
			sendMessage(new MsgError(registration.getConversationId(), "Well, something went wrong. Go pester your admin!"));
		}
	}
	
	// ------------------------------------------------------------------------

	private void handleGetSettings(MsgRequestSettings settings) throws IOException {
		try {
			String username = settings.getUsername().trim();
			UserObject user = DatabaseService.queryUser(username);
			if (user != null) {
				boolean settingObservable = user.isObservable();
					sendMessage(new MsgOk(settings.getConversationId(), String.valueOf(settingObservable)));
			} else {
				sendMessage(new MsgError(settings.getConversationId(), "invalid username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sendMessage(new MsgError(settings.getConversationId(), "Well, something went wrong. Go pester the site admin!"));
		}
	}
	
	private void handleSetSettings(MsgRequestSetSettings settings) throws IOException {
		try {
			String username = settings.getUsername().trim();
			UserObject user = DatabaseService.queryUser(username);
			if (user != null) {
				boolean settingObservable = settings.getObservable();
				DatabaseService.setObservableSetting(user.getId(), settingObservable);
				sendMessage(new MsgOk(settings.getConversationId(), "Settings successfully changed"));
			} else {
				sendMessage(new MsgError(settings.getConversationId(), "invalid username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sendMessage(new MsgError(settings.getConversationId(), "Well, something went wrong. Go pester the site admin!"));
		}
	}

	// ------------------------------------------------------------------------

	private void handleLogin(MsgLogin login) throws IOException {
		try {
			String username = login.getUsername().trim();
			String password = login.getPassword().trim();
			// --
			UserObject user = DatabaseService.queryUser(username);
			if (user != null) {
				boolean authenticated = PasswordEncryptionService.authenticate(password, user.getEncryptedPassword(), user.getSalt());
				// --
				if (authenticated) {
					String sessionId = UUID.randomUUID().toString();
					this.session = new UserSession(sessionId, user.getName(), user.getId(), System.currentTimeMillis());
					// --
					System.out.println("User <" + user.getId() + "> registered session <" + sessionId + ">");
					SessionObserver.registerSession(this.session, this);
					// --
					sendMessage(new MsgOk(login.getConversationId(), sessionId));
				}else{
					sendMessage(new MsgError(login.getConversationId(), "invalid credentials"));
				}
			} else {
				sendMessage(new MsgError(login.getConversationId(), "invalid credentials"));
			}
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			sendMessage(new MsgError(login.getConversationId(), "Something went terribly wrong, try again if you dare..."));
		} catch (SQLException e) {
			e.printStackTrace();
			sendMessage(new MsgError(login.getConversationId(), "Well, something went wrong. Go pester the site admin!"));
		}
	}

	// ------------------------------------------------------------------------

	private void handleSessionCheck(MsgSession sessionCheck) throws IOException {
		if (checkSession(sessionCheck)) {
			sendMessage(new MsgOk(sessionCheck.getConversationId(), session.getUsername()));
		}
	}

	// ------------------------------------------------------------------------

	private void handleLogout(MsgLogout logout) throws IOException {
		SessionObserver.unregisterSession(this.session);
		this.session = null;
	}

	// ------------------------------------------------------------------------

	private void handleLocationUpdate(MsgLocationUpdate location) throws IOException {
		if (checkSession(location)) {
			try {
				long timestamp = System.currentTimeMillis(); // use server time for timestamps to avoid out of sync
																// issues
				GeolocationObject geoObject = new GeolocationObject(session.getUserId(), timestamp, location);
				// --
				if (DatabaseService.insertLocation(geoObject)) {
					sendMessage(new MsgOk(location.getConversationId()));
				} else {
					sendMessage(new MsgError(location.getConversationId(), "We have a problem storing your location..."));
				}
				// TODO: push update
				SessionObserver.pushLocationUpdate(timestamp, session.getUserId(), session.getUsername(), location.getLongitude(),
						location.getLatitude(), location.getAccuracy());
			} catch (SQLException e) {
				e.printStackTrace();
				sendMessage(new MsgError(location.getConversationId(), "Well, something went wrong. Go pester the site admin!"));
			}
		}
	}

	// ------------------------------------------------------------------------

	private void handleUserList(MsgRequestUserList request) throws IOException {
		if (checkSession(request)) {
			try {
				List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
				// --
				List<UserObject> users = DatabaseService.queryUsers(request.getOnlyObservable());
				// --
				for (UserObject u : users) {
					if (u.getId() != session.getUserId()) {
						// crude implementation due to workaround for quick-json bug with trailing commas
						Map<String, Object> user = new HashMap<String, Object>();
						user.put("name", u.getName());
						user.put("observable", u.isObservable());
						user.put("online", false);
						// --
						userList.add(user);
					}
				}
				// --
				sendMessage(new MsgResponseList(request.getConversationId(), userList));
			} catch (SQLException e) {
				e.printStackTrace();
				sendMessage(new MsgError(request.getConversationId(), "Well, something went wrong. Go pester the site admin!"));
			}
		}
	}

	// ------------------------------------------------------------------------

	private void handleSessionList(MsgRequestSessionList request) throws IOException {
		if (checkSession(request)) {
			try {
				List<Map<String, Object>> sessionList = new ArrayList<Map<String, Object>>();
				// --
				List<TrackingSessionObject> sessions = DatabaseService.queryTrackingSessions(session.getUserId(), true, false, false);
				// --
				for (TrackingSessionObject s : sessions) {
					// crude implementation due to workaround for quick-json bug with trailing commas
					Map<String, Object> session = new HashMap<String, Object>();
					UserObject u = DatabaseService.queryUser(s.getObserved());
					session.put("observation-id", s.getId());
					if (u != null) {
						session.put("observed", u.getName());
					}
					session.put("starttime", s.getStarttime());
					session.put("endtime", s.getEndtime());
					// --
					sessionList.add(session);
				}
				// --
				sendMessage(new MsgResponseList(request.getConversationId(), sessionList));
			} catch (SQLException e) {
				e.printStackTrace();
				sendMessage(new MsgError(request.getConversationId(), "Well, something went wrong. Go pester the site admin!"));
			}
		}
	}

	// ------------------------------------------------------------------------

	private void handleSessionPoints(MsgRequestSessionTrack request) throws IOException {
		if (checkSession(request)) {
			try {
				List<Map<String, Object>> pointList = new ArrayList<Map<String, Object>>();
				// --
				List<GeolocationObject> points = DatabaseService.getTrackingSessionPoints(request.getObservationId());
				// --
				for (GeolocationObject p : points) {
					// crude implementation due to workaround for quick-json bug with trailing commas
					Map<String, Object> point = new HashMap<String, Object>();
					point.put("timestamp", p.getTimestamp());
					point.put("longitude", p.getLongitude());
					point.put("latitude", p.getLatitude());
					point.put("accuracy", p.getAccuracy());
					// --
					pointList.add(point);
				}
				// --
				sendMessage(new MsgResponseList(request.getConversationId(), pointList));
			} catch (SQLException e) {
				e.printStackTrace();
				sendMessage(new MsgError(request.getConversationId(), "Well, something went wrong. Go pester the site admin!"));
			}
		}
	}

	// ------------------------------------------------------------------------

	private void handleStartObservation(MsgStartObservation request) throws IOException {
		if (checkSession(request)) {
			try {
				UserObject observed = DatabaseService.queryUser(request.getObservedUser());
				if (observed != null) {
					if (!observed.isObservable()) {
						sendMessage(new MsgError(request.getConversationId(), observed.getName() + " is not observable"));
						return;
					}
					// check if this user already observes the requested user
					List<TrackingSessionObject> sessions = DatabaseService.queryTrackingSessions(session.getUserId(), observed.getId(), true);
					if (sessions.size() > 0) {
						sendMessage(new MsgError(request.getConversationId(), "You are already observing " + observed.getName()));
						return;
					}
					// --
					DatabaseService.startTrackingSession(session.getUserId(), observed.getId(), System.currentTimeMillis());
					// --
					sendMessage(new MsgOk(request.getConversationId(), observed.getName()));
					// --
					SessionObserver.pushNotifyStartObservation(observed.getId(), session.getUsername());
				} else {
					sendMessage(new MsgError(request.getConversationId(), "User doesn't exist"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				sendMessage(new MsgError(request.getConversationId(), "Well, something went wrong. Go pester the site admin!"));
			}
		}
	}

	// ------------------------------------------------------------------------

	private void handleStopObservation(MsgStopObservation request) throws IOException {
		if (checkSession(request)) {
			try {
				long endtime = System.currentTimeMillis();
				// --
				UserObject user = DatabaseService.queryUser(request.getUser());
				if (user != null) {
					long observationId = request.getObservationId();
					long observed = request.userIsObserver() ? session.getUserId() : user.getId();
					long observer = request.userIsObserver() ? user.getId() : session.getUserId();
					String observerName = request.userIsObserver() ? user.getName() : session.getUsername();
					// --
					if (observationId == -1) {
						// check if this observer observes the requested user
						List<TrackingSessionObject> sessions = DatabaseService.queryTrackingSessions(observer, observed, true);
						if (sessions.size() == 1) {
							observationId = sessions.get(0).getId();
						} else {
							if (request.userIsObserver()) {
								sendMessage(new MsgError(request.getConversationId(), "You are not being observed by " + user.getName()));
							} else {
								sendMessage(new MsgError(request.getConversationId(), "You are not observing " + user.getName()));
							}
							return;
						}
					}
					// --
					boolean success = DatabaseService.stopTrackingSession(observationId, endtime, session.getUserId());
					// --
					if (success) {
						sendMessage(new MsgOk(request.getConversationId(), user.getName()));
						SessionObserver.pushNotifyStopObservation(observed, observerName);
					} else {
						sendMessage(new MsgError(request.getConversationId(), "Error while stopping session"));
					}
				} else {
					sendMessage(new MsgError(request.getConversationId(), "User doesn't exist"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				sendMessage(new MsgError(request.getConversationId(), "Well, something went wrong. Go pester the site admin!"));
			}
		}
	}

	// ------------------------------------------------------------------------

	private boolean checkSession(AMessage message) throws IOException {
		String sessionId = message.getSessionId();
		if (sessionId == null || sessionId.length() == 0) {
			sendMessage(new MsgError(message.getConversationId(), "invalid session-id"));
			return false;
		}
		// --
		if (session == null) {
			if (SessionObserver.hasSession(sessionId)) {
				this.session = SessionObserver.getSession(sessionId);
			} else {
				sendMessage(new MsgError(message.getConversationId(), "invalid session"));
				return false;
			}
		} else if (!session.isSession(sessionId)) {
			sendMessage(new MsgError(message.getConversationId(), "invalid session"));
			return false;
		} else if (session.isExpired()) {
			sendMessage(new MsgError(message.getConversationId(), "session expired"));
			return false;
		} else {
			session.renew();
		}
		return true;
	}
}