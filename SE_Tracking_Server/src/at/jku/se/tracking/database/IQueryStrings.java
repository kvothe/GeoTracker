package at.jku.se.tracking.database;

public interface IQueryStrings {
	public String getQueryUserById(long id);
	public String getQueryUserByName(String name);
	public String getQueryUsers(boolean observable);

	public String getInsertUser();
	public String getInsertLocation();
	public String getInsertSession();

	public String getUpdateSession();
	public String getUpdateUser();

	public String getQuerySessionPoints();

	public String getQuerySessions(boolean listObservedByUser, boolean listObserversOfUser, boolean activeOnly);
	public String getQuerySessions(boolean activeOnly);
}
