package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgRequestTrackingSessions extends AMessage {
	private static final String FIELD_IS_OBSERVER = "is-observer";

	// ------------------------------------------------------------------------

	public MsgRequestTrackingSessions(Map<?, ?> map) {
		setMap(map);
	}
	public MsgRequestTrackingSessions(boolean isObserver) {
		setType(MessageType.SESSION_LIST);
		setValue(FIELD_IS_OBSERVER, isObserver);
	}

	// ------------------------------------------------------------------------

	public boolean isObserver() {
		if (getValue(FIELD_IS_OBSERVER) == null) {
			return false;
		}
		return Boolean.parseBoolean(getValue(FIELD_IS_OBSERVER).toString());
	}
}
