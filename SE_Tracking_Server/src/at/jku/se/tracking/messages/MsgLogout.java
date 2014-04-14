package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgLogout extends AMessage {

	private static final String FIELD_SESSION_ID = "session-id";

	// ------------------------------------------------------------------------

	public MsgLogout() {
		// default constructor needed to instantiate after parsing
	}
	public MsgLogout(String session_id) {
		setType(MessageType.LOGOUT);
		setValue(FIELD_SESSION_ID, session_id);
	}

	// ------------------------------------------------------------------------


	public String getSessionId() {
		return (String) getValue(FIELD_SESSION_ID);
	}
}
