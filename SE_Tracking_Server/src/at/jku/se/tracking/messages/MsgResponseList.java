package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgResponseList extends AMessage {
	private static final String FIELD_USER_LIST = "user-list";

	// ------------------------------------------------------------------------

	public MsgResponseList() {
		// default constructor needed to instantiate after parsing
	}
	public MsgResponseList(double cid, Map<String, String> users) {
		setType(MessageType.LIST);
		setConversationId(cid);
		setValue(FIELD_USER_LIST, users);
	}
}
