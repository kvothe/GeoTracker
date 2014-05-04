package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.database.LocationConstants;
import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgLocationUpdate extends AMessage {

	private static final String FIELD_SESSION_ID = "session-id";
	private static final String FIELD_USERNAME = "username";
	private static final String FIELD_LATITUDE = "latitude";
	private static final String FIELD_LONGITUDE = "longitude";
	private static final String FIELD_ACCURACY = "accuracy";
	private static final String FIELD_ALTITUDE = "altitude";
	private static final String FIELD_ALTITUDE_ACCURACCY = "altitude-accuracy";
	private static final String FIELD_HEADING = "heading";
	private static final String FIELD_SPEED = "speed";
	private static final String FIELD_TIMESTAMP = "timestamp";

	// ------------------------------------------------------------------------

	public MsgLocationUpdate(Map<?, ?> map) {
		setMap(map);
	}

	public MsgLocationUpdate(String username, double latitude, double longitude, double accuracy, long timestamp) {
		setType(MessageType.LOCATION_UPDATE);
		setValue(FIELD_USERNAME, username);
		setValue(FIELD_LATITUDE, latitude);
		setValue(FIELD_LONGITUDE, longitude);
		setValue(FIELD_ACCURACY, accuracy);		
		setValue(FIELD_TIMESTAMP, timestamp);
	}
	public MsgLocationUpdate(String username, double latitude, double longitude, double accuracy, float altitude, float altitudeAccuracy,
			double heading, float speed, long timestamp) {
		setType(MessageType.LOCATION_UPDATE);
		setValue(FIELD_USERNAME, username);
		setValue(FIELD_LATITUDE, latitude);
		setValue(FIELD_LONGITUDE, longitude);
		setValue(FIELD_ACCURACY, accuracy);
		setValue(FIELD_ALTITUDE, altitude);
		setValue(FIELD_ALTITUDE_ACCURACCY, altitudeAccuracy);
		setValue(FIELD_HEADING, heading);
		setValue(FIELD_SPEED, speed);
		setValue(FIELD_TIMESTAMP, timestamp);
	}

	// ------------------------------------------------------------------------

	public String getSessionId() {
		return (String) getValue(FIELD_SESSION_ID);
	}
	public String getUsername() {
		return (String) getValue(FIELD_USERNAME);
	}
	public double getLatitude() {
		return returnDouble(FIELD_LATITUDE, LocationConstants.LONG_LAT_NULL);
	}

	public double getLongitude() {
		return returnDouble(FIELD_LONGITUDE, LocationConstants.LONG_LAT_NULL);
	}

	public double getAccuracy() {
		return returnDouble(FIELD_ACCURACY, LocationConstants.ACCURACY_NULL);
	}
	public float getAltitude() {
		return returnFloat(FIELD_ALTITUDE, LocationConstants.ALTITUDE_NULL);
	}
	public double getAltitudeAccuraccy() {
		return returnDouble(FIELD_ALTITUDE_ACCURACCY, LocationConstants.ACCURACY_NULL);
	}

	public double getHeading() {
		return returnDouble(FIELD_HEADING, LocationConstants.HEADING_NULL);
	}

	public float getSpeed() {
		return returnFloat(FIELD_SPEED, LocationConstants.SPEED_NULL);
	}

	public long getTimestamp() {
		return returnLong(FIELD_TIMESTAMP, 0);
	}
}
