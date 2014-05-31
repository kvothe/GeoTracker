package at.jku.se.tracking.rest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.jku.se.tracking.UserSession;
import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.UserObject;
import at.jku.se.tracking.messages.serialization.MarshallingService;
import at.jku.se.tracking.rest.response.ResponseGenerator;

@Path("/settings")
public class SettingsResource {

	/**
	 * get observable setting of user
	 * 
	 * @param username
	 * @return
	 */
	@GET
	@Path("/isobservable/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObservable(@Context HttpServletRequest request, @PathParam("username") String username) {
		UserSession userSession = UserResource.checkSession(request);
		if (userSession != null) {
			try {
				UserObject user = DatabaseService.queryUser(username);
				if (user != null) {
					Map<String, Object> responseEntity = new HashMap<String, Object>();
					responseEntity.put("observable", user.isObservable());
					return ResponseGenerator.generateOK(responseEntity);
				} else {
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
	 * put request for setting visibility of user
	 * 
	 * @param request
	 *            map with username, password and observable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PUT
	@Path("/setobservable")
	public Response setObservable(@Context HttpServletRequest request, String data) {
		UserSession userSession = UserResource.checkSession(request);
		if (userSession != null) {
			// parse json and retrieve parameter
			@SuppressWarnings("rawtypes")
			Map map = MarshallingService.getParser().parseJson(data);
			map = MarshallingService.unpackMap(map);
			boolean observable;
			try {
				observable = Boolean.parseBoolean(map.get("observable").toString());
			} catch (NullPointerException e) {
				return ResponseGenerator.generateBadRequest();
			}
			// --
			try {
				DatabaseService.setObservableSetting(userSession.getUserId(), observable);
				return ResponseGenerator.generateOK();
			} catch (SQLException e) {
				e.printStackTrace();
				return ResponseGenerator.generateSQLError();
			}
		}
		return ResponseGenerator.generateNotAuthorized();
	}
}
