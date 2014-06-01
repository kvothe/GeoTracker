package at.jku.se.tracking.messages;

import java.util.Map;

import at.jku.se.tracking.messages.serialization.AMessage;

public class MsgNotificationUserAdded extends AMessage {
	private static final String FIELD_USERNAME = "username";
	private static final String FIELD_OBSERVABLE = "observable";
	private static final String FIELD_ONLINE = "online";

	// ------------------------------------------------------------------------

	public MsgNotificationUserAdded(Map<?, ?> map) {
		setMap(map);
	}
	public MsgNotificationUserAdded(String username, boolean observable, boolean online) {
		setType(MessageType.NOTIFICATION_USER_ADDED);
		setValue(FIELD_USERNAME, username);
		setValue(FIELD_OBSERVABLE, observable);
		setValue(FIELD_ONLINE, online);
	}

	// ------------------------------------------------------------------------

	public String getUsername() {
		return getValue(FIELD_USERNAME).toString();
	}
	public boolean getObservable() {
		if (getValue(FIELD_OBSERVABLE) == null) {
			return false;
		}
		return Boolean.parseBoolean(getValue(FIELD_OBSERVABLE).toString());
	}
	public boolean getOnline() {
		if (getValue(FIELD_ONLINE) == null) {
			return false;
		}
		return Boolean.parseBoolean(getValue(FIELD_ONLINE).toString());
	}

}
