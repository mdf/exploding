package uk.ac.horizon.ug;

import java.util.ArrayList;
import java.util.List;

public class Field
{
	private String name;
	
	private String type;

	private AttributeSet attributeSet;
	
	private List<Coordinate> coordinates = new ArrayList<Coordinate>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setAttributeSet(AttributeSet attributeSet) {
		this.attributeSet = attributeSet;
	}

	public AttributeSet getAttributeSet() {
		return attributeSet;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}
}
