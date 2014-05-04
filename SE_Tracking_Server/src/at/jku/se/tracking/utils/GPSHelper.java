package at.jku.se.tracking.utils;

public class GPSHelper {

//	public static double distance(double lat1, double lng1, double lat2,
//			double lng2) {
//		double a = (lat1 - lat2) * GPSHelper.distPerLat(lat1);
//		double b = (lng1 - lng2) * GPSHelper.distPerLng(lat1);
//		return Math.sqrt(a * a + b * b);
//	}
//
//	private static double distPerLng(double lat) {
//		return 0.0003121092 * Math.pow(lat, 4) + 0.0101182384
//				* Math.pow(lat, 3) - 17.2385140059 * lat * lat + 5.5485277537
//				* lat + 111301.967182595;
//	}
//
//	private static double distPerLat(double lat) {
//		return -0.000000487305676 * Math.pow(lat, 4) - 0.0033668574
//				* Math.pow(lat, 3) + 0.4601181791 * lat * lat - 1.4558127346
//				* lat + 110579.25662316;
//	}

	//adopted code from http://stackoverflow.com/questions/3715521/how-can-i-calculate-the-distance-between-two-gps-points-in-java
	public static double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
	    float pk = (float) (180/3.14159);

	    float a1 = lat_a / pk;
	    float a2 = lng_a / pk;
	    float b1 = lat_b / pk;
	    float b2 = lng_b / pk;

	    float t1 = (float)Math.cos(a1)*(float)Math.cos(a2)*(float)Math.cos(b1)*(float)Math.cos(b2);
	    float t2 = (float)Math.cos(a1)*(float)Math.sin(a2)*(float)Math.cos(b1)*(float)Math.sin(b2);
	    float t3 = (float)Math.sin(a1)*(float)Math.sin(b1);
	    double tt = Math.acos(t1 + t2 + t3);

	    return 6366000*tt / 1000;
	}

}
