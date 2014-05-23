package at.jku.se.tracking.test;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.jku.se.tracking.messages.serialization.MarshallingService;
import at.jku.se.tracking.utils.AcceptSSLCertificate;

public class TestRestService {

	public static void main(String[] args) throws KeyManagementException,
			NoSuchAlgorithmException {
		// sample client
		Client restClient = AcceptSSLCertificate.getClient();
		
		WebTarget target = restClient.target("https://localhost:448/rest/");
		
		Response r;
		
//		r = testGetObservableUser(target);
//		r = testSetObservableUser(target);
//		r = testGetUserList(target);
//		r = testGetSessionPoints(target);
//		r = testGetSessionList(target);
//		r = testRegistration(target);
//		r = testStartObservation(target);
		r = testStopObservation(target);
		
		
		String entity = r.readEntity(String.class);
		
		System.out.println("Entity: " + entity);
		System.out.println(r.getStatus());
		
		System.out.println("----");
	}
	
	private static Response testGetObservableUser(WebTarget target){
		//get request
		WebTarget resourceTarget = target.path("settings/getobservable/testuser");
		Response response = resourceTarget.request(MediaType.APPLICATION_JSON).get();
		return response;
	}
	
	private static Response testSetObservableUser(WebTarget target){
		//put request
		WebTarget resourceTarget = target.path("settings/setobservable");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", "manuel123");
		map.put("password", "manuel123");
		map.put("observable", true);
		Response response = resourceTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(MarshallingService.toJSON(map), MediaType.APPLICATION_JSON));
		return response;
	}

	private static Response testGetUserList(WebTarget target){
		//get
		WebTarget resourceTarget = target.path("user/getuserlist/testuser/true/");
		Response response = resourceTarget.request(MediaType.APPLICATION_JSON).get();
		return response;
	}
	
	private static Response testGetSessionList(WebTarget target){
		//get
		WebTarget resourceTarget = target.path("session/getsessionlist/testuser");
		Response response = resourceTarget.request(MediaType.APPLICATION_JSON).get();
		return response;
	}
	
	private static Response testGetSessionPoints(WebTarget target){
		//get
		WebTarget resourceTarget = target.path("session/getsessionpoints/100");
		Response response = resourceTarget.request(MediaType.APPLICATION_JSON).get();
		return response;
	}
	
	private static Response testRegistration(WebTarget target){
		//post
		WebTarget resourceTarget = target.path("user/register");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", "manuel1234");
		map.put("password", "manuel1234");
		map.put("observable", false);
		
		String json = MarshallingService.toJSON(map);
		
		Response response = resourceTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON));
		return response;
	}
	
	private static Response testStartObservation(WebTarget target){
		//post
		WebTarget resourceTarget = target.path("observation/start");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", "manuel12");
		map.put("password", "manuel12");
		map.put("observed", 7);
		
		String json = MarshallingService.toJSON(map);
		
		Response response = resourceTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON));
		return response;
	}
	
	private static Response testStopObservation(WebTarget target){
		//put
		WebTarget resourceTarget = target.path("observation/stop");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", "manuel123");
		map.put("password", "manuel123");
		map.put("isObserver", false);
		map.put("observationId", 40);
		
		String json = MarshallingService.toJSON(map);
		
		Response response = resourceTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(json, MediaType.APPLICATION_JSON));
		return response;
	}
}
