package at.jku.geotracker.rest.model;

import android.app.Activity;

public class LoginModel {

	private String username;
	private String password;
	private Activity loginActivity;

	public LoginModel() {
		super();
	}

	public LoginModel(String username, String password, Activity loginActivity) {
		super();
		this.username = username;
		this.password = password;
		this.loginActivity = loginActivity;
	}

	public Activity getLoginActivity() {
		return loginActivity;
	}

	public void setLoginActivity(Activity loginActivity) {
		this.loginActivity = loginActivity;
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
