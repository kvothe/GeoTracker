package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgRequestSessionTrack extends AMessage {
	private static final String FIELD_OBSERVATION_ID = "observation-id";

	// ------------------------------------------------------------------------

	public MsgRequestSessionTrack(Map<?, ?> map) {
		setMap(map);
	}
	public MsgRequestSessionTrack(String observationId, String observeUser, boolean senderIsObserver) {
		setType(MessageType.SESSION_POINTS);
		setValue(FIELD_OBSERVATION_ID, observationId);
	}

	// ------------------------------------------------------------------------

	public long getObservationId() {
		return returnLong(FIELD_OBSERVATION_ID, -1);
	}
}
