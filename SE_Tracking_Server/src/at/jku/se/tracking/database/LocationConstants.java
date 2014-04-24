package at.jku.se.tracking.database;

public class LocationConstants {
	public static final double LONG_LAT_NULL = Double.NaN;
	public static final double ACCURACY_NULL = Float.NaN;
	public static final float ALTITUDE_NULL = Float.NaN;
	public static final float SPEED_NULL = Float.NaN;
	public static final double HEADING_NULL = -1; // Heading not supported
	public static final double HEADING_NAN = Double.NaN; // Heading not applicable (i.e. speed = 0)
}
