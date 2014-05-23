package at.jku.se.tracking.rest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.rest.response.ResponseGenerator;
import at.jku.se.tracking.utils.HandleRequestHelper;

@Path("/session")
public class SessionResource {

	/**
	 * processes get request for list of sessions
	 * @param username
	 * @return
	 */
	@GET
	@Path("/getsessionlist/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSessionList(@PathParam("username") String username){
		
		try {
			long userId = DatabaseService.queryUser(username).getId();
			
			List<Map<String, Object>> sessionList = HandleRequestHelper.getSessionList(userId);
			if(sessionList.size() > 0)
				return ResponseGenerator.generateOK(sessionList);
			else
				return ResponseGenerator.generateNoContent();
		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseGenerator.generateSQLError();
		}
	}
	
	/**
	 * get request for points of a session
	 * @param observationId
	 * @return
	 */
	@GET
	@Path("/getsessionpoints/{observation}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSessionPoints(@PathParam("observation") long observationId){
		try {
			List<Map<String, Object>> pointList = HandleRequestHelper.getSessionPoints(observationId);
			if(pointList.size() > 0)
				return ResponseGenerator.generateOK(pointList);
			else
				return ResponseGenerator.generateNoContent();
		} catch (SQLException e) {
			e.printStackTrace();
			return ResponseGenerator.generateSQLError();
		}
		
	}
}
