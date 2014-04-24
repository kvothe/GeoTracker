package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgNotification extends AMessage {
	private static final String FIELD_MESSAGE = "message";

	// ------------------------------------------------------------------------

	public MsgNotification(String message) {
		setType(MessageType.NOTIFICATION);
		setValue(FIELD_MESSAGE, message);
	}

	// ------------------------------------------------------------------------

	public String getMessage() {
		return (String) getValue(FIELD_MESSAGE);
	}
}
