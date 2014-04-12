package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgRegister extends AMessage {

	private static final String FIELD_USERNAME = "username";
	private static final String FIELD_PASSWORD = "password";
	private static final String FIELD_OBSERVABLE = "observable";

	// ------------------------------------------------------------------------

	public MsgRegister() {
		// default constructor needed to instantiate after parsing
	}
	public MsgRegister(String username, String password, boolean observable) {
		setType(MessageType.REGISTRATION);
		setValue(FIELD_USERNAME, username);
		setValue(FIELD_PASSWORD, password);
		setValue(FIELD_OBSERVABLE, observable);
	}

	// ------------------------------------------------------------------------

	public String getUsername() {
		return (String) getValue(FIELD_USERNAME);
	}

	public String getPassword() {
		return (String) getValue(FIELD_PASSWORD);
	}

	public boolean isObservable() {
		if (getValue(FIELD_OBSERVABLE) == null) {
			return false;
		}
		return Boolean.parseBoolean(getValue(FIELD_OBSERVABLE).toString());
	}
}
