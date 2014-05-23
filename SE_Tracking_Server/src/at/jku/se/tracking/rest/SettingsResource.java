package at.jku.se.tracking.rest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.UserObject;
import at.jku.se.tracking.messages.serialization.MarshallingService;
import at.jku.se.tracking.rest.response.ResponseGenerator;

@Path("/settings")
public class SettingsResource {
	
	/**
	 * get observable setting of user
	 * @param username
	 * @return
	 */
	@GET
	@Path("/getobservable/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObservable(@PathParam("username") String username) {
		
		try {
			UserObject user = DatabaseService.queryUser(username);
			if (user != null) {				
				Map<String, Object> responseEntity = new HashMap<String, Object>();
				responseEntity.put("observable",  user.isObservable());
				return ResponseGenerator.generateOK(responseEntity);
			} else {
				return ResponseGenerator.generateInvalidUserResponse();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseGenerator.generateSQLError();
		}
	}

	/**
	 * put request for setting visibility of user
	 * @param request map with username, password and observable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PUT
	@Path("/setobservable")
	public Response setObservable(String request) {
		//parse json and retrieve parameter
		@SuppressWarnings("rawtypes")
		Map map = MarshallingService.getParser().parseJson(request);
		map = MarshallingService.unpackMap(map);
		String username;
		String password;
		boolean observable;
		try {
			username = map.get("username").toString().trim();
			password = map.get("password").toString().trim();
			observable = Boolean.parseBoolean(map.get("observable").toString());
		} catch (NullPointerException e) {
			return ResponseGenerator.generateBadRequest();
		}
		
		try{
			//check pw
			if(LoginResource.checkCredentials(username, password)){
				UserObject user = DatabaseService.queryUser(username);
				if (user != null) {
					DatabaseService.setObservableSetting(user.getId(),
							observable);
					return ResponseGenerator.generateOK();
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
