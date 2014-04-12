package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgError extends AMessage {

	private static final String FIELD_MESSAGE = "message";

	// ------------------------------------------------------------------------

	public MsgError() {
		// default constructor needed to instantiate after parsing
	}
	public MsgError(String message) {
		setType(MessageType.ERROR);
		setValue(FIELD_MESSAGE, message);
	}
	public MsgError(Exception e) {
		this(e.getClass().getSimpleName() + ": " + e.getMessage());
	}
	// ------------------------------------------------------------------------

	public String getMessage() {
		return (String) getValue(FIELD_MESSAGE);
	}
}
