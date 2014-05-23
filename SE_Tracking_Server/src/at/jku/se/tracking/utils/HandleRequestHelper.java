package at.jku.se.tracking.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.GeolocationObject;
import at.jku.se.tracking.database.TrackingSessionObject;
import at.jku.se.tracking.database.UserObject;

public class HandleRequestHelper {

	/**
	 * returns all users except for the user with given userId
	 * @param userId
	 * @param observableOnly
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> getUserList(long userId, boolean observableOnly) throws SQLException {
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
		// --
		List<UserObject> users = DatabaseService.queryUsers(observableOnly);

		// --
		for (UserObject u : users) {
			if (u.getId() != userId) {
				// crude implementation due to workaround for quick-json bug
				// with trailing commas
				Map<String, Object> user = new HashMap<String, Object>();
				user.put("name", u.getName());
				user.put("observable", u.isObservable());
				user.put("online", false);
				UserObject observed = DatabaseService.queryUser(u.getName());

				// check if this user already observes the requested user
				List<TrackingSessionObject> sessions = DatabaseService.queryTrackingSessions(userId, observed.getId(), true);
				if (sessions.size() > 0) {
					user.put("isObserved", true);
				} else {
					user.put("isObserved", false);
				}

				// --
				userList.add(user);
			}
		}
		return userList;
	}
	
	/**
	 * returns a list of sessions of a user
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> getSessionList(long userId) throws SQLException{
		List<Map<String, Object>> sessionList = new ArrayList<Map<String, Object>>();
		// --
		List<TrackingSessionObject> sessions = DatabaseService.queryTrackingSessions(userId, true, false, false);
		// --
		for (TrackingSessionObject s : sessions) {
			// crude implementation due to workaround for quick-json bug with trailing commas
			Map<String, Object> session = new HashMap<String, Object>();
			UserObject u = DatabaseService.queryUser(s.getObserved());
			session.put("observation-id", s.getId());
			if (u != null) {
				session.put("observed", u.getName());
			}
			session.put("starttime", s.getStarttime());
			session.put("endtime", s.getEndtime());
			// --
			sessionList.add(session);
		}
		return sessionList;
	}
	
	public static List<Map<String, Object>> getSessionPoints(long observationId) throws SQLException{
		List<Map<String, Object>> pointList = new ArrayList<Map<String, Object>>();
		// --
		List<GeolocationObject> points = DatabaseService.getTrackingSessionPoints(observationId);
		// --
		for (GeolocationObject p : GPSHelper.fixGPSPoints(points)) {
			// crude implementation due to workaround for quick-json bug with trailing commas
			Map<String, Object> point = new HashMap<String, Object>();
			point.put("timestamp", p.getTimestamp());
			point.put("longitude", p.getLongitude());
			point.put("latitude", p.getLatitude());
			point.put("accuracy", p.getAccuracy());
			// --
			
			pointList.add(point);
		}
		// --
		//System.out.println("pushing session points - " + pointList.size() + " reduced from " + points.size() + " points");
		return pointList;
	}
}
