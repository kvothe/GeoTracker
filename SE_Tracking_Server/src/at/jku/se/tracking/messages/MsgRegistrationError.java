package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgRegistrationError extends AMessage {

	private static final String FIELD_MESSAGE = "message";

	// ------------------------------------------------------------------------

	public MsgRegistrationError() {
		// default constructor needed to instantiate after parsing
	}
	public MsgRegistrationError(String message) {
		setType(MessageType.REGISTRATION);
		setValue(FIELD_MESSAGE, message);
	}
	public MsgRegistrationError(Exception e) {
		this(e.getClass().getSimpleName() + ": " + e.getMessage());
	}
	// ------------------------------------------------------------------------

	public String getMessage() {
		return (String) getValue(FIELD_MESSAGE);
	}
}
