package at.jku.se.tracking.messages.serialization;

import java.util.HashMap;
import java.util.Map;

import at.jku.se.tracking.messages.MessageType;

public abstract class AMessage {
	protected static final String FIELD_CID = "cid";
	protected static final String FIELD_MESSAGE_TYPE = "message-type";
	protected static final String FIELD_SESSION_ID = "session-id";

	// ------------------------------------------------------------------------

	private Map<String, Object> values;

	protected Map<String, Object> getMap() {
		return values;
	}

	@SuppressWarnings("unchecked")
	protected void setMap(Map<?, ?> map) {
		this.values = (Map<String, Object>) map;
	}

	// ------------------------------------------------------------------------

	protected void setConversationId(double id) {
		setValue(FIELD_CID, id);
	}

	public double getConversationId() {
		if (hasValue(FIELD_CID)) {
			return Double.parseDouble(getValue(FIELD_CID).toString());
		}
		return -1;
	}

	// ------------------------------------------------------------------------

	protected void setSessionId(String id) {
		setValue(FIELD_SESSION_ID, id);
	}

	public String getSessionId() {
		if (hasValue(FIELD_SESSION_ID)) {
			return (String) getValue(FIELD_SESSION_ID);
		}
		return null;
	}

	// ------------------------------------------------------------------------

	protected void setType(MessageType type) {
		setValue(FIELD_MESSAGE_TYPE, type.serialize());
	}

	public MessageType getType() {
		String type = (String) getValue(FIELD_MESSAGE_TYPE);
		return MessageType.parse(type);
	}

	// ------------------------------------------------------------------------

	protected void setValue(String name, Object value) {
		if (values == null) {
			values = new HashMap<String, Object>();
		}
		values.put(name, value);
	}

	protected Object getValue(String name) {
		if (hasValue(name)) {
			return values.get(name);
		}
		return null;
	}

	// ------------------------------------------------------------------------

	protected boolean hasValue(String name) {
		if (values != null) {
			return values.containsKey(name);
		}
		return false;
	}
}
