package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgRequestUserList extends AMessage {
	private static final String FIELD_OBSERVABLE_ONLY = "observable-only";

	// ------------------------------------------------------------------------

	public MsgRequestUserList(Map<?, ?> map) {
		setMap(map);
	}
	public MsgRequestUserList(String username, String password, boolean observable) {
		setType(MessageType.USER_LIST);
		setValue(FIELD_OBSERVABLE_ONLY, observable);
	}

	// ------------------------------------------------------------------------

	public boolean getOnlyObservable() {
		if (getValue(FIELD_OBSERVABLE_ONLY) == null) {
			return false;
		}
		return Boolean.parseBoolean(getValue(FIELD_OBSERVABLE_ONLY).toString());
	}
}
