package uk.ac.horizon.ug;

import java.util.ArrayList;
import java.util.List;

public class GameState
{
	private String version;
	
	String location;
	
	private Integer startYear;
	
	Integer endYear;
	
	private List<TimeEvent> timeEvents = new ArrayList<TimeEvent>();
	
	private List<Zone> zones = new ArrayList<Zone>();

	public void setStartYear(Integer startYear) {
		this.startYear = startYear;
	}

	public Integer getStartYear() {
		return startYear;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setZones(List<Zone> zones) {
		this.zones = zones;
	}

	public List<Zone> getZones() {
		return zones;
	}

	public void setTimeEvents(List<TimeEvent> timeEvents) {
		this.timeEvents = timeEvents;
	}

	public List<TimeEvent> getTimeEvents() {
		return timeEvents;
	}

}
