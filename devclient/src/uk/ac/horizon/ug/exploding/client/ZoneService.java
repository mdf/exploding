/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import uk.ac.horizon.ug.exploding.client.model.Position;
import uk.ac.horizon.ug.exploding.client.model.Zone;

/** Not really a service, but implements the getZone functionality from
 * the original ZoneService against the current cached state.
 * 
 * @author cmg
 *
 */
public class ZoneService {
	
	private static final String TAG = "ZoneService";
	/** called by Location Utils on position update */
	static public void updateLocation(Context context, Location loc) {
		if (loc==null)
			return;
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();
		String zone = getZone(context, latitude, longitude);
		Log.d(TAG, "UpdateLocation to "+latitude+","+longitude+", zone="+zone);
	}
	
	static public String getZone(Context context, double latitude, double longitude){
		ClientState clientState = BackgroundThread.getClientState(context);
		List<Object> zones = clientState.getCache().getFacts(Zone.class.getName());
    	for(Object z : zones)
		{
    		Zone zone = (Zone)z;
    		Position ps [] = zone.getCoordinates();
    		if (polygonContains(ps, latitude, longitude)){
    			return zone.getName();
    		}
		}
    	return null; 
    }
	/**
     * Checks if the Polygon contains a point.
     * @see "http://alienryderflex.com/polygon/"
     * @param x Point horizontal pos.
     * @param y Point vertical pos.
     * @return Point is in Poly flag.
     */
 
	static public boolean polygonContains(Position ps[], double latitude, double longitude) {

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
}
