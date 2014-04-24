package at.jku.se.tracking.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import at.jku.se.tracking.database.ConfigLoader;

public class TestDatabaseConnection {
	public static void main(String[] args) {
		Properties prop = ConfigLoader.loadConfig();
		if (prop != null) {
			String host = prop.getProperty("dbhost");
			String dbname = prop.getProperty("dbname");
			String user = prop.getProperty("dbuser");
			String pass = prop.getProperty("dbpassword");
			// --
			String connectionString = "jdbc:sqlserver://" + host + ";";
			connectionString += "database=" + dbname + ";user=" + user + ";password=" + pass;
			// --
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				Connection con = DriverManager.getConnection(connectionString);
				con.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// TODO
		}
	}
}
