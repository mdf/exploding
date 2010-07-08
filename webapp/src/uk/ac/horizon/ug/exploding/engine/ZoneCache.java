package uk.ac.horizon.ug.exploding.engine;

import uk.ac.horizon.ug.exploding.db.Position;
import uk.ac.horizon.ug.exploding.db.Zone;

public class ZoneCache
{
	Zone zone;
	 
    double[] polyX, polyY;
    
    double radius;
 
    int polySides;
    
    boolean polygon;
	
	public ZoneCache(Zone zone)
	{
		this.zone = zone;
		
		if(zone.getPolygon()==1)
		{
			this.polygon = true;
			polyX = new double[zone.getCoordinates().length];
			polyY = new double[zone.getCoordinates().length];
			polySides = zone.getCoordinates().length;
			
			for(int i=0; i<zone.getCoordinates().length; i++)
			{
				Position p = zone.getCoordinates()[i];
				polyX[i] = p.getLatitude();
				polyY[i] = p.getLongitude();
			}
		}
		else
		{
			this.polygon = false;
			this.radius = zone.getRadius();
		}
	}
	

	public boolean contains(double x, double y)
	{
    	 if(this.polygon == false)
    	 {
    		 // FIXME zone xml doesn't contain central point?!
    		 return false;
    	 }
    	 else
    	 {
    		 boolean oddTransitions = false;

    		 for(int i = 0, j = polySides -1; i < polySides; j = i++ )
    		 {
    			 if( ( polyY[ i ] < y && polyY[ j ] >= y ) || ( polyY[ j ] < y && polyY[ i ] >= y ) )
    			 {
    				 if( polyX[ i ] + ( y - polyY[ i ] ) / ( polyY[ j ] - polyY[ i ] ) * ( polyX[ j ] - polyX[ i ] ) < x )
    				 {
    					 oddTransitions = !oddTransitions;          
    				 }
    			 }
    		 }

    		 return oddTransitions;
    	 }	
	}
	
	public static double distanceBetweenPoints(Position p1, Position p2)
	{
		// FIXME - calculate in metres, not degrees!
		// sodding non-cartesian coordinates...
		
		double dx = p1.getLongitude() - p2.getLongitude();
		double dy = p1.getLatitude() - p2.getLatitude();
		return Math.sqrt(dx*dx + dy*dy);
	}
}
