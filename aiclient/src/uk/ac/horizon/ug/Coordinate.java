package uk.ac.horizon.ug;

public class Coordinate
{
	private Double latitude;
	
	private Double longitude;
	
	private Double elevation;

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	public Double getElevation() {
		return elevation;
	}
}
