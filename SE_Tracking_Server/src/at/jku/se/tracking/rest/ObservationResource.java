package at.jku.se.tracking.rest;

import java.sql.SQLException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.TrackingSessionObject;
import at.jku.se.tracking.database.UserObject;
import at.jku.se.tracking.messages.serialization.MarshallingService;
import at.jku.se.tracking.rest.response.ResponseGenerator;

@Path("/observation")
public class ObservationResource {

	/**
	 * processes post request for starting an observation.
	 * 
	 * @param request
	 *            map with username, password and observed
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/start")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response startObservation(String request) {
		long starttime = System.currentTimeMillis();

		// parse json and retrieve parameter
		@SuppressWarnings("rawtypes")
		Map map = MarshallingService.getParser().parseJson(request);
		map = MarshallingService.unpackMap(map);
		String username;
		String password;
		String observedUsername;
		try {
			username = map.get("username").toString().trim();
			password = map.get("password").toString().trim();
			observedUsername = map.get("targetUsername").toString();
		} catch (NullPointerException e) {
			return ResponseGenerator.generateBadRequest();
		}

		try {
			// check pw
			if (LoginResource.checkCredentials(username, password)) {
				System.out.println("ObservationResource.start -> credentials ok");
				UserObject user = DatabaseService.queryUser(username);
				UserObject target = DatabaseService.queryUser(observedUsername);
				if (user != null) {
					// check if user is already observing observed
					if (DatabaseService.queryTrackingSessions(user.getId(), target.getId(), true).size() > 0) {
						System.out.println("ObservationResource.start -> already tracking");
						return ResponseGenerator.generateServerError("you are already tracking " + target.getName());
					} else {
						// start observation
						DatabaseService.startTrackingSession(user.getId(), target.getId(), starttime);
						System.out.println("ObservationResource.start -> ok");
						return ResponseGenerator.generateOK();
					}
				} else {
					System.out.println("ObservationResource.start -> generateInvalidUserResponse");
					return ResponseGenerator.generateInvalidUserResponse();
				}
			} else {
				System.out.println("ObservationResource.start -> invalid credentials");
				return ResponseGenerator.generateNotAuthorized();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseGenerator.generateSQLError();
		}
	}

	/**
	 * processes put request for stopping an observation
	 * 
	 * @param request
	 *            map with username, password, userIsObserver and secondUsername
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PUT
	@Path("/stop")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response stopObservation(String request) {
		long endtime = System.currentTimeMillis();

		// parse json and retrieve parameter
		@SuppressWarnings("rawtypes")
		Map map = MarshallingService.getParser().parseJson(request);
		map = MarshallingService.unpackMap(map);

		String username;
		String password;
		boolean targetIsObserver;
		String targetUsername;
		try {
			username = map.get("username").toString();
			password = map.get("password").toString();
			targetIsObserver = Boolean.parseBoolean(map.get("targetIsObserver").toString());
			targetUsername = map.get("targetUsername").toString();
		} catch (NullPointerException e) {
			return ResponseGenerator.generateBadRequest();
		}

		try {
			// check pw
			if (LoginResource.checkCredentials(username, password)) {
				UserObject user = DatabaseService.queryUser(username);
				UserObject targetUser = DatabaseService.queryUser(targetUsername);
				if (user != null) {
					// get active sessions of user depending on userIsObserver
					TrackingSessionObject session = null;
					for (TrackingSessionObject t : DatabaseService.queryTrackingSessions(user.getId(),
							!targetIsObserver, targetIsObserver, true)) {
						if (!targetIsObserver) {
							if (t.getObserved() == targetUser.getId()) {
								session = t;
								break;
							}
						} else {
							if (t.getObserver() == targetUser.getId()) {
								session = t;
								break;
							}
						}
					}
					if (session == null)
						return ResponseGenerator.generateServerError("session not found");
					else {
						// stop tracking session
						DatabaseService.stopTrackingSession(session.getId(), endtime, user.getId());
						return ResponseGenerator.generateOK();
					}
				} else {
					return ResponseGenerator.generateInvalidUserResponse();
				}
			} else
				return ResponseGenerator.generateNotAuthorized();
		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseGenerator.generateSQLError();
		}
	}
}
