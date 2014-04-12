package at.jku.se.tracking.messages.serialization;

public class InvalidMessageException extends Exception {
	private static final long serialVersionUID = 6353612216569398637L;

	public InvalidMessageException(String message) {
		super(message);
	}

	public InvalidMessageException(String message, Throwable cause) {
		super(message, cause);
	}
}
