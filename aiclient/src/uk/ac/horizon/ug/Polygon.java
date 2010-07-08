package uk.ac.horizon.ug;

/**

 * Minimum Polygon class for Android.
 * see: http://www.anddev.org/using_javaawtpolygon_in_android-t6521.html
 */
 
public class Polygon {
 
 
 
    // Polygon coordinates.
 
    private int[] polyY, polyX;
 
 
 
    // Number of sides in the polygon.
 
    private int polySides;
 
    private String name;
 
    /**
 
     * Default constructor.
 
     * @param px Polygon y coods.
 
     * @param py Polygon x coods.
 
     * @param ps Polygon sides count.
 
     */
 
    public Polygon(String name, int[] px, int[] py, int ps ) {
 
    	this.name = name;
 
        polyX = px;
 
        polyY = py;
 
        polySides = ps;
 
    }
 
 
 
    public String getName(){
    	return name;
    }
    /**
 
     * Checks if the Polygon contains a point.
 
     * @see "http://alienryderflex.com/polygon/"
 
     * @param x Point horizontal pos.
 
     * @param y Point vertical pos.
 
     * @return Point is in Poly flag.
 
     */
 
    public boolean contains( int x, int y ) {
 
 
 
        boolean oddTransitions = false;
 
        for( int i = 0, j = polySides -1; i < polySides; j = i++ ) {
 
            if( ( polyY[ i ] < y && polyY[ j ] >= y ) || ( polyY[ j ] < y && polyY[ i ] >= y ) ) {
 
                if( polyX[ i ] + ( y - polyY[ i ] ) / ( polyY[ j ] - polyY[ i ] ) * ( polyX[ j ] - polyX[ i ] ) < x ) {
 
                    oddTransitions = !oddTransitions;          
 
                }
 
            }
 
        }
 
        return oddTransitions;
 
    }  
 
}