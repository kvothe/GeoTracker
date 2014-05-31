package at.jku.geotracker.rest.model;

import at.jku.geotracker.rest.interfaces.ResponseListener;

public class LoginModel extends AbstractModel {
	private String username;
	private String password;

	public LoginModel(String username, String password, ResponseListener listener) {
		super(listener);
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

}
