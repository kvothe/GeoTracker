package at.jku.geotracker.rest.model;

import android.app.Activity;

public class RegisterModel {

	private String username;
	private String password;
	private boolean observAble;
	private Activity registerActivity;

	public RegisterModel() {
		super();
	}

	public RegisterModel(String username, String password,
			Activity registerActivity, boolean observable) {
		super();
		this.username = username;
		this.password = password;
		this.registerActivity = registerActivity;
		this.observAble = observable;
	}

	public boolean isObservAble() {
		return observAble;
	}

	public void setObservAble(boolean observAble) {
		this.observAble = observAble;
	}

	public Activity getRegisterActivity() {
		return registerActivity;
	}

	public void setRegisterActivity(Activity registerActivity) {
		this.registerActivity = registerActivity;
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

}
