package uk.ac.horizon.ug.exploding.engine;

import uk.ac.horizon.ug.exploding.db.Position;
import uk.ac.horizon.ug.exploding.db.Zone;

public class ZoneCache
{
	Zone zone;
	
	public ZoneCache(Zone zone)
	{
		this.zone = zone;
	}

	public boolean isGameZone() {
		return zone.getName()!=null && ("main".equals(zone.getName().toLowerCase()) || zone.getName().startsWith("~"));
	}

	public boolean contains(double latitude, double longitude)
	{
		if(this.zone == null || this.zone.getPolygon() != 1)
		{
			return false;
		}
		
		Position [] ps = zone.getCoordinates();

		boolean oddTransitions = false;
		for( int i = 0, j = ps.length-1; i < ps.length; j = i++ ) {

			if( ( ps[ i ].getLatitude() < latitude && ps[ j ].getLatitude() >= latitude ) || ( ps[ j ].getLatitude() < latitude && ps[ i ].getLatitude() >= latitude ) ) {
				if( ps[ i ].getLongitude() + ( latitude - ps[ i ].getLatitude() ) / ( ps[ j ].getLatitude() - ps[ i ].getLatitude() ) * ( ps[ j ].getLongitude() - ps[ i ].getLongitude() ) < longitude ) {
					oddTransitions = !oddTransitions;          
				}
			}
		}

		return oddTransitions;
	}

	
	static double EARTH_RADIUS = 6335437;
	private static double getDistance(double latitude, double longitude,
			Double latitude2, Double longitude2)
	{
		// quick and dirty
		double dy = (latitude2-latitude)*2*Math.PI*EARTH_RADIUS/360;
		double dx = (longitude-longitude)*2*Math.PI*EARTH_RADIUS/360*Math.cos((latitude+latitude2)/2);
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	public static double distanceBetweenPoints(Position p1, Position p2)
	{
		return getDistance(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
	}
}
