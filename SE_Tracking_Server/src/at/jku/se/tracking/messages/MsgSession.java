package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgSession extends AMessage {
	public MsgSession() {
		// default constructor needed to instantiate after parsing
	}
	public MsgSession(String username, String password) {
		setType(MessageType.SESSION);
	}
}