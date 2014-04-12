package at.jku.se.tracking.database;

public class GeolocationObject {

	static final String TABLE_NAME="geolocation";
	static final String COLUMN_ID = "id";
	static final String COLUMN_USER_FK = "user_fk";
	static final String COLUMN_TIMESTAMP = "timestamp";
	static final String COLUMN_LONGITUDE = "longitude";
	static final String COLUMN_LATITUDE = "latitude";

	// ------------------------------------------------------------------------

	private double id;
	private double userFK;
	private double timestamp;
	private double longitude;
	private double latitude;

	// ------------------------------------------------------------------------

	public GeolocationObject(double userFK, double timestamp, double longitude, double latitude) {
		this(-1, userFK, timestamp, longitude, latitude);
	}
	public GeolocationObject(double id, double userFK, double timestamp, double longitude, double latitude) {
		super();
		this.id = id;
		this.userFK = userFK;
		this.timestamp = timestamp;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	// ------------------------------------------------------------------------

	public double getId() {
		return id;
	}
	public double getUserFK() {
		return userFK;
	}
	public double getTimestamp() {
		return timestamp;
	}
	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}

}
