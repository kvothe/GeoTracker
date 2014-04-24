package at.jku.se.tracking.messages;

public enum MessageType {
	//@formatter:off
	REGISTRATION("request-registration"),
	LOGIN("request-login"),
	SESSION("request-session-check"),
	USER_LIST("request-user-list"),
	SESSION_LIST("request-session-list"),
	LIST("response-list"),
	START_OBSERVATION("request-start-observation"),
	STOP_OBSERVATION("request-stop-observation"),
	SET_OBSERVABLE("request-set-observable"),
	LOCATION_UPDATE("location-update"),	
	OK("response-ok"),
	ERROR("response-error"),
	LOGOUT("request-logout");
	//@formatter:on

	private String value;

	MessageType(String value) {
		this.value = value;
	}

	public String serialize() {
		return value;
	}

	public static MessageType parse(String s) {
		for (MessageType t : values()) {
			if (t.value.equals(s)) {
				return t;
			}
		}
		return null;
	}
}
