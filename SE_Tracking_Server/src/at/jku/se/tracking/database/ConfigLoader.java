package at.jku.se.tracking.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_DATABASE = "database.properties";

	public static Properties loadConfig() {

		Properties prop = new Properties();
		InputStream input = null;

		try {
			File configDir = new File(CONFIG_DIR);
			File configFile = new File(configDir, CONFIG_DATABASE);
			// --
			if (configFile.exists()) {
				input = new FileInputStream(configFile);

				// load a properties file
				prop.load(input);

				// get the property value and print it out
				System.out.println(prop.getProperty("dbhost"));
				System.out.println(prop.getProperty("dbname"));
				System.out.println(prop.getProperty("dbuser"));
				System.out.println(prop.getProperty("dbpassword"));
			} else {
				System.err.println("no config file");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// --
		return prop;
	}
}
