package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgLoginOk extends AMessage {
	private static final String FIELD_MESSAGE = "message";
	private static final String FIELD_SESSION_ID = "session-id";
	
	public MsgLoginOk(String sessionId) {
		setType(MessageType.LOGIN);
		setValue(FIELD_MESSAGE, "Success");
		setValue(FIELD_SESSION_ID, sessionId);
	}
}
