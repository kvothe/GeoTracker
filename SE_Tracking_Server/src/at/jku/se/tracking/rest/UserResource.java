package at.jku.se.tracking.rest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.UserObject;
import at.jku.se.tracking.messages.serialization.MarshallingService;
import at.jku.se.tracking.rest.response.ResponseGenerator;
import at.jku.se.tracking.utils.HandleRequestHelper;
import at.jku.se.tracking.utils.PasswordEncryptionService;

@Path("/user")
public class UserResource {
	
	/**
	 * get request for a list of users
	 * @param username
	 * @param observableOnly
	 * @return
	 */
	@GET
	@Path("/getuserlist/{username}/{observable-only}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserlist(@PathParam("username") String username, @PathParam("observable-only") boolean observableOnly) {
		try{
			long userId = DatabaseService.queryUser(username).getId();
			
			List<Map<String, Object>> userList = HandleRequestHelper.getUserList(userId, observableOnly);
			if(userList.size() > 0)
				return ResponseGenerator.generateOK(userList);
			else
				return ResponseGenerator.generateNoContent();
		}catch (SQLException e) {
			e.printStackTrace();
			return ResponseGenerator.generateSQLError();
		}
	}
	
	/**
	 * post request for registering
	 * @param request map with username, password and observable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(String request){
		@SuppressWarnings("rawtypes")
		Map map = MarshallingService.getParser().parseJson(request);
		map = MarshallingService.unpackMap(map);
	
		try{
			//get parameter
			String username = map.get("username").toString().trim();
			String password = map.get("password").toString().trim();
			boolean observable = Boolean.parseBoolean(map.get("observable").toString());
			if((username == null) || (password == null)){
				return ResponseGenerator.generateBadRequest();
			}else{
				UserObject user = DatabaseService.queryUser(username);
				if(user != null)
					return ResponseGenerator.generateUsernameTaken();
				else{
					//encryption
					byte[] salt = PasswordEncryptionService.generateSalt();
					byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword(password, salt);
					// Instantiate User Object
					user = new UserObject(username, encryptedPassword, salt, observable);
					// Store user
					long userId = DatabaseService.insertUser(user);
					if (userId != -1) {
						return ResponseGenerator.generateOK();
					} else {
						return ResponseGenerator.generateServerError("something went wrong...");
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			return ResponseGenerator.generateSQLError();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseGenerator.generateServerError("NoSuchAlgorithmException");
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseGenerator.generateServerError("InvalidKeySpecException");
		}
	}
}
