/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

/**
 * @author cmg
 *
 */
public class GameMapActivity extends MapActivity /*implements LocationListener*/ {

	private static final String TAG = "Map";
	private static final int MILLION = 1000000;
	private static final int MIN_ZOOM_LEVEL = 14;
	private MyLocationOverlay myLocationOverlay;
	private MyOverlay itemOverlay;
	
	/** just a test for now */
	static class MyItem extends OverlayItem {

		public MyItem(GeoPoint point, String title, String snippet) {
			super(point, title, snippet);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Drawable getMarker(int stateBitset) {
			// TODO Auto-generated method stub
			//Log.d(TAG,"getmarker("+stateBitset+")="+this.mMarker);
			return this.mMarker;
			//return super.getMarker(stateBitset);
		}
		
	}
	/** just a test for now */
	static class MyOverlay extends ItemizedOverlay<MyItem> {
		private Drawable defaultMarker;
		
		public MyOverlay(Drawable defaultMarker) {
			super(defaultMarker);
			boundCenter(defaultMarker);
			//this.defaultMarker = defaultMarker;
			// TODO Auto-generated constructor stub
		}
		public void init() {
			populate();
		}
		@Override
		protected MyItem createItem(int i) {
			// TODO Auto-generated method stub
			//return null; //new MyItem();
			Log.d(TAG,"CreateItem("+i+"), drawable="+defaultMarker);
			MyItem item = new MyItem(new GeoPoint(52891937,-1157297), "Test"+i, null);
			//item.setMarker(defaultMarker);
			return item;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 1;
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			Log.d(TAG, "Try to load map view");
			setContentView(R.layout.map);
			MapView mapView = (MapView)findViewById(R.id.map_view);
			mapView.setBuiltInZoomControls(true);
			myLocationOverlay = new MyLocationOverlay(this, mapView);
			mapView.getOverlays().add(myLocationOverlay);
			myLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					centreOnMyLocation();
				}
			});
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.icon/*android.R.drawable.btn_star*/);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			Log.d(TAG,"defaultDrawable="+drawable);
			itemOverlay = new MyOverlay(drawable);
			itemOverlay.init();
			mapView.getOverlays().add(itemOverlay);
		}
		catch (Exception e) {
			Log.e(TAG, "Error loading map view: "+e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();    
    	inflater.inflate(R.menu.map_menu, menu);    
    	return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.map_my_location:
			centreOnMyLocation();
			return true;
		case R.id.map_menu_gps:
		{
			Intent intent = new Intent();
			intent.setClass(this, GpsStatusActivity.class);
			startActivity(intent);
			return true;
		}			
		}
		return super.onOptionsItemSelected(item);
	}

	private void centreOnMyLocation() {
		try {
			Location loc = LocationUtils.getCurrentLocation(this);
			if (loc!=null) {
				MapView mapView = (MapView)findViewById(R.id.map_view);
				MapController controller = mapView.getController();
				int zoomLevel = mapView.getZoomLevel();
				// zoom Level 15 is about 1000m on a side
				if (zoomLevel < MIN_ZOOM_LEVEL)
					controller.setZoom(MIN_ZOOM_LEVEL);
				GeoPoint point = new GeoPoint((int)(loc.getLatitude()*MILLION), (int)(loc.getLongitude()*MILLION));
				controller.animateTo(point);
			}
			else
			{
				Toast.makeText(this, "Current location unknown", Toast.LENGTH_SHORT).show();
			}
		}catch (Exception e) {
			Log.e(TAG, "doing centreOnMyLocation", e);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		//LocationUtils.unregisterOnThread(this, this, null);
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();
//		LocationUtils.registerOnThread(this, this, null);
//		centreOnMyLocation();
	}

	
//	@Override
//	public void onLocationChanged(Location location) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		// TODO Auto-generated method stub
//		
//	}

}
