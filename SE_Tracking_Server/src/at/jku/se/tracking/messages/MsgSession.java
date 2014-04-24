package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgSession extends AMessage {
	public MsgSession(Map<?, ?> map) {
		setMap(map);
	}
	public MsgSession(String username, String password) {
		setType(MessageType.SESSION);
	}
}