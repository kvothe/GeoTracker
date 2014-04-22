package at.jku.se.tracking.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyFileWriter {
	public static void main(String[] args) {

		Properties prop = new Properties();
		OutputStream output = null;

		try {
			File configDir = new File("config");
			configDir.mkdir();
			
			output = new FileOutputStream("config/database.properties");

			// set the properties value
			prop.setProperty("dbhost", "DEVELOPMENTVM\\SQLEXPRESS");
			prop.setProperty("dbname", "GeoTracker");
			prop.setProperty("dbuser", "geo");
			prop.setProperty("dbpassword", "tracker");

			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
