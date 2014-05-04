package at.jku.se.tracking.database;

import at.jku.se.tracking.messages.MsgLocationUpdate;

public class GeolocationObject {
	static final String TABLE_NAME = "geolocation";
	static final String COLUMN_ID = "id";
	static final String COLUMN_USER_FK = "user_fk";
	static final String COLUMN_TIMESTAMP = "timestamp";
	static final String COLUMN_LONGITUDE = "longitude";
	static final String COLUMN_LATITUDE = "latitude";
	static final String COLUMN_ACCURACY = "accuracy";
	static final String COLUMN_ALTITUDE = "altitude";
	static final String COLUMN_ALTITUDE_ACCURACCY = "altitude-accuracy";
	static final String COLUMN_HEADING = "heading";
	static final String COLUMN_SPEED = "speed";

	// ------------------------------------------------------------------------

	private long id;
	private long userFK;
	private long timestamp;
	private double longitude;
	private double latitude;
	private double accuracy;
	private float altitude;
	private double altitudeAccuracy;
	private double heading;
	private float speed;

	// ------------------------------------------------------------------------

	public GeolocationObject(long user, long timestamp, MsgLocationUpdate location) {
		this(-1, user, timestamp, location.getLongitude(), location.getLatitude(), location.getAccuracy(), location.getAltitude(), location
				.getAltitudeAccuraccy(), location.getHeading(), location.getSpeed());
	}

	public GeolocationObject(long id, long userFK, long timestamp, double longitude, double latitude, double accuracy, float altitude,
			double altitudeAccuracy, double heading, float speed) {
		super();
		this.id = id;
		this.userFK = userFK;
		this.timestamp = timestamp;
		this.longitude = longitude;
		this.latitude = latitude;
		this.accuracy = accuracy;
		this.altitude = altitude;
		this.altitudeAccuracy = altitudeAccuracy;
		this.heading = heading;
		this.speed = speed;
	}

	// ------------------------------------------------------------------------

	public long getId() {
		return id;
	}
	public long getUserFK() {
		return userFK;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public float getAltitude() {
		return altitude;
	}
	public double getAltitudeAccuracy() {
		return altitudeAccuracy;
	}
	public double getHeading() {
		return heading;
	}
	public float getSpeed() {
		return speed;
	}

	public boolean isSameLocation(GeolocationObject obj)	{
	
		return obj.getLongitude() == this.getLongitude() && obj.getLatitude() == this.getLatitude(); //maybe we should check further the altitude, but not now.
	}
	
	
}
