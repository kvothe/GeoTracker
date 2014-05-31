package at.jku.geotracker.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import at.jku.geotracker.application.Globals;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class GPSTrackerService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	public static final int UPDATE_INTERVAL = 1000;

	private LocationRequest mLocationRequest;
	private LocationClient mLocationClient;

	public GPSTrackerService() {
	}

	/*
	 * Called before service onStart method is called.All Initialization part
	 * goes here
	 */
	@Override
	public void onCreate() {
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationClient = new LocationClient(getApplicationContext(), this,
				this);

		mLocationClient.connect();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO: start websocket server to listen for location updates from
		// geotracker server
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*
	 * Overriden method of the interface
	 * GooglePlayServicesClient.OnConnectionFailedListener . called when
	 * connection to the Google Play Service are not able to connect
	 */

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
	}

	/*
	 * This is overriden method of interface
	 * GooglePlayServicesClient.ConnectionCallbacks which is called when
	 * locationClient is connecte to google service. You can receive GPS reading
	 * only when this method is called.So request for location updates from this
	 * method rather than onStart()
	 */
	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
	}

	/*
	 * Overriden method of interface LocationListener called when location of
	 * gps device is changed. Location Object is received as a parameter. This
	 * method is called when location of GPS device is changed
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (location.getAccuracy() <= 40) {

			if (Globals.getSessionId() != null) {
				JSONObject jo = new JSONObject();
				try {
					jo.put("message-type", "location-update");
					jo.put("session-id", Globals.getSessionId());
					jo.put("latitude", location.getLatitude());
					jo.put("longitude", location.getLongitude());
					jo.put("accuracy", location.getAccuracy());
					jo.put("altitude", location.getAltitude());
					jo.put("altitude-accuracy", "0");
					jo.put("heading", "0");
					jo.put("speed", location.getSpeed());
					jo.put("timestamp", location.getTime());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				((Globals) getApplication()).getWsClient().send(jo.toString());
			}

		}
	}

	/*
	 * Called when Sevice running in backgroung is stopped. Remove location
	 * upadate to stop receiving gps reading
	 */
	@Override
	public void onDestroy() {
		mLocationClient.removeLocationUpdates(this);
		super.onDestroy();
	}
}