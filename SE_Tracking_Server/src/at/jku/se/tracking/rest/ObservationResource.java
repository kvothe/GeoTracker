package at.jku.se.tracking.rest;

import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.jku.se.tracking.UserSession;
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
	public Response startObservation(@Context HttpServletRequest request, String data) {
		UserSession userSession = UserResource.checkSession(request);
		if (userSession != null) {
			System.out.println("ObservationResource.start -> credentials ok");
			long starttime = System.currentTimeMillis();

			// parse json and retrieve parameter
			@SuppressWarnings("rawtypes")
			Map map = MarshallingService.getParser().parseJson(data);
			map = MarshallingService.unpackMap(map);
			String targetUsername;
			try {
				targetUsername = map.get("targetUsername").toString();
			} catch (NullPointerException e) {
				return ResponseGenerator.generateBadRequest();
			}

			try {
				UserObject targetUser = DatabaseService.queryUser(targetUsername);
				if (targetUser != null) {
					// check if user is already observing observed
					if (DatabaseService.queryTrackingSessions(userSession.getUserId(), targetUser.getId(), true).size() > 0) {
						System.out.println("ObservationResource.start -> already tracking");
						return ResponseGenerator
								.generateServerError("you are already tracking " + targetUser.getName());
					} else {
						// start observation
						DatabaseService.startTrackingSession(userSession.getUserId(), targetUser.getId(), starttime);
						System.out.println("ObservationResource.start -> ok");
						return ResponseGenerator.generateOK();
					}
				} else {
					System.out.println("ObservationResource.start -> generateInvalidUserResponse");
					return ResponseGenerator.generateInvalidUserResponse();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				return ResponseGenerator.generateSQLError();
			}
		}
		return ResponseGenerator.generateNotAuthorized();
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
	public Response stopObservation(@Context HttpServletRequest request, String data) {
		UserSession userSession = UserResource.checkSession(request);
		if (userSession != null) {
			long endtime = System.currentTimeMillis();
			boolean targetIsObserver;
			String targetUsername;

			// parse json and retrieve parameter
			@SuppressWarnings("rawtypes")
			Map map = MarshallingService.getParser().parseJson(data);
			map = MarshallingService.unpackMap(map);

			try {
				targetIsObserver = Boolean.parseBoolean(map.get("targetIsObserver").toString());
				targetUsername = map.get("targetUsername").toString();
			} catch (NullPointerException e) {
				return ResponseGenerator.generateBadRequest();
			}

			try {
				UserObject targetUser = DatabaseService.queryUser(targetUsername);
				// get active sessions of user depending on userIsObserver
				if (targetUser != null) {
					TrackingSessionObject session = null;
					for (TrackingSessionObject t : DatabaseService.queryTrackingSessions(userSession.getUserId(),
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
						DatabaseService.stopTrackingSession(session.getId(), endtime, userSession.getUserId());
						return ResponseGenerator.generateOK();
					}
				} else {
					System.out.println("ObservationResource.stop -> generateInvalidUserResponse");
					return ResponseGenerator.generateInvalidUserResponse();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				return ResponseGenerator.generateSQLError();
			}
		}
		return ResponseGenerator.generateNotAuthorized();
	}
}
