package at.jku.geotracker.rest.model;

import at.jku.geotracker.rest.interfaces.ResponseListener;

public class SettingsModel extends AbstractModel {

	private boolean observable;
	private String username;
	private String password;

	public SettingsModel(boolean observable, String username, String password, ResponseListener listener) {
		super(listener);
		this.observable = observable;
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isObservable() {
		return observable;
	}

	public void setObservable(boolean observable) {
		this.observable = observable;
	}
}
