package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgStopObservation extends AMessage {
	private static final String FIELD_OBSERVATION_ID = "observation-id";
	private static final String FIELD_USER = "user";
	private static final String FIELD_USER_IS_OBSERVER = "user-is-observer";

	// ------------------------------------------------------------------------

	public MsgStopObservation(Map<?, ?> map) {
		setMap(map);
	}
	public MsgStopObservation(String observationId, String observeUser, boolean senderIsObserver) {
		setType(MessageType.STOP_OBSERVATION);
		setValue(FIELD_OBSERVATION_ID, observationId);
		setValue(FIELD_USER, observeUser);
		setValue(FIELD_USER_IS_OBSERVER, senderIsObserver);

	}

	// ------------------------------------------------------------------------

	public long getObservationId() {
		return returnLong(FIELD_OBSERVATION_ID, -1);
	}
	public String getUser() {
		return (String) getValue(FIELD_USER).toString();
	}
	public boolean userIsObserver() {
		if (getValue(FIELD_USER_IS_OBSERVER) == null) {
			return false;
		}
		return Boolean.parseBoolean(getValue(FIELD_USER_IS_OBSERVER).toString());
	}
}
