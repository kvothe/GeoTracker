package at.jku.se.tracking.messages;

import java.util.List;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgResponseList extends AMessage {
	private static final String FIELD_USER_LIST = "list";

	// ------------------------------------------------------------------------

	public MsgResponseList() {
		// default constructor needed to instantiate after parsing
	}
	public MsgResponseList(double cid, List<?> list) {
		setType(MessageType.LIST);
		setConversationId(cid);
		if (list.size() > 0) { // workaround for bug in quick-json (trailing comma after empty entry)
			setValue(FIELD_USER_LIST, list);
		}
	}
}
