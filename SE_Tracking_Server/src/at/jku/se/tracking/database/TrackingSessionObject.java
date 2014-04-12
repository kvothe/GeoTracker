package at.jku.se.tracking.database;

public class TrackingSessionObject {

	static final String TABLE_NAME = "trackingsession";
	static final String COLUMN_ID = "id";
	static final String COLUMN_OBSERVED = "observed";
	static final String COLUMN_OBSERVER = "observer";
	static final String COLUMN_STARTTIME = "starttime";
	static final String COLUMN_ENDTIME = "endtime";
	static final String COLUMN_CANCELED_BY = "canceled_by";

	// ------------------------------------------------------------------------

	private double id;
	private double observed;
	private double observer;
	private double starttime;
	private double endtime;
	private double canceledBy;

	// ------------------------------------------------------------------------

	public TrackingSessionObject(double observed, double observer, double starttime, double endtime, double canceledBy) {
		this(-1, observed, observer, starttime, endtime, canceledBy);
	}
	public TrackingSessionObject(double id, double observed, double observer, double starttime, double endtime,
			double canceledBy) {
		super();
		this.id = id;
		this.observed = observed;
		this.observer = observer;
		this.starttime = starttime;
		this.endtime = endtime;
		this.canceledBy = canceledBy;
	}

	// ------------------------------------------------------------------------

	public double getId() {
		return id;
	}
	public double getObserved() {
		return observed;
	}
	public double getObserver() {
		return observer;
	}
	public double getStarttime() {
		return starttime;
	}
	public double getEndtime() {
		return endtime;
	}
	public double getCanceledBy() {
		return canceledBy;
	}

}
