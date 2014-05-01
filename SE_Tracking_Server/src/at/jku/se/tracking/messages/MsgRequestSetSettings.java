package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgRequestSetSettings extends AMessage {

	private static final String FIELD_USERNAME = "username";
	private static final String FIELD_OBSERVABLE = "observable";

	// ------------------------------------------------------------------------

	public MsgRequestSetSettings(Map<?, ?> map) {
		setMap(map);
	}

	public MsgRequestSetSettings(String username, String observable) {
		setType(MessageType.GET_SETTINGS);
		setValue(FIELD_USERNAME, username);
		setValue(FIELD_OBSERVABLE, observable);
	}

	// ------------------------------------------------------------------------

	public String getUsername() {
		return (String) getValue(FIELD_USERNAME);
	}

	public boolean getObservable() {
		if(getValue(FIELD_OBSERVABLE).equals("false")) {
			return false;
		}
		return true;
	}
}