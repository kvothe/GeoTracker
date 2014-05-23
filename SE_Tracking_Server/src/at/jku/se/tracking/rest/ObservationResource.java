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
	 * @param request map with username, password and observedId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/start")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response startObservation(String request){
		long starttime = System.currentTimeMillis();
		
		//parse json and retrieve parameter
		@SuppressWarnings("rawtypes")
		Map map = MarshallingService.getParser().parseJson(request);
		map = MarshallingService.unpackMap(map);
		String username;
		String password;
		long observedId;
		try {
			username = map.get("username").toString().trim();
			password = map.get("password").toString().trim();
			observedId = Long.parseLong(map.get("observed").toString());
		} catch (NullPointerException e) {
			return ResponseGenerator.generateBadRequest();
		}
		
		try{
			//check pw
			if(LoginResource.checkCredentials(username, password)){
				UserObject user = DatabaseService.queryUser(username);
				UserObject observed = DatabaseService.queryUser(observedId);
				if (user != null) {
					//check if user is already observing observed
					if(DatabaseService.queryTrackingSessions(user.getId(), observed.getId(), true).size() > 0){
						return ResponseGenerator.generateServerError("you are already tracking " + observed.getName());
					}else{
						//start observation
						DatabaseService.startTrackingSession(user.getId(), observed.getId(), starttime);
						return ResponseGenerator.generateOK();
					}
				} else {
					return ResponseGenerator.generateInvalidUserResponse();
				}
			}else
				return ResponseGenerator.generateNotAuthorized();
		}catch(SQLException e){
			e.printStackTrace();
			return ResponseGenerator.generateSQLError();
		}
	}
	
	/**
	 * processes put request for stopping an observation
	 * @param request map with username, password, userIsObserver and observationId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PUT
	@Path("/stop")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response stopObservation(String request){
		long endtime = System.currentTimeMillis();
		
		//parse json and retrieve parameter
		@SuppressWarnings("rawtypes")
		Map map = MarshallingService.getParser().parseJson(request);
		map = MarshallingService.unpackMap(map);

		String username;
		String password;
		boolean userIsObserver;
		long observationId;
		try {
			username = map.get("username").toString();
			password = map.get("password").toString();
			userIsObserver = Boolean.parseBoolean(map.get("isObserver").toString());
			observationId = Long.parseLong(map.get("observationId").toString());
		} catch (NullPointerException e) {
			return ResponseGenerator.generateBadRequest();
		}

		try{
			//check pw
			if(LoginResource.checkCredentials(username, password)){
				UserObject user = DatabaseService.queryUser(username);
				if (user != null) {
					//get active sessions of user depending on userIsObserver
					TrackingSessionObject session = null;
					for(TrackingSessionObject t : DatabaseService.queryTrackingSessions(user.getId(), userIsObserver, !userIsObserver, true)){
						if(t.getId() == observationId){
							session = t;
							break;
						}
					}
					if(session == null)
						return ResponseGenerator.generateServerError("session not found");
					else{
						//stop tracking session
						DatabaseService.stopTrackingSession(observationId, endtime, user.getId());
						return ResponseGenerator.generateOK();
					}
				} else {
					return ResponseGenerator.generateInvalidUserResponse();
				}
			}else
				return ResponseGenerator.generateNotAuthorized();
		}catch(SQLException e){
			e.printStackTrace();
			return ResponseGenerator.generateSQLError();
		}
	}
}