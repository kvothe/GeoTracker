package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgOk extends AMessage {
	public MsgOk() {
		setType(MessageType.OK);
	}
}
