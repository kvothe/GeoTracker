package at.jku.geotracker.rest.model;

import at.jku.geotracker.rest.interfaces.ResponseListener;

public class SettingsModel extends AbstractModel {

	private boolean observable;

	public SettingsModel(boolean observable, ResponseListener listener) {
		super(listener);
		this.observable = observable;
	}

	public boolean isObservable() {
		return observable;
	}

	public void setObservable(boolean observable) {
		this.observable = observable;
	}
}
