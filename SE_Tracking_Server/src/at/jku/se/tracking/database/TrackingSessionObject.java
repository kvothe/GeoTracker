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

	private long id;
	private long observed;
	private long observer;
	private long starttime;
	private long endtime;
	private long canceledBy;

	// ------------------------------------------------------------------------

	public TrackingSessionObject(long observed, long observer, long starttime, long endtime, long canceledBy) {
		this(-1, observed, observer, starttime, endtime, canceledBy);
	}	
	public TrackingSessionObject(long id, long observed, long observer, long starttime, long endtime, long canceledBy) {
		super();
		this.id = id;
		this.observed = observed;
		this.observer = observer;
		this.starttime = starttime;
		this.endtime = endtime;
		this.canceledBy = canceledBy;
	}

	// ------------------------------------------------------------------------

	public long getId() {
		return id;
	}
	public long getObserved() {
		return observed;
	}
	public long getObserver() {
		return observer;
	}
	public long getStarttime() {
		return starttime;
	}
	public long getEndtime() {
		return endtime;
	}
	public long getCanceledBy() {
		return canceledBy;
	}

}
