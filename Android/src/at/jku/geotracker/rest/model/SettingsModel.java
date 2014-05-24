package at.jku.geotracker.rest.model;

import android.app.Fragment;

public class SettingsModel {

	private boolean observable;
	private Fragment settingsFragment;
	private String username;
	private String password;

	public SettingsModel() {
		super();
	}

	public SettingsModel(boolean observable, Fragment settingsFragment,
			String username, String password) {
		super();
		this.observable = observable;
		this.settingsFragment = settingsFragment;
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

	public Fragment getSettingsFragment() {
		return settingsFragment;
	}

	public void setSettingsFragment(Fragment settingsFragment) {
		this.settingsFragment = settingsFragment;
	}
}
