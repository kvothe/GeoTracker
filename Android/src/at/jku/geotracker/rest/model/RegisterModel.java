package at.jku.geotracker.rest.model;

import at.jku.geotracker.rest.interfaces.ResponseListener;

public class RegisterModel extends AbstractModel {

	private String username;
	private String password;
	private boolean observAble;

	public RegisterModel(String username, String password, boolean observable, ResponseListener listener) {
		super(listener);
		this.username = username;
		this.password = password;
		this.observAble = observable;
	}

	public boolean isObservAble() {
		return observAble;
	}

	public void setObservAble(boolean observAble) {
		this.observAble = observAble;
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
