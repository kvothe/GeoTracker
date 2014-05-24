package at.jku.geotracker.activity.userlist;

public class UserItem {
	private boolean isObserved;
	private String name;
	private boolean observable;
	private boolean online;

	public UserItem() {
		super();
	}

	public UserItem(boolean isObserved, String name, boolean observable,
			boolean online) {
		super();
		this.isObserved = isObserved;
		this.name = name;
		this.observable = observable;
		this.online = online;
	}

	public boolean isObserved() {
		return isObserved;
	}

	public void setObserved(boolean isObserved) {
		this.isObserved = isObserved;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isObservable() {
		return observable;
	}

	public void setObservable(boolean observable) {
		this.observable = observable;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

}
