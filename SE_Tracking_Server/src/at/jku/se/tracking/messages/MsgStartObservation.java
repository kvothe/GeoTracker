package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgStartObservation extends AMessage {
	private static final String FIELD_USER = "user";

	// ------------------------------------------------------------------------

	public MsgStartObservation(Map<?, ?> map) {
		setMap(map);
	}
	public MsgStartObservation(String observeUser) {
		setType(MessageType.START_OBSERVATION);
		setValue(FIELD_USER, observeUser);
	}

	// ------------------------------------------------------------------------

	public String getObservedUser() {
		return (String) getValue(FIELD_USER).toString();
	}
}
