package uk.ac.horizon.ug.exploding.author.model;

import java.util.ArrayList;
import java.util.List;

public class Zone
{
	public String ref;
	
	public Integer id;
	
	public String name;
	
	public Integer polygon;
	
	public Double radius;

	public List<Field> fields = new ArrayList<Field>();
	
}
