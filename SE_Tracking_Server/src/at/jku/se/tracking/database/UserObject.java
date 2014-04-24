package at.jku.se.tracking.database;

public class UserObject {

	static final String TABLE_NAME = "user";
	static final String COLUMN_ID = "id";
	static final String COLUMN_USERNAME = "username";
	static final String COLUMN_PASSWORD = "encryptedPassword";
	static final String COLUMN_SALT = "salt";
	static final String COLUMN_OBSERVABLE = "observable";

	// ------------------------------------------------------------------------

	private long id;
	private String name;
	private byte[] encryptedPassword;
	private byte[] salt;
	private boolean observable;

	// ------------------------------------------------------------------------

	public UserObject(String name, byte[] encryptedPassword, byte[] salt, boolean observable) {
		this(-1, name, encryptedPassword, salt, observable);
	}
	public UserObject(long id, String name, byte[] encryptedPassword, byte[] salt, boolean observable) {
		super();
		this.id = id;
		this.name = name;
		this.encryptedPassword = encryptedPassword;
		this.salt = salt;
		this.observable = observable;
	}

	// ------------------------------------------------------------------------

	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public byte[] getEncryptedPassword() {
		return encryptedPassword;
	}
	public byte[] getSalt() {
		return salt;
	}
	public boolean isObservable() {
		return observable;
	}
}
