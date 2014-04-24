package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgRequestSessionList extends AMessage {
	private static final String FIELD_LIST_OBSERVED = "list-observed";
	private static final String FIELD_LIST_OBSERVERS = "list-observers";

	// ------------------------------------------------------------------------

	public MsgRequestSessionList(Map<?, ?> map) {
		setMap(map);
	}
	public MsgRequestSessionList(boolean listObserved, boolean listObservers) {
		setType(MessageType.SESSION_LIST);
		setValue(FIELD_LIST_OBSERVED, listObserved);
		setValue(FIELD_LIST_OBSERVERS, listObservers);
	}

	// ------------------------------------------------------------------------

	public boolean listObserved() {
		if (getValue(FIELD_LIST_OBSERVED) == null) {
			return false;
		}
		return Boolean.parseBoolean(getValue(FIELD_LIST_OBSERVED).toString());
	}

	public boolean listObserves() {
		if (getValue(FIELD_LIST_OBSERVERS) == null) {
			return false;
		}
		return Boolean.parseBoolean(getValue(FIELD_LIST_OBSERVERS).toString());
	}
}
