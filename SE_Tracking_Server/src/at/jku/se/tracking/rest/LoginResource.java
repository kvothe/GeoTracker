package at.jku.se.tracking.rest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Map;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.UserObject;
import at.jku.se.tracking.messages.serialization.MarshallingService;
import at.jku.se.tracking.rest.response.ResponseGenerator;
import at.jku.se.tracking.utils.PasswordEncryptionService;

@Path("/login")
public class LoginResource {

	/**
	 * 
	 * @param request map with username and password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String request){
		//parse json and retrieve parameter
		@SuppressWarnings("rawtypes")
		Map map = MarshallingService.getParser().parseJson(request);
		map = MarshallingService.unpackMap(map);
		String username;
		String password;
		try {
			username = map.get("username").toString().trim();
			password = map.get("password").toString().trim();
		} catch (NullPointerException e) {
			return ResponseGenerator.generateBadRequest();
		}
		//check pw
		if(checkCredentials(username, password)){
			return ResponseGenerator.generateOK();
		}else
			return ResponseGenerator.generateNotAuthorized();
	}
	
	/**
	 * checks username and password
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean checkCredentials(String username, String password){
		try{
			UserObject user = DatabaseService.queryUser(username);
			if (user != null)
					return PasswordEncryptionService.authenticate(password, user.getEncryptedPassword(), user.getSalt());
		}catch(SQLException e){
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
