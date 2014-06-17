package at.jku.se.tracking.database;

public class QueryStringsMySQL implements IQueryStrings {

	@Override
	public String getQueryUserById(long id) {
		String stmt = "SELECT * FROM " + UserObject.TABLE_NAME + " WHERE " + UserObject.COLUMN_ID + "=?";
		return stmt;
	}

	@Override
	public String getQueryUserByName(String name) {
		String stmt = "SELECT * FROM " + UserObject.TABLE_NAME + " WHERE " + UserObject.COLUMN_USERNAME + "=?";
		return stmt;
	}

	@Override
	public String getQueryUsers(boolean observable) {
		String stmt = null;
		if (observable) {
			stmt = "SELECT " + UserObject.COLUMN_ID + "," + UserObject.COLUMN_USERNAME + "," + UserObject.COLUMN_OBSERVABLE + " FROM "
					+ UserObject.TABLE_NAME + " WHERE " + UserObject.COLUMN_OBSERVABLE + "=?";
		} else {
			stmt = "SELECT " + UserObject.COLUMN_ID + "," + UserObject.COLUMN_USERNAME + "," + UserObject.COLUMN_OBSERVABLE + " FROM "
					+ UserObject.TABLE_NAME + "";
		}
		return stmt;
	}

	@Override
	public String getInsertUser() {
		String stmt = "INSERT INTO " + UserObject.TABLE_NAME + " (" + UserObject.COLUMN_USERNAME + "," + UserObject.COLUMN_PASSWORD + ","
				+ UserObject.COLUMN_SALT + "," + UserObject.COLUMN_OBSERVABLE + ") VALUES(?,?,?,?)";
		return stmt;
	}

	@Override
	public String getInsertLocation() {
		String stmt = "INSERT INTO " + GeolocationObject.TABLE_NAME + " " + "(" + GeolocationObject.COLUMN_USER_FK + ","
				+ GeolocationObject.COLUMN_TIMESTAMP + "," + "" + GeolocationObject.COLUMN_LONGITUDE + "," + GeolocationObject.COLUMN_LATITUDE
				+ "," + "" + GeolocationObject.COLUMN_ACCURACY + "," + GeolocationObject.COLUMN_ALTITUDE + "," + ""
				+ GeolocationObject.COLUMN_ALTITUDE_ACCURACCY + "," + GeolocationObject.COLUMN_HEADING + "," + ""
				+ GeolocationObject.COLUMN_SPEED + ") " + "VALUES(?,?,?,?,?,?,?,?,?)";
		return stmt;
	}

	@Override
	public String getInsertSession() {
		String stmt = "INSERT INTO " + TrackingSessionObject.TABLE_NAME + " " + "(" + TrackingSessionObject.COLUMN_OBSERVER + ","
				+ TrackingSessionObject.COLUMN_OBSERVED + "," + "" + TrackingSessionObject.COLUMN_STARTTIME + ") " + "VALUES(?,?,?)";
		return stmt;
	}

	@Override
	public String getUpdateSession() {
		String stmt = "UPDATE " + TrackingSessionObject.TABLE_NAME + " SET " + "" + TrackingSessionObject.COLUMN_ENDTIME + " = ?,"
				+ TrackingSessionObject.COLUMN_CANCELED_BY + " = ? " + "WHERE " + TrackingSessionObject.COLUMN_ID + " = ?";
		return stmt;
	}

	@Override
	public String getUpdateUser() {
		String stmt = "UPDATE " + UserObject.TABLE_NAME + " SET " + "" + UserObject.COLUMN_OBSERVABLE + " = ? " + "WHERE " + UserObject.COLUMN_ID
				+ " = ?";
		return stmt;
	}

	@Override
	public String getQuerySessionPoints() {
		String stmt = "SELECT l." + GeolocationObject.COLUMN_TIMESTAMP + ", " + "l." + GeolocationObject.COLUMN_LONGITUDE + ", " + "l."
				+ GeolocationObject.COLUMN_LATITUDE + ", " + "l." + GeolocationObject.COLUMN_ACCURACY + " " + "FROM "
				+ TrackingSessionObject.TABLE_NAME + " s, " + GeolocationObject.TABLE_NAME + " l " + "WHERE s." + TrackingSessionObject.COLUMN_ID
				+ " = ? " + "AND l." + GeolocationObject.COLUMN_TIMESTAMP + " >= s." + TrackingSessionObject.COLUMN_STARTTIME + " "
				+ "AND l."
				+ GeolocationObject.COLUMN_ACCURACY
				+ " <= 70 " // tests mit Bestandsdaten zeigen gute Ergebnisse
				+ "AND (s." + TrackingSessionObject.COLUMN_ENDTIME + " IS NULL OR l." + GeolocationObject.COLUMN_TIMESTAMP + " <= s."
				+ TrackingSessionObject.COLUMN_ENDTIME + ")" + "AND s." + TrackingSessionObject.COLUMN_OBSERVED + " = l."
				+ GeolocationObject.COLUMN_USER_FK + " " // ?? die einschränkung sollte schon davor beim ermitteln der
															// session ID erfolgen, so kann man nicht nach sessions wo
															// man OBSERVER ist abfragen
				// die Einschr�nkung kann nicht weggelassen werden, sonst w�rde ja observer-egal abgefragt. Man k�nnte
				// den Observer vorher ermitteln, aber wenn hier schon ein Join ist dann k�nnen wir den auch verwenden.
				// + "AND l." + GeolocationObject.COLUMN_TIMESTAMP + " >= ( "
				// + "SELECT MAX("+GeolocationObject.COLUMN_TIMESTAMP+")-86400000 FROM " + GeolocationObject.TABLE_NAME
				// + ") " // ?? max 24h vom letzten eintrag in der tabelle? eine session sollten wir schon komplett
				// anzeigen, wenn dann die länge einer session beschränken
				+ "ORDER BY l." + GeolocationObject.COLUMN_TIMESTAMP + "";
		return stmt;
	}

	@Override
	public String getQuerySessions(boolean listObservedByUser, boolean listObserversOfUser, boolean activeOnly) {
		String stmt = "SELECT * FROM " + TrackingSessionObject.TABLE_NAME + " WHERE ";
		if (listObservedByUser && listObserversOfUser) {
			stmt += "" + TrackingSessionObject.COLUMN_OBSERVER + " = ? OR " + TrackingSessionObject.COLUMN_OBSERVED + " = ?";
			if (activeOnly) {
				stmt += " AND " + TrackingSessionObject.COLUMN_ENDTIME + " IS NULL";
			}
		} else if (listObservedByUser) {
			stmt += "" + TrackingSessionObject.COLUMN_OBSERVER + " = ?";
			if (activeOnly) {
				stmt += " AND " + TrackingSessionObject.COLUMN_ENDTIME + " IS NULL";
			}
		} else if (listObserversOfUser) {
			stmt += "" + TrackingSessionObject.COLUMN_OBSERVED + " = ?";
			if (activeOnly) {
				stmt += " AND " + TrackingSessionObject.COLUMN_ENDTIME + " IS NULL";
			}
		}
		return stmt;
	}

	@Override
	public String getQuerySessions(boolean activeOnly) {
		String stmt = "SELECT * FROM " + TrackingSessionObject.TABLE_NAME + " WHERE " + TrackingSessionObject.COLUMN_OBSERVER + " = ? AND "
				+ TrackingSessionObject.COLUMN_OBSERVED + " = ?";
		// --
		if (activeOnly) {
			stmt += " AND " + TrackingSessionObject.COLUMN_ENDTIME + " IS NULL";
		}
		// --
		return stmt;
	}

}
