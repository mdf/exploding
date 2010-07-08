package com.littlebighead.exploding;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import uk.ac.horizon.ug.AttributeSet;
import uk.ac.horizon.ug.Coordinate;
import uk.ac.horizon.ug.Field;
import uk.ac.horizon.ug.FieldConverter;
import uk.ac.horizon.ug.GameState;
import uk.ac.horizon.ug.IndexList;
import uk.ac.horizon.ug.Polygon;
import uk.ac.horizon.ug.TimeEvent;
import uk.ac.horizon.ug.Zone;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;

public class ZoneService extends Service {
	static double STARTLAT = 51.523436;
	static double STARTLNG = 0.040255;
		
	static ArrayList<Polygon> zones; 
	static ZoneUpdateListener ZONE_UPDATE_LISTENER;
	private Timer timer = new Timer();

	static int lat = (int) (STARTLAT * 1E6);
	static int lng = (int) (STARTLNG * 1E6);
	
	private static GameState gameState;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		 // init the service here
		new Thread(new Runnable(){
		 public void run(){
			_initService();
		 }
		}).start();
	}
	
	@Override
	public void onDestroy() {
		  super.onDestroy();

		  _shutdownService();

		 
	}
	
	public static GameState getGameState() {
		Log.i("ZoneService", "getGameState");
		return gameState;
	}

	
	public static void setUpdateListener(ZoneUpdateListener l) {
		  ZONE_UPDATE_LISTENER = l;
	}

	public static int getLat(){
		return lat;
	}
	
	public static int getLng(){
		return lng;
	}
	
	private void _runService(){
		Log.w("ZoneService", "_runService");
		 timer.scheduleAtFixedRate(
			      new TimerTask() {
			        public void run() {
			          _updateLocation();
			        }
			      },
			      0,
			      5000);

	
	}
	

	
	private void _updateLocation(){
		//lng += (int)(1E6 * 0.0008);
		if (ZONE_UPDATE_LISTENER != null){
			ZONE_UPDATE_LISTENER.updateCoords(lat, lng, getZone(lat, lng));
		}
	}
	
	private void _initService(){
		//GameState gameState = null;

			
		zones = new ArrayList<Polygon>();
		Resources resources = this.getResources();
		AssetManager assetManager = resources.getAssets();
		XStream xstream = new XStream(new DomDriver());
		
		xstream.alias("gameState", GameState.class);
		xstream.aliasAttribute(GameState.class, "version", "version");
		
		xstream.alias("timeEvent", TimeEvent.class);
		xstream.aliasAttribute(TimeEvent.class, "ref", "ref");

		xstream.alias("zone", Zone.class);
		xstream.aliasAttribute(Zone.class, "ref", "ref");

		xstream.alias("indexList", IndexList.class);
		xstream.aliasAttribute(IndexList.class, "ref", "ref");

		xstream.alias("attributeSet", AttributeSet.class);
		xstream.aliasAttribute(AttributeSet.class, "ref", "ref");
		xstream.addImplicitCollection(AttributeSet.class,"fields", "field", Field.class);

		xstream.addImplicitCollection(Zone.class,"fields", "field", Field.class);
		xstream.registerConverter(new FieldConverter());
		
		try
		{
			
//			InputStreamReader in = new InputStreamReader(assetManager.open("woolwichGameState.xml"));
			InputStreamReader in = new InputStreamReader(assetManager.open("gameState.xml"));

			Log.i("ZoneService", "setting gameState");

			gameState = (GameState) xstream.fromXML(in);

			for(Zone z : gameState.getZones())
			{
				System.err.println("ZONE: " + z.getName());
				
				for(Field f : z.getFields())
				{
					System.err.println("field name = "+ f.getName());
					
					System.err.println("field type = " + f.getType());
					
					if("attributes".equals(f.getName()))
					{
						System.err.println(f.getAttributeSet());
						System.err.println(f.getAttributeSet().getFlags());
					}
					else if("coords".equals(f.getName()))
					{
						System.err.println("polygon with " + f.getCoordinates().size() + " points");
						int[] xpoints = new int[f.getCoordinates().size()];
						int[] ypoints = new int[f.getCoordinates().size()];
						int i = 0;
						
						for(Coordinate c : f.getCoordinates())
						{
							xpoints[i] = (int) (c.getLongitude().doubleValue() * 1E6);
							ypoints[i] = (int) (c.getLatitude().doubleValue() * 1E6);
							i++;
							System.err.println("lat" + c.getLatitude() + " long " + c.getLongitude() + " elev " + c.getElevation());
						}
						zones.add(new Polygon(z.getName(), xpoints, ypoints, xpoints.length-1));
					}
				}
			}
			
			/*System.err.println("time events as follows:");
			
			for(TimeEvent t : gameState.getTimeEvents())
			{
				System.err.println(t.getStartTime());
			}*/
		}
		catch(IOException e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
		//_runService();
		
		
	}
	
	static public String getZone(int lat, int lng){
    	for(Polygon p : zones)
		{
    		if (p.contains(lng, lat)){
    			return p.getName();
    		}
		}
    	return "no zone"; 
    }
	
	private void _shutdownService() {
		  if (timer != null) timer.cancel();
		  Log.i(getClass().getSimpleName(), "Timer stopped!!!");
		}

}
