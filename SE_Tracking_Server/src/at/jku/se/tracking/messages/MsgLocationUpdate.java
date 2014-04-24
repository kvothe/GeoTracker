package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.database.LocationConstants;
import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgLocationUpdate extends AMessage {

	private static final String FIELD_SESSION_ID = "session-id";
	private static final String FIELD_LATITUDE = "latitude";
	private static final String FIELD_LONGITUDE = "logitude";
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

	public MsgLocationUpdate(String sessionId, double latitude, double longitude, double accuracy, float altitude, float altitudeAccuracy,
			double heading, float speed, long timestamp) {
		setType(MessageType.LOCATION_UPDATE);
		setValue(FIELD_SESSION_ID, sessionId);
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
		return returnFloat(FIELD_LATITUDE, LocationConstants.ALTITUDE_NULL);
	}
	public double getAltitudeAccuraccy() {
		return returnDouble(FIELD_ALTITUDE_ACCURACCY, LocationConstants.ACCURACY_NULL);
	}

	public double getHeading() {
		float speed = getSpeed();
		if (speed == LocationConstants.SPEED_NULL || speed == 0) {
			return LocationConstants.HEADING_NAN;
		} else {
			return returnDouble(FIELD_HEADING, LocationConstants.HEADING_NULL);
		}
	}

	public float getSpeed() {
		return returnFloat(FIELD_SPEED, LocationConstants.SPEED_NULL);
	}

	public long getTimestamp() {
		return returnLong(FIELD_TIMESTAMP, 0);
	}
}
