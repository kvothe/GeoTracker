package at.jku.se.tracking.rest;

import java.sql.SQLException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import at.jku.se.tracking.SessionObserver;
import at.jku.se.tracking.UserSession;
import at.jku.se.tracking.database.DatabaseService;
import at.jku.se.tracking.database.GeolocationObject;
import at.jku.se.tracking.messages.serialization.MarshallingService;
import at.jku.se.tracking.rest.response.ResponseGenerator;

@Path("/location")
public class LocationResource {

	@SuppressWarnings("unchecked")
	@POST
	@Path("/update")
	public Response update(@Context HttpServletRequest request, String data) {
		UserSession userSession = UserResource.checkSession(request);
		if (userSession != null) {
			long timestamp = System.currentTimeMillis(); // use server time for timestamps to avoid out of sync
			double latitude;
			double longitude;
			double accuracy;
			float altitude;
			float altitudeAccuracy;
			double heading;
			float speed;

			// parse json and retrieve parameter
			@SuppressWarnings("rawtypes")
			Map map = MarshallingService.getParser().parseJson(data);
			map = MarshallingService.unpackMap(map);
			try {
				latitude = Double.parseDouble((String) map.get("latitude"));
				longitude = Double.parseDouble((String) map.get("longitude"));
				accuracy = Double.parseDouble((String) map.get("accuracy"));
				altitude = Float.parseFloat((String) map.get("altitude"));
				altitudeAccuracy = Float.parseFloat((String) map.get("altitude-accuracy"));
				heading = Double.parseDouble((String) map.get("heading"));
				speed = Float.parseFloat((String) map.get("speed"));
				timestamp = Long.parseLong((String) map.get("timestamp")); // keep for client time sync
			} catch (NullPointerException e) {
				return ResponseGenerator.generateBadRequest();
			}
			// --
			try {
				GeolocationObject geoObject = new GeolocationObject(-1, userSession.getUserId(), timestamp, longitude,
						latitude, accuracy, altitude, altitudeAccuracy, heading, speed);
				// --
				if (DatabaseService.insertLocation(geoObject)) {
					SessionObserver.pushLocationUpdate(timestamp, userSession.getUserId(), userSession.getUsername(),
							geoObject.getLongitude(), geoObject.getLatitude(), geoObject.getAccuracy());
					// --
					return ResponseGenerator.generateOK();
				} else {
					return ResponseGenerator.generateServerError("We have a problem storing your location...");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return ResponseGenerator.generateSQLError();
			}
		}
		return ResponseGenerator.generateNotAuthorized();
	}
}
