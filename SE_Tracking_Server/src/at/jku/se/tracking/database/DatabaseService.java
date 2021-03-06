package at.jku.se.tracking.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import at.jku.se.tracking.utils.PasswordEncryptionService;

public class DatabaseService {
	private static final int MAX_RESOURCES = 16; // Number of concurrent connections
	private static final int POOL_WAIT_TIME = 60000; // Wait at most one minute for a database connection
	private static String CONNECTION_STRING;
	private static ConnectionPool CONNECTION_POOL;

	// ------------------------------------------------------------------------

	private static IQueryStrings QUERY_STRINGS;

	// ------------------------------------------------------------------------

	public static void main(String[] args) {
		System.out.println("Connecting...");

		try {
			Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
			System.out.println("Connected!");

			System.out.println("insert user");
			UserObject user = null;
			byte[] salt = PasswordEncryptionService.generateSalt();
			byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword("password", salt);
			// Instantiate User Object
			user = new UserObject("testuser", encryptedPassword, salt, true);
			// Store user
			long userId = DatabaseService.insertUser(user);
			System.out.println("userid=" + userId);

			CONNECTION_POOL.returnResource(con);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static {
		// Initialize DB connection with settings from config file
		Properties prop = ConfigLoader.loadConfig();
		if (prop != null) {
			String dbms = prop.getProperty("dbms");
			String host = prop.getProperty("dbhost");
			String dbname = prop.getProperty("dbname");
			String user = prop.getProperty("dbuser");
			String pass = prop.getProperty("dbpassword");
			// --
			if (dbms == null) {
				dbms = "sqlserver";
			}
			// --
			if (!host.equals("test")) { // setting for testing purpose when no database is present
				switch (dbms) {
				case "sqlserver":
					CONNECTION_STRING = "jdbc:sqlserver://" + host + ";";
					CONNECTION_STRING += "database=" + dbname + ";user=" + user + ";password=" + pass;
					QUERY_STRINGS = new QueryStringsSQLServer();
					break;
				case "mysql":
					CONNECTION_STRING = "jdbc:mysql://" + host + "/" + dbname;
					CONNECTION_STRING += "?user=" + user + "&password=" + pass;
					QUERY_STRINGS = new QueryStringsMySQL();
					break;
				default:
					throw new RuntimeException("invalid dbms type");
				}

				// --
				CONNECTION_POOL = new ConnectionPool(MAX_RESOURCES, dbms, CONNECTION_STRING);
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
		PreparedStatement query = con.prepareStatement(QUERY_STRINGS.getQueryUserById(id));
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
		PreparedStatement query = con.prepareStatement(QUERY_STRINGS.getQueryUserByName(username));
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
		PreparedStatement query = con.prepareStatement(QUERY_STRINGS.getQueryUsers(onlyObservable));
		if (onlyObservable) {
			query.setBoolean(1, true);
		}
		// --
		ResultSet rs = query.executeQuery();
		// --
		try {
			while (rs.next()) {
				long id = rs.getLong(UserObject.COLUMN_ID);
				String username = rs.getString(UserObject.COLUMN_USERNAME);
				boolean observable = rs.getBoolean(UserObject.COLUMN_OBSERVABLE);
				// --
				users.add(new UserObject(id, username, observable));
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
		PreparedStatement insert = con.prepareStatement(QUERY_STRINGS.getInsertUser(), Statement.RETURN_GENERATED_KEYS);
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
		// --
		PreparedStatement insert = con.prepareStatement(QUERY_STRINGS.getInsertLocation());
		// --
		insert.setLong(1, location.getUserFK());
		insert.setLong(2, location.getTimestamp());
		// --
		insert.setDouble(3, location.getLongitude());
		insert.setDouble(4, location.getLatitude());
		// --
		setDoubleOrNull(insert, 5, location.getAccuracy());
		setFloatOrNull(insert, 6, location.getAltitude());
		setDoubleOrNull(insert, 7, location.getAltitudeAccuracy());
		setDoubleOrNull(insert, 8, location.getHeading());
		setFloatOrNull(insert, 9, location.getSpeed());
		// --
		try {
			result = insert.executeUpdate() == 1;
		} finally {
			insert.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return result;
	}

	// ------------------------------------------------------------------------

	public static boolean startTrackingSession(long observer, long observed, long starttime) throws SQLException {
		boolean result = false;
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement insert = con.prepareStatement(QUERY_STRINGS.getInsertSession());
		// --
		insert.setLong(1, observer);
		insert.setLong(2, observed);
		insert.setLong(3, starttime);
		// --
		try {
			result = insert.executeUpdate() == 1;
		} finally {
			insert.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return result;
	}

	// ------------------------------------------------------------------------

	public static boolean stopTrackingSession(long id, long endtime, long canceledBy) throws SQLException {
		boolean result = false;
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement insert = con.prepareStatement(QUERY_STRINGS.getUpdateSession());
		// --
		insert.setLong(1, endtime);
		insert.setLong(2, canceledBy);
		insert.setLong(3, id);
		// --
		try {
			result = insert.executeUpdate() == 1;
		} finally {
			insert.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return result;
	}

	public static boolean setObservableSetting(Long userId, boolean isObservable) throws SQLException {
		boolean result = false;
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);

		//@formatter:off
		PreparedStatement insert = 
			con.prepareStatement("UPDATE [" + UserObject.TABLE_NAME + "] SET "
			+ "[" + UserObject.COLUMN_OBSERVABLE + "] = ? "
			+ "WHERE " + UserObject.COLUMN_ID + " = ?");			
		//@formatter:on

		insert.setBoolean(1, isObservable);
		insert.setLong(2, userId);
		// --
		try {
			result = insert.executeUpdate() == 1;
		} finally {
			insert.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return result;
	}

	// ------------------------------------------------------------------------

	public static List<TrackingSessionObject> queryTrackingSessions(long userId, boolean listObservedByUser, boolean listObserversOfUser,
			boolean activeOnly) throws SQLException {
		List<TrackingSessionObject> sessions = new ArrayList<TrackingSessionObject>();
		if (CONNECTION_POOL == null || !listObservedByUser && !listObserversOfUser) {
			return sessions;
		}
		// --
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		PreparedStatement query = null;
		String stmt = QUERY_STRINGS.getQuerySessions(listObservedByUser, listObserversOfUser, activeOnly);
		query = con.prepareStatement(stmt);
		// --
		if (listObservedByUser && listObserversOfUser) {
			query.setLong(1, userId);
			query.setLong(2, userId);
		} else {
			query.setLong(1, userId);
		}
		// --
		ResultSet rs = query.executeQuery();
		// --
		try {
			while (rs.next()) {
				long observed = rs.getLong(TrackingSessionObject.COLUMN_OBSERVED);
				long observer = rs.getLong(TrackingSessionObject.COLUMN_OBSERVER);
				long starttime = rs.getLong(TrackingSessionObject.COLUMN_STARTTIME);
				long endtime = rs.getLong(TrackingSessionObject.COLUMN_ENDTIME);
				long canceledBy = rs.getLong(TrackingSessionObject.COLUMN_CANCELED_BY);
				long id = rs.getLong(TrackingSessionObject.COLUMN_ID);
				// --
				sessions.add(new TrackingSessionObject(id, observed, observer, starttime, endtime, canceledBy));
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

	// ------------------------------------------------------------------------

	public static List<GeolocationObject> getTrackingSessionPoints(long sessionId) throws SQLException {
		if (CONNECTION_POOL == null) {
			return null;
		}
		// --
		List<GeolocationObject> points = new ArrayList<GeolocationObject>();
		// --
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);

		//@formatter:off
		String stmt = QUERY_STRINGS.getQuerySessionPoints();
		//@formatter:on
		PreparedStatement query = con.prepareStatement(stmt);
		// System.out.println("stmt: " + stmt + " for session id " + sessionId);
		query.setLong(1, sessionId);

		// --
		ResultSet rs = query.executeQuery();
		// --
		try {
			while (rs.next()) {
				long timestamp = rs.getLong(GeolocationObject.COLUMN_TIMESTAMP);
				double longitude = rs.getDouble(GeolocationObject.COLUMN_LONGITUDE);
				double latitude = rs.getDouble(GeolocationObject.COLUMN_LATITUDE);
				double accuracy = rs.getDouble(GeolocationObject.COLUMN_ACCURACY);
				// --
				points.add(new GeolocationObject(-1, -1, timestamp, longitude, latitude, accuracy, Float.NaN, Double.NaN, Double.NaN, Float.NaN));
			}
		} finally {
			rs.close();
			query.close();
		}
		// --
		CONNECTION_POOL.returnResource(con);
		// --
		return points;
	}

	// ------------------------------------------------------------------------

	public static List<TrackingSessionObject> queryTrackingSessions(long observer, long observed, boolean activeOnly) throws SQLException {
		if (CONNECTION_POOL == null) {
			return null;
		}
		// --
		List<TrackingSessionObject> sessions = new ArrayList<TrackingSessionObject>();
		Connection con = CONNECTION_POOL.getConnection(POOL_WAIT_TIME);
		// --
		String stmt = QUERY_STRINGS.getQuerySessions(activeOnly);
		// --
		PreparedStatement query = con.prepareStatement(stmt);
		// --
		query.setLong(1, observer);
		query.setLong(2, observed);
		// --
		ResultSet rs = query.executeQuery();
		// --
		try {
			while (rs.next()) {
				long id = rs.getLong(TrackingSessionObject.COLUMN_ID);
				long starttime = rs.getLong(TrackingSessionObject.COLUMN_STARTTIME);
				long endtime = rs.getLong(TrackingSessionObject.COLUMN_ENDTIME);
				long canceledBy = rs.getLong(TrackingSessionObject.COLUMN_CANCELED_BY);
				// --
				sessions.add(new TrackingSessionObject(id, observed, observer, starttime, endtime, canceledBy));
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

	// ------------------------------------------------------------------------

	private static void setDoubleOrNull(PreparedStatement stmt, int idx, double value) throws SQLException {
		if (Double.isNaN(value)) {
			stmt.setNull(idx, java.sql.Types.FLOAT);
		} else {
			stmt.setDouble(idx, value);
		}
	}

	private static void setFloatOrNull(PreparedStatement stmt, int idx, float value) throws SQLException {
		if (Float.isNaN(value)) {
			stmt.setNull(idx, java.sql.Types.FLOAT);
		} else {
			stmt.setFloat(idx, value);
		}
	}
}
