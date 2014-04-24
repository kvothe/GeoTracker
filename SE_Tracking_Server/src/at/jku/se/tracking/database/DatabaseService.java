package at.jku.se.tracking.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

	public static UserObject queryUser(long id) throws SQLException {
		if (CONNECTION_POOL == null) {
			return null;
		}
		// --
		UserObject user = null;
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement query = con.prepareStatement("SELECT * FROM [" + UserObject.TABLE_NAME + "] WHERE " + UserObject.COLUMN_ID + "=?");
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
		PreparedStatement query = con.prepareStatement("SELECT * FROM [" + UserObject.TABLE_NAME + "] WHERE " + UserObject.COLUMN_USERNAME + "=?");
		query.setString(1, username);
		ResultSet rs = query.executeQuery();
		// --
		try {
			while (rs.next()) {
				// TODO normalize username (case-insenstivie?)
				long id = rs.getLong(UserObject.COLUMN_ID);
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

	public static List<UserObject> queryUsers(boolean onlyObservable) throws SQLException {
		if (CONNECTION_POOL == null) {
			return null;
		}
		// --
		List<UserObject> users = new ArrayList<UserObject>();
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement query = null;
		if (onlyObservable) {
			query = con.prepareStatement("SELECT [" + UserObject.COLUMN_USERNAME + "],[" + UserObject.COLUMN_OBSERVABLE + "] FROM ["
					+ UserObject.TABLE_NAME + "] WHERE " + UserObject.COLUMN_OBSERVABLE + "=?");
			query.setBoolean(1, true);
		} else {
			query = con.prepareStatement("SELECT [" + UserObject.COLUMN_USERNAME + "],[" + UserObject.COLUMN_OBSERVABLE + "] FROM ["
					+ UserObject.TABLE_NAME + "]");
		}
		// --
		ResultSet rs = query.executeQuery();
		// --
		try {
			while (rs.next()) {
				String username = rs.getString(UserObject.COLUMN_USERNAME);
				boolean observable = rs.getBoolean(UserObject.COLUMN_OBSERVABLE);
				// --
				users.add(new UserObject(username, observable));
			}
		} finally {
			rs.close();
			query.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return users;
	}

	// ------------------------------------------------------------------------

	public static long insertUser(UserObject user) throws SQLException {
		long result = -1;
		if (CONNECTION_POOL == null) {
			return result;
		}
		// --
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement insert = con.prepareStatement("INSERT INTO [" + UserObject.TABLE_NAME + "] ([" + UserObject.COLUMN_USERNAME + "],["
				+ UserObject.COLUMN_PASSWORD + "],[" + UserObject.COLUMN_SALT + "],[" + UserObject.COLUMN_OBSERVABLE + "]) VALUES(?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS);
		// --
		insert.setString(1, user.getName());
		insert.setBytes(2, user.getEncryptedPassword());
		insert.setBytes(3, user.getSalt());
		insert.setBoolean(4, user.isObservable());
		// --
		try {
			result = insert.executeUpdate();
			if (result == 1) {
				ResultSet key = insert.getGeneratedKeys();
				key.next();
				result = key.getLong(1);
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

	public static boolean insertLocation(GeolocationObject location) throws SQLException {
		boolean result = false;
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);

		//@formatter:off
		PreparedStatement insert = 
				con.prepareStatement("INSERT INTO [" + GeolocationObject.TABLE_NAME + "] "
				+ "([" + GeolocationObject.COLUMN_USER_FK + "],[" + GeolocationObject.COLUMN_TIMESTAMP + "],"
				+ "[" + GeolocationObject.COLUMN_LONGITUDE + "],[" + GeolocationObject.COLUMN_LATITUDE + "],"
				+ "[" + GeolocationObject.COLUMN_ACCURACY + "],[" + GeolocationObject.COLUMN_ALTITUDE + "],"
				+ "[" + GeolocationObject.COLUMN_ALTITUDE_ACCURACCY + "],[" + GeolocationObject.COLUMN_HEADING + "],"
				+ "[" + GeolocationObject.COLUMN_SPEED + "]) "
				+ "VALUES(?,?,?,?,?,?,?,?,?)");
		//@formatter:on

		insert.setLong(1, location.getUserFK());
		insert.setLong(2, location.getTimestamp());
		// --
		insert.setDouble(3, location.getLongitude());
		insert.setDouble(4, location.getLatitude());
		insert.setDouble(5, location.getAccuracy());
		insert.setFloat(6, location.getAltitude());
		insert.setDouble(7, location.getAltitudeAccuracy());
		insert.setDouble(8, location.getHeading());
		insert.setFloat(9, location.getSpeed());
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

	public static List<TrackingSessionObject> queryTrackingSessions(long userId, boolean isObserver) throws SQLException {
		if (CONNECTION_POOL == null) {
			return null;
		}
		// --
		List<TrackingSessionObject> sessions = new ArrayList<TrackingSessionObject>();
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement query = null;
		if (isObserver) {
			query = con.prepareStatement("SELECT * FROM [" + TrackingSessionObject.TABLE_NAME + "] WHERE " + TrackingSessionObject.COLUMN_OBSERVER
					+ "=?");
			query.setLong(1, userId);
		} else {
			query = con.prepareStatement("SELECT * FROM [" + TrackingSessionObject.TABLE_NAME + "] WHERE " + TrackingSessionObject.COLUMN_OBSERVED
					+ "=?");
			query.setLong(1, userId);
		}
		// --
		ResultSet rs = query.executeQuery();
		// --
		try {
			while (rs.next()) {
				long observer = -1;
				long observed = -1;
				if (isObserver) {
					observer = userId;
					observed = rs.getLong(TrackingSessionObject.COLUMN_OBSERVED);
				} else {
					observed = userId;
					observer = rs.getLong(TrackingSessionObject.COLUMN_OBSERVER);
				}
				long starttime = rs.getLong(TrackingSessionObject.COLUMN_STARTTIME);
				long endtime = rs.getLong(TrackingSessionObject.COLUMN_ENDTIME);
				long canceledBy = rs.getLong(TrackingSessionObject.COLUMN_CANCELED_BY);
				// --
				sessions.add(new TrackingSessionObject(observed, observer, starttime, endtime, canceledBy));
			}
		} finally {
			rs.close();
			query.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return sessions;
	}
}
