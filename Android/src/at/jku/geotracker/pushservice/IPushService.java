package at.jku.geotracker.pushservice;

import android.location.Location;

public interface IPushService {
	public void receivedLocationUpate(String observedUser, Location l);
	public void receivedNotification(String message);
	public void receivedUserAdded(String name, boolean observable, boolean online);
}
