package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgLogout extends AMessage {

	public MsgLogout(Map<?, ?> map) {
		setMap(map);
		setType(MessageType.LOGOUT);
	}
}
