package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgOk extends AMessage {
	private static final String FIELD_MESSAGE = "message";

	// ------------------------------------------------------------------------

	public MsgOk(double cid) {
		setType(MessageType.OK);
		setConversationId(cid);
	}
	public MsgOk(double cid, String message) {
		setType(MessageType.OK);
		setConversationId(cid);
		setValue(FIELD_MESSAGE, message);
	}

	// ------------------------------------------------------------------------

	public String getMessage() {
		return (String) getValue(FIELD_MESSAGE);
	}
}
