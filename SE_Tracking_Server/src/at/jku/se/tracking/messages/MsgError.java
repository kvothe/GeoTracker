package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgError extends AMessage {

	private static final String FIELD_MESSAGE = "message";

	// ------------------------------------------------------------------------

	public MsgError() {
		// default constructor needed to instantiate after parsing
	}
	public MsgError(double cid, String message) {
		setType(MessageType.ERROR);
		setConversationId(cid);
		setValue(FIELD_MESSAGE, message);
	}
	public MsgError(double cid, Exception e) {
		this(cid, e.getClass().getSimpleName() + ": " + e.getMessage());
	}

	// ------------------------------------------------------------------------

	public String getMessage() {
		return (String) getValue(FIELD_MESSAGE);
	}
}
