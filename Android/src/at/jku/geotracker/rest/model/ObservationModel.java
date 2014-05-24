package at.jku.geotracker.rest.model;

import android.app.Fragment;

public class ObservationModel {

	private String observed;
	private Fragment userListFragment;

	public ObservationModel() {
		super();
	}

	public ObservationModel(String observed, Fragment userListFragment) {
		super();
		this.observed = observed;
		this.userListFragment = userListFragment;
	}

	public String getObserved() {
		return observed;
	}

	public void setObserved(String observed) {
		this.observed = observed;
	}

	public Fragment getUserListFragment() {
		return userListFragment;
	}

	public void setUserListFragment(Fragment userListFragment) {
		this.userListFragment = userListFragment;
	}

}
