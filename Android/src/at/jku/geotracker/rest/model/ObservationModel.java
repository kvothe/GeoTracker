package at.jku.geotracker.rest.model;

import at.jku.geotracker.rest.interfaces.ResponseListener;

public class ObservationModel extends AbstractModel {

	private String targetUsername;

	public ObservationModel(String target, ResponseListener listener) {
		super(listener);
		this.targetUsername = target;
	}

	public String getTarget() {
		return targetUsername;
	}

	public void setTarget(String target) {
		this.targetUsername = target;
	}
}
