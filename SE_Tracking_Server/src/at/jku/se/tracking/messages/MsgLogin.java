package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgLogin extends AMessage {

	private static final String FIELD_USERNAME = "username";
	private static final String FIELD_PASSWORD = "password";

	// ------------------------------------------------------------------------

	public MsgLogin(Map<?, ?> map) {
		setMap(map);
	}
	public MsgLogin(String username, String password) {
		setType(MessageType.LOGIN);
		setValue(FIELD_USERNAME, username);
		setValue(FIELD_PASSWORD, password);
	}

	// ------------------------------------------------------------------------

	public String getUsername() {
		return (String) getValue(FIELD_USERNAME);
	}

	public String getPassword() {
		return (String) getValue(FIELD_PASSWORD);
	}
}
