package at.jku.geotracker.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import at.jku.geotracker.R;
import at.jku.geotracker.rest.SessionPointListRequest;
import at.jku.geotracker.rest.interfaces.ResponseListener;
import at.jku.geotracker.rest.model.ResponseObject;
import at.jku.geotracker.rest.model.SessionModel;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends Activity {
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		// Initialize map
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		// Request session points
		SessionModel session = new SessionModel(String.valueOf(getIntent().getIntExtra("id", 0)),
				new ResponseHandlerSessionPoints());
		new SessionPointListRequest().execute(session);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly installed) and the
	 * map has not already been instantiated.. This will ensure that we only ever call {@link #setUpMap()} once when
	 * {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and {@link com.google.android.gms.maps.MapView MapView}) will
	 * show a prompt for the user to install/update the Google Play services APK on their device.
	 * <p>
	 * A user can return to this FragmentActivity after following the prompt and correctly installing/updating/enabling
	 * the Google Play services. Since the FragmentActivity may not have been completely destroyed during this process
	 * (it is likely that it would only be stopped or paused), {@link #onCreate(Bundle)} may not be called again so we
	 * should call this method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (map == null) {
			// Try to obtain the map from the SupportMapFragment.
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (map != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the camera. In this case, we just add a marker
	 * near Africa.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap} is not null.
	 */
	private void setUpMap() {
		map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	}

	private class ResponseHandlerSessionPoints implements ResponseListener {

		@Override
		public void receivedResponse(ResponseObject response) {
			if (response.getStatusCode() == 200) {
				map.clear();
				if (response.getResponse() != null) {
					String respOkay = response.getResponse().substring(1, response.getResponse().length() - 1);
					try {
						LatLngBounds.Builder mapBoundsBuilder = LatLngBounds.builder();
						List<LatLng> mapPoints = new ArrayList<LatLng>();
						JSONArray jsonArray = new JSONArray(respOkay);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject point = jsonArray.getJSONObject(i);
							try {
								// long timestamp = Long.parseLong((String) point.get("timestamp"));
								double longitude = (Double) point.get("longitude");
								double latitude = (Double) point.get("latitude");
								// double accuracy = Double.parseDouble((String) point.get("accuracy"));
								// --
								LatLng latLng = new LatLng(latitude, longitude);
								mapBoundsBuilder.include(latLng);
								mapPoints.add(latLng);
							} catch (NumberFormatException e) {
								// invalid point
							}
						}
						Log.d("GeoTracker", "received " + mapPoints.size() + " points");
						// Draw line and zoom to bounds
						map.addPolyline((new PolylineOptions()).add(mapPoints.toArray(new LatLng[0])).width(5)
								.color(Color.MAGENTA).geodesic(false));
						map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundsBuilder.build(), 5));
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			}
		}
	}
}