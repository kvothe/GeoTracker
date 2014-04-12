package at.jku.se.tracking.messages.serialization;

import java.util.HashMap;
import java.util.Map;

import at.jku.se.tracking.messages.MessageType;

public abstract class AMessage {
	protected static final String FIELD_MESSAGE_TYPE = "message-type";

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
		if (values != null) {
			return values.get(name);
		}
		return null;
	}
}
