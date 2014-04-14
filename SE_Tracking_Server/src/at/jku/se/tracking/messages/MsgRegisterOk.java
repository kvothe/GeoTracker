package at.jku.se.tracking.messages;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgRegisterOk extends AMessage {
	private static final String FIELD_MESSAGE = "message";
	
	public MsgRegisterOk() {
		setType(MessageType.REGISTRATION);
		setValue(FIELD_MESSAGE, "Success");
	}
}
