package at.jku.geotracker.pushservice;

import com.google.android.gms.maps.model.LatLng;

import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import at.jku.geotracker.activity.MapActivity;
import at.jku.geotracker.activity.userlist.UserItem;
import at.jku.geotracker.activity.userlist.UserListAdapter;

public class PushServiceHandler implements IPushService {
	private Context ctx;
	private int notificationId = 001;
	private UserListAdapter userList;
	private MapActivity map;

	public PushServiceHandler(Context context) {
		this.ctx = context;
	}

	public void setUserList(UserListAdapter userList) {
		this.userList = userList;
	}

	public void setMapActivity(MapActivity map) {
		this.map = map;
	}

	@Override
	public void receivedLocationUpate(String observedUser, Location l) {
		// TODO Auto-generated method stub
		Log.d("GeoTracker", "PushServiceHandler.receivedLocationUpdate: " + observedUser + ", " + l.getTime());
		// --
		if (map != null) {
			if (map.getCurrentlyObservedUser().equals(observedUser)) {
				new MapUpdate().execute(l);
			}
		}
	}

	@Override
	public void receivedNotification(String message) {
		Log.d("GeoTracker", "PushServiceHandler.receivedNotification: " + message);
		// --
		showNotification(message);
	}

	@Override
	public void receivedUserAdded(String name, boolean observable, boolean online) {
		Log.d("GeoTracker", "PushServiceHandler.receivedUserAdded: " + name);
		showNotification(name + " joined GeoTracker");
		if (userList != null) {
			new UserListUpdate().execute(new UserItem(false, name, observable, online));
		}
	}
	private void showNotification(String message) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
				.setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle("GeoTracker")
				.setContentText(message);
		mBuilder.build();
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(notificationId++, mBuilder.build());
	}

	private class UserListUpdate extends AsyncTask<UserItem, Void, UserListAdapter> {

		@Override
		protected UserListAdapter doInBackground(UserItem... params) {
			if (userList != null) {
				userList.getAllItems().add(params[0]);
			}
			return userList;
		}

		@Override
		protected void onPostExecute(UserListAdapter result) {
			super.onPostExecute(result);
			if (result != null) {
				result.notifyDataSetChanged();
			}
		}
	}

	private class MapUpdate extends AsyncTask<Location, Void, Void> {

		@Override
		protected Void doInBackground(Location... params) {
			LatLng point = new LatLng(params[0].getLatitude(), params[0].getLongitude());
			if (map != null) {
				map.addPoint(point);
			}
			return null;
		}
	}

}
