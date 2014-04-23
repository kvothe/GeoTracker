package at.jku.se.tracking.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DatabaseService {
	private static final int MAX_RESOURCES = 16; // Number of concurrent connections
	private static final int POOL_WAIT_TIME = 60000; // Wait at most one minute for a database connection
	private static String CONNECTION_STRING;
	private static ConnectionPool CONNECTION_POOL;

	// ------------------------------------------------------------------------

	static {
		// Initialize DB connection with settings from config file
		Properties prop = ConfigLoader.loadConfig();
		if (prop != null) {
			String host = prop.getProperty("dbhost");
			String dbname = prop.getProperty("dbname");
			String user = prop.getProperty("dbuser");
			String pass = prop.getProperty("dbpassword");
			// --
			if (!host.equals("test")) { // setting for testing purpose when no database is present
				CONNECTION_STRING = "jdbc:sqlserver://" + host + ";";
				CONNECTION_STRING += "database=" + dbname + ";user=" + user + ";password=" + pass;
				// --
				CONNECTION_POOL = new ConnectionPool(MAX_RESOURCES, CONNECTION_STRING);
			}
		} else {
			// TODO
		}
	}

	// ------------------------------------------------------------------------

	public static void releaseResources() throws SQLException {
		if (CONNECTION_POOL != null) {
			CONNECTION_POOL.releasePool();
		}
	}

	// ------------------------------------------------------------------------

	public static UserObject queryUser(double id) throws SQLException {
		if (CONNECTION_POOL == null) {
			return null;
		}
		// --
		UserObject user = null;
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement query = con.prepareStatement("SELECT * FROM [" + UserObject.TABLE_NAME + "] WHERE "
				+ UserObject.COLUMN_ID + "=?");
		query.setDouble(1, id);
		ResultSet rs = query.executeQuery();
		// --
		try {
			while (rs.next()) {
				String username = rs.getString(UserObject.COLUMN_USERNAME);
				byte[] password = rs.getBytes(UserObject.COLUMN_PASSWORD);
				byte[] salt = rs.getBytes(UserObject.COLUMN_SALT);
				boolean observable = rs.getBoolean(UserObject.COLUMN_OBSERVABLE);
				// --
				user = new UserObject(id, username, password, salt, observable);
				break; // TODO: the id should be unique, nonetheless check for it
			}
		} finally {
			rs.close();
			query.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return user;
	}

	// ------------------------------------------------------------------------

	public static UserObject queryUser(String username) throws SQLException {
		if (CONNECTION_POOL == null) {
			return null;
		}
		// --
		UserObject user = null;
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement query = con.prepareStatement("SELECT * FROM [" + UserObject.TABLE_NAME + "] WHERE "
				+ UserObject.COLUMN_USERNAME + "=?");
		query.setString(1, username);
		ResultSet rs = query.executeQuery();
		// --
		try {
			while (rs.next()) {
				// TODO normalize username (case-insenstivie?)
				double id = rs.getDouble(UserObject.COLUMN_ID);
				byte[] password = rs.getBytes(UserObject.COLUMN_PASSWORD);
				byte[] salt = rs.getBytes(UserObject.COLUMN_SALT);
				boolean observable = rs.getBoolean(UserObject.COLUMN_OBSERVABLE);
				// --
				user = new UserObject(id, username, password, salt, observable);
				break; // TODO: the id should be unique, nonetheless check for it
			}
		} finally {
			rs.close();
			query.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return user;
	}

	// ------------------------------------------------------------------------

	public static List<UserObject> queryObservableUsers() throws SQLException {
		return null;
	}

	// ------------------------------------------------------------------------

	public static boolean insertUser(UserObject user) throws SQLException {
		if (CONNECTION_POOL == null) {
			return false;
		}
		// --
		boolean result = false;
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement insert = con.prepareStatement("INSERT INTO [" + UserObject.TABLE_NAME + "] (["
				+ UserObject.COLUMN_USERNAME + "],[" + UserObject.COLUMN_PASSWORD + "],[" + UserObject.COLUMN_SALT
				+ "],[" + UserObject.COLUMN_OBSERVABLE + "]) VALUES(?,?,?,?)");
		// --
		insert.setString(1, user.getName());
		insert.setBytes(2, user.getEncryptedPassword());
		insert.setBytes(3, user.getSalt());
		insert.setBoolean(4, user.isObservable());
		// --
		try {
			result = insert.execute();
			if (!result) {
				result = insert.getUpdateCount() == 1;
			}
		} finally {
			insert.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return result;
	}

	// ------------------------------------------------------------------------

	public static boolean changePassword(double userId, String encryptedPassword) throws SQLException {
		return false;
	}

	// ------------------------------------------------------------------------

	public static boolean insertLocation(GeolocationObject location) throws SQLException{
		boolean result = false;
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement insert = con.prepareStatement("INSERT INTO [" + GeolocationObject.TABLE_NAME + "] ([" + GeolocationObject.COLUMN_USER_FK + "],["
				+ GeolocationObject.COLUMN_TIMESTAMP + "],[" + GeolocationObject.COLUMN_LONGITUDE + "],[" + GeolocationObject.COLUMN_LATITUDE + "]) VALUES(?,?,?,?)");
		// --
		
		insert.setDouble(1, location.getUserFK());
		insert.setDouble(2, location.getTimestamp());
		
		insert.setDouble(3, location.getLongitude());
		insert.setDouble(4, location.getLatitude());

		// --
		try {
			result = insert.execute();
			if (!result) {
				result = insert.getUpdateCount() == 1;
			}
		} finally {
			insert.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return result;
	}
}
