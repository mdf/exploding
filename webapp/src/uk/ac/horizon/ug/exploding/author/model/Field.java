package uk.ac.horizon.ug.exploding.author.model;

import java.util.ArrayList;
import java.util.List;

public class Field
{
	public String name;
	
	public String type;

	public AttributeSet attributeSet;
	
	public List<Coordinate> coordinates = new ArrayList<Coordinate>();
}
