package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgRequestSettings extends AMessage {
	
	private static final String FIELD_USERNAME = "username";

	// ------------------------------------------------------------------------

	public MsgRequestSettings(Map<?, ?> map) {
		setMap(map);
	}
	public MsgRequestSettings(String username) {
		setType(MessageType.GET_SETTINGS);
		setValue(FIELD_USERNAME, username);
	}

	// ------------------------------------------------------------------------

	public String getUsername() {
		return (String) getValue(FIELD_USERNAME);
	}
}