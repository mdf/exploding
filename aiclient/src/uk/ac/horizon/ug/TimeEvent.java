package uk.ac.horizon.ug;

public class TimeEvent
{
	String ref;
	
	Integer id;
	
	private String name;
	
	String description;
	
	Integer zone;
	
	Integer enabled;
	
	Integer absolute;
	
	private Integer startTime;
	
	private Integer endTime;

	public Integer track;
	
	Integer rgb;
	
	IndexList indexList;
	
	public boolean played = false;

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	public Integer getStartTime() {
		return startTime;
	}
	
	public Integer getEndTime() {
		return endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return description;
	}

}
