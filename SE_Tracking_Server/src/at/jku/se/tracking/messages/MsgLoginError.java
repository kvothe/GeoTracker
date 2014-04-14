package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgLoginError extends AMessage {

	private static final String FIELD_MESSAGE = "message";

	// ------------------------------------------------------------------------

	public MsgLoginError() {
		// default constructor needed to instantiate after parsing
	}
	public MsgLoginError(String message) {
		setType(MessageType.LOGIN);
		setValue(FIELD_MESSAGE, message);
	}
	public MsgLoginError(Exception e) {
		this(e.getClass().getSimpleName() + ": " + e.getMessage());
	}
	// ------------------------------------------------------------------------

	public String getMessage() {
		return (String) getValue(FIELD_MESSAGE);
	}
}
