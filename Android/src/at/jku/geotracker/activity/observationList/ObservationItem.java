package at.jku.geotracker.activity.observationList;

public class ObservationItem {
	private long endTime;
	private long startTime;
	private String observed;
	private int id;

	public ObservationItem() {
		super();
	}

	public ObservationItem(long endTime, long startTime, String observed, int id) {
		super();
		this.endTime = endTime;
		this.startTime = startTime;
		this.observed = observed;
		this.id = id;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getObserved() {
		return observed;
	}

	public void setObserved(String observed) {
		this.observed = observed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
