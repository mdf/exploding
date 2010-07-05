package uk.ac.horizon.ug.exploding.author.model;

import java.util.ArrayList;
import java.util.List;

public class GameState
{
	public String version;
	
	public String location;
	
	public Integer startYear;
	
	public Integer endYear;
	
	public List<TimeEvent> timeEvents = new ArrayList<TimeEvent>();
	
	public List<Zone> zones = new ArrayList<Zone>();

}
