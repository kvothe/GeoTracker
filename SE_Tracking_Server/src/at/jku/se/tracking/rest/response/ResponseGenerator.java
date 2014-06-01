package at.jku.se.tracking.rest.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import at.jku.se.tracking.messages.serialization.MarshallingService;

/**
 * provides response objects for REST methods
 * 
 * @author Manuel
 */
public class ResponseGenerator {
	static {
		responseEntity = new HashMap<String, Object>();
	}
	// error codes
	private static final int INVALID_USER = 404;
	private static final int SERVER_ERROR = 500;
	private static final int BAD_REQUEST = 400;
	private static final int USER_ALREADY_TAKEN = 406;
	private static final int UNAUTHORIZED = 401;

	private static Map<String, Object> responseEntity;

	public static Response generateSQLError() {
		setMessage("SQL error");
		return Response.status(SERVER_ERROR).entity(MarshallingService.toJSON(responseEntity)).build();
	}

	public static Response generateInvalidUserResponse() {
		setMessage("invalid user");
		return Response.status(INVALID_USER).entity(MarshallingService.toJSON(responseEntity)).build();
	}

	public static Response generateOK(Map<String, Object> m) {
		return Response.ok(MarshallingService.toJSON(m)).build();
	}

	public static Response generateOK(List<Map<String, Object>> l) {
		return Response.ok(MarshallingService.toJSON(l)).build();
	}

	public static Response generateOK(String sessionToken) {
		return Response.ok(sessionToken).build();
	}

	public static Response generateOK() {
		return Response.ok().build();
	}

	public static Response generateNoContent() {
		return Response.noContent().build();
	}

	public static Response generateBadRequest() {
		setMessage("bad request");
		return Response.status(BAD_REQUEST).entity(MarshallingService.toJSON(responseEntity)).build();
	}

	public static Response generateUsernameTaken() {
		setMessage("username already taken");
		return Response.status(USER_ALREADY_TAKEN).entity(MarshallingService.toJSON(responseEntity)).build();
	}

	public static Response generateServerError(String message) {
		setMessage(message);
		return Response.status(SERVER_ERROR).entity(MarshallingService.toJSON(responseEntity)).build();
	}

	public static Response generateNotAuthorized() {
		setMessage("not authorized");
		return Response.status(UNAUTHORIZED).build();
	}

	private static void setMessage(String message) {
		responseEntity.clear();
		responseEntity.put("message", message);
	}
}
