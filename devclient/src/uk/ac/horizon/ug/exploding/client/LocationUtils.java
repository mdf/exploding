/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * @author cmg
 *
 */
public class LocationUtils {
	private static String TAG = "LocationUtils";
	private static boolean locationRequired = false;
	private static LocationThread locationThread;
	public static synchronized void updateRequired(Context context, boolean req) {
		Log.d(TAG,"updateRequired("+req+")");
		if (locationCallback==null) {
			locationCallback = new LocationCallback(context);			
		}
		if (locationThread==null) {
			locationThread = new LocationThread(context);
			locationThread.start();
		}
		locationRequired = req;
		locationThread.check();
	}
	static class LocationThread extends Thread {
		private Context context;
		private Handler handler;
		private boolean locating = false;
		LocationThread(Context context) {
			this.context = context;			
		}
		public void run() {
			Looper.prepare();
			handler = new Handler();
			Looper.loop();
			Log.i(TAG,"LocationThread terminated");
		}
		private void waitForHandler() {
			try {
				while (handler==null) {
					sleep(10);
				}
			} catch (InterruptedException ie) { /*ignore?*/ }
		}
		void check() {
			waitForHandler();
			handler.post(new Runnable() {
				public void run() {
					boolean required = locationRequired;
					Log.d(TAG,"Checking in thread ("+locationRequired+" vs "+locating+")");
					if (required && !locating) 
						registerOnThread();
					else if (!required && locating)
						unregisterOnThread();
					locating = required;
				}
			});
		}
		private void registerOnThread() {
			LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			List<String> providers = locationManager.getAllProviders();
			Log.i(TAG,"Found "+providers.size()+" location providers");
			for (String provider : providers) {
				if (locationManager.isProviderEnabled(provider)) {
					Log.i(TAG,"Provider "+provider+" enabled");
				}
				else {
					Log.i(TAG,"Provider "+provider+" disabled");	
				}
				Location loc = locationManager.getLastKnownLocation(provider);
				if (loc!=null)
					Log.i(TAG,"Last Location, provider="+loc.getProvider()+", lat="+loc.getLatitude()+", long="+loc.getLongitude()+", bearing="+(loc.hasBearing() ? ""+loc.getBearing() : "NA")+", speed="+(loc.hasSpeed() ? ""+loc.getSpeed() : "NA")+", accuracy="+(loc.hasAccuracy() ? ""+loc.getAccuracy() : "NA")+", alt="+(loc.hasAltitude() ? ""+loc.getAltitude() : "NA"));
				if (!"passive".equals(provider))
					locationManager.requestLocationUpdates(provider, 0/*minTime*/, 0/*minDistance*/, locationCallback);
			}
			locationManager.addGpsStatusListener(locationCallback);
		}
		private void unregisterOnThread() {
			Log.i(TAG,"Unregister for location events");
			LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(locationCallback);
			locationManager.removeGpsStatusListener(locationCallback);
		}
	}
	private static LocationCallback locationCallback;
	static class LocationCallback implements LocationListener, Listener {
		private Context context;
		LocationCallback(Context context) {
			this.context = context;
		}
		@Override
		public void onLocationChanged(Location loc) {
			Log.i(TAG,"Location provider="+loc.getProvider()+", lat="+loc.getLatitude()+", long="+loc.getLongitude()+", bearing="+(loc.hasBearing() ? ""+loc.getBearing() : "NA")+", speed="+(loc.hasSpeed() ? ""+loc.getSpeed() : "NA")+", accuracy="+(loc.hasAccuracy() ? ""+loc.getAccuracy() : "NA")+", alt="+(loc.hasAltitude() ? ""+loc.getAltitude() : "NA"));
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i(TAG,"Provider "+provider+" disabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i(TAG, "Provider "+provider+" enabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(TAG, "Provider "+provider+" status now "+status+" ("+(status==LocationProvider.AVAILABLE ? "AVAILABLE" : status==LocationProvider.OUT_OF_SERVICE ? "OUT_OF_SERVICE" : status==LocationProvider.TEMPORARILY_UNAVAILABLE ? "TEMPORARILY_UNAVAILABLE" : "unknown")+", extras="+extras);
		}

		@Override
		public void onGpsStatusChanged(int event) {
			LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			GpsStatus gpsStatus = locationManager.getGpsStatus(null);
			// TODO Auto-generated method stub
			Log.i(TAG,"GpsStatus "+event+" ("+(event==GpsStatus.GPS_EVENT_FIRST_FIX ? "First fix" : event==GpsStatus.GPS_EVENT_SATELLITE_STATUS ? "Satelite status" : event==GpsStatus.GPS_EVENT_STARTED ? "started" : event==GpsStatus.GPS_EVENT_STOPPED ? "stopped" : "unknown"));
			Log.i(TAG,"GpsStatus timeToFirstFix="+gpsStatus.getTimeToFirstFix());
			Iterator<GpsSatellite> sats = gpsStatus.getSatellites().iterator();
			while(sats.hasNext()) {
				GpsSatellite sat = sats.next();
				Log.i(TAG,"GpsSatellite snr="+sat.getSnr()+", used="+sat.usedInFix()+", azimuth="+sat.getAzimuth()+", elevation="+sat.getElevation()+", almanac="+sat.hasAlmanac()+", ephemeris="+sat.hasEphemeris()+", prn="+sat.getPrn());
			}
		}
	
	}
}
