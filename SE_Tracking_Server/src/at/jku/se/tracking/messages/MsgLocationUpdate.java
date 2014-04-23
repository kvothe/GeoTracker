package at.jku.se.tracking.messages;

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

	public MsgLocationUpdate() {
		// default constructor needed to instantiate after parsing
	}

	public MsgLocationUpdate(String sessionId,
			double latitude, double longitude, String accuracy,
			String altitude, String altitudeAccuracy, String heading,
			String speed, double timestamp) {
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

	public String getFieldSessionId() {
		return (String) getValue(FIELD_SESSION_ID);
	}

	public double getFieldLatitude() {
		return Double.parseDouble((String) getValue(FIELD_LATITUDE));
	}

	public double getFieldLongitude() {
		return Double.parseDouble((String) getValue(FIELD_LONGITUDE));
	}

	public String getFieldAccuracy() {
		return (String) getValue(FIELD_ACCURACY);
	}

	public String getFieldAltitude() {
		return (String) getValue(FIELD_ALTITUDE);
	}

	public String getFieldAltitudeAccuraccy() {
		return (String) getValue(FIELD_ALTITUDE_ACCURACCY);
	}

	public String getFieldHeading() {
		return (String) getValue(FIELD_HEADING);
	}

	public String getFieldSpeed() {
		return (String) getValue(FIELD_SPEED);
	}

	public double getFieldTimestamp() {
		return Double.parseDouble((String) getValue(FIELD_TIMESTAMP));
	}

}
