package at.jku.se.tracking.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {
	private final Semaphore sem;
	private final Queue<Connection> resources = new ConcurrentLinkedQueue<Connection>();
	private String connectionString;

	// ------------------------------------------------------------------------

	public ConnectionPool(int maxConnections, String connectionString) {
		this.sem = new Semaphore(maxConnections, true);
		this.connectionString = connectionString;
	}

	// ------------------------------------------------------------------------

	public Connection getConnection(long maxWaitMillis) throws SQLException {
		// First, get permission to take or create a resource
		try {
			sem.tryAcquire(maxWaitMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new SQLException(e);
		}

		// Then, actually take one if available...
		Connection res = resources.poll();
		if (res != null) {
			return res;
		}

		// ...or create one if none available (and resources are available)
		try {
			if (sem.availablePermits() > 0) {
				return openConnection();
			} else {
				throw new SQLException("unable to acquire connection within " + maxWaitMillis + "ms");
			}
		} catch (Exception e) {
			// Don't hog the permit if we failed to create a resource!
			sem.release();
			throw new SQLException(e);
		}
	}

	// ------------------------------------------------------------------------

	public void returnResource(Connection con) {
		resources.add(con);
		sem.release();
	}

	// ------------------------------------------------------------------------

	public void releasePool() throws SQLException {
		for (Connection c : resources) {
			disconnect(c);
		}
	}

	// ------------------------------------------------------------------------

	private Connection openConnection() throws SQLException {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection con = DriverManager.getConnection(connectionString);
			return con;
		} catch (ClassNotFoundException e) {
			throw new SQLException(e);
		}
	}

	// ------------------------------------------------------------------------

	private static void disconnect(Connection con) throws SQLException {
		if (con != null && !con.isClosed()) {
			con.close();
		}
	}
}