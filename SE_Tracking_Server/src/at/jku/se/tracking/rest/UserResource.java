package at.jku.se.tracking.rest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.jku.se.tracking.SessionObserver;
import at.jku.se.tracking.UserSession;
import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.UserObject;
import at.jku.se.tracking.messages.serialization.MarshallingService;
import at.jku.se.tracking.rest.response.ResponseGenerator;
import at.jku.se.tracking.utils.HandleRequestHelper;
import at.jku.se.tracking.utils.PasswordEncryptionService;

@Path("/user")
public class UserResource {
	public static final String FIELD_SESSION_TOKEN = "session-token";

	/**
	 * get request for a list of users
	 * 
	 * @param username
	 * @param observableOnly
	 * @return
	 */
	@GET
	@Path("/list/{observable-only}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserlist(@Context HttpServletRequest request,
			@PathParam("observable-only") boolean observableOnly) {
		UserSession session = checkSession(request);
		if (session != null) {
			try {
				List<Map<String, Object>> userList = HandleRequestHelper.getUserList(session.getUserId(),
						observableOnly);
				if (userList.size() > 0)
					return ResponseGenerator.generateOK(userList);
				else
					return ResponseGenerator.generateNoContent();
			} catch (SQLException e) {
				e.printStackTrace();
				return ResponseGenerator.generateSQLError();
			}
		}
		return ResponseGenerator.generateNotAuthorized();
	}

	/**
	 * post request for registering
	 * 
	 * @param request
	 *            map with username, password and observable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(String request) {
		@SuppressWarnings("rawtypes")
		Map map = MarshallingService.getParser().parseJson(request);
		map = MarshallingService.unpackMap(map);

		try {
			// get parameter
			String username = map.get("username").toString().trim();
			String password = map.get("password").toString().trim();
			boolean observable = Boolean.parseBoolean(map.get("observable").toString());
			if ((username == null) || (password == null)) {
				return ResponseGenerator.generateBadRequest();
			} else {
				UserObject user = DatabaseService.queryUser(username);
				if (user != null)
					return ResponseGenerator.generateUsernameTaken();
				else {
					// encryption
					byte[] salt = PasswordEncryptionService.generateSalt();
					byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword(password, salt);
					// Instantiate User Object
					user = new UserObject(username, encryptedPassword, salt, observable);
					// Store user
					long userId = DatabaseService.insertUser(user);
					if (userId != -1) {
						user = new UserObject(userId, username, observable);
						String sessionId = loginUser(user);
						return ResponseGenerator.generateOK(sessionId);
					} else {
						return ResponseGenerator.generateServerError("something went wrong...");
					}
				}
			}
		} catch (SQLException e) {
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

	/**
	 * @param request
	 *            map with username and password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PUT
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String request) {
		// parse json and retrieve parameter
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
		// check pw
		if (checkCredentials(username, password)) {
			try {
				String sessionId = loginUser(DatabaseService.queryUser(username));
				// --
				return ResponseGenerator.generateOK(sessionId);
			} catch (SQLException e) {
				e.printStackTrace();
				return ResponseGenerator.generateNotAuthorized();
			}

		} else {
			return ResponseGenerator.generateNotAuthorized();
		}
	}

	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@Context HttpServletRequest request) {
		UserSession session = checkSession(request);
		if (session != null) {
			SessionObserver.unregisterSession(session);
			System.out.println("User <" + session.getUsername() + "> signed off");
			return ResponseGenerator.generateOK();
		}
		// --
		return ResponseGenerator.generateBadRequest();
	}

	private static String loginUser(UserObject user) {
		String sessionId = UUID.randomUUID().toString();
		// --
		UserSession session = new UserSession(sessionId, user.getName(), user.getId(), System.currentTimeMillis());
		// --
		System.out.println("User <" + user.getName() + "> registered session <" + sessionId + ">");
		SessionObserver.registerSession(session, null);
		// --
		return sessionId;
	}
	
	static UserSession checkSession(HttpServletRequest request) {
		String sessionToken = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies) {
			if (c.getName().equals(FIELD_SESSION_TOKEN)) {
				sessionToken = c.getValue();
				if (SessionObserver.hasSession(sessionToken)) {
					return SessionObserver.getSession(sessionToken);
				}
			}
		}
		// --
		return null;
	}

	/**
	 * checks username and password
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	private static boolean checkCredentials(String username, String password) {
		try {
			UserObject user = DatabaseService.queryUser(username);
			if (user != null)
				return PasswordEncryptionService.authenticate(password, user.getEncryptedPassword(), user.getSalt());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return false;
	}
}
