package at.jku.geotracker.ws.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;
import at.jku.geotracker.pushservice.IPushService;

/**
 * This example demonstrates how to create a websocket connection to a server. Only the most important callbacks are
 * overloaded.
 */
public class WSClient extends WebSocketClient {
	private List<IPushService> listeners;

	public WSClient(URI serverUri) {
		super(serverUri);
	}

	public void addOnMessageListener(IPushService l) {
		if (listeners == null) {
			listeners = new ArrayList<IPushService>();
		}
		listeners.add(l);
	}

	public void removeOnMessageListener(IPushService l) {
		if (listeners != null && listeners.contains(l)) {
			listeners.remove(l);
		}
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("opened connection");
	}

	@Override
	public void onMessage(String message) {
		Log.d("GeoTracker", "wss.onmessage: " + message);
		// --
		try {
			JSONObject m = new JSONObject(message.substring(1, message.length() - 1));
			String type = m.getString("message-type");
			// --
			if (type.equals("notification")) {
				String msg = m.getString("message");
				if (listeners != null) {
					for (IPushService l : listeners) {
						l.receivedNotification(msg);
					}
				}
			} else if (type.equals("notification-user-added")) {
				String username = m.getString("username");
				boolean observable = m.getBoolean("observable");
				boolean online = m.getBoolean("online");
				// --
				if (listeners != null) {
					for (IPushService l : listeners) {
						l.receivedUserAdded(username, observable, online);
					}
				}
			} else if (type.equals("location-update")) {
				String username = m.getString("username");
				long timestamp = m.getLong("timestamp");
				double latitude = m.getDouble("latitude");
				double longitude = m.getDouble("longitude");
				double accuracy = m.getDouble("accuracy");
				float altitude = (float) m.getDouble("altitude");
				// float altitudeAccuracy = (float) m.getDouble("altitude-accuracy");
				double heading = m.getDouble("heading");
				float speed = (float) m.getDouble("speed");
				// --
				Location location = new Location("GeoTracker");
				location.setTime(timestamp);
				location.setLongitude(longitude);
				location.setLatitude(latitude);
				location.setAccuracy((float) accuracy);
				location.setAltitude(altitude);
				location.setBearing((float) heading);
				location.setSpeed(speed);
				// --
				if (listeners != null) {
					for (IPushService l : listeners) {
						l.receivedLocationUpate(username, location);
					}
				}
			}
		} catch (JSONException e) {
			Log.e("GeoTracker", e.getMessage(), e);
		}
	}

	@Override
	public void onFragment(Framedata fragment) {
		System.out.println("received fragment: " + new String(fragment.getPayloadData().array()));
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codecodes are documented in class
		// org.java_websocket.framing.CloseFrame
		System.out.println("Connection closed code: " + code);
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}
}