package com.littlebighead.exploding;

import java.util.ArrayList;
import java.util.List;

import uk.ac.horizon.ug.GameState;
import uk.ac.horizon.ug.TimeEvent;

import com.littlebighead.exploding.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;

import android.widget.TextView;
import android.widget.Toast;



import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;


public class GameMapView extends MapActivity implements ZoneUpdateListener, TimeEventUpdateListener {	//implements OnClickListener {
    private LocationManager lm;
    private LocationListener locationListener;
    
    private ArrayList playerMembers = new ArrayList();

	
	
//	LinearLayout linearLayout;
	MapView mapView;
	GeoPoint p;
	//ZoomControls mZoom;
	int lat;
	int lng;
	
	long gameStartTime;
	
	String lastzone = "";
	
	GameState gameState = null;
	
	MediaPlayer mMediaPlayer = null;
	
    class MapOverlay extends com.google.android.maps.Overlay {
        @Override
        public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
            super.draw(canvas, mv, shadow);                   
 
            
            //---translate the GeoPoint to screen pixels---
            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);
 
            
            //---add the marker---
 //           Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.image001);            
            //Double yadjust = Math.random()*100.0;
//            canvas.setMatrix(new Matrix());
//            canvas.drawBitmap(bmp, screenPts.x-64, screenPts.y-64, null);

            //canvas.drawCircle(screenPts.x,screenPts.y, 20, new Paint());
            
            return true;
        }
    } 

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        startService(new Intent(this, ZoneService.class));
        ZoneService.setUpdateListener(this); 
        lat = ZoneService.getLat();
        lng = ZoneService.getLng();
       

        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
       // linearLayout = (LinearLayout) findViewById(R.id.zoomview);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setReticleDrawMode(MapView.ReticleDrawMode.DRAW_RETICLE_OVER);
        
        //mapView.setStreetView(true);
        mapView.setBuiltInZoomControls(true);
        MyLocationOverlay overlay = new MyLocationOverlay(this, mapView);
        overlay.enableMyLocation();
       

        MapOverlay mapOverlay = new MapOverlay();
      
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);        
  
        
    	mapView.getController().setZoom(19); 
        p = new GeoPoint(lat, lng);
        mapView.getController().animateTo(p);    
        mapView.invalidate();
 
        
		Button button = (Button)findViewById(R.id.story);
		//button.setOnClickListener(this);
		button.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
				Intent myIntent = new Intent();
				myIntent.setClassName("com.littlebighead.exploding", "com.littlebighead.exploding.AddStoryView");
				startActivity(myIntent);
			}
		});
		
		button = (Button)findViewById(R.id.community);
//		button.setOnClickListener(this);
		button.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
				Intent myIntent = new Intent();
				myIntent.setClassName("com.littlebighead.exploding", "com.littlebighead.exploding.CommunityView");
				startActivity(myIntent);
			}
		});

		button = (Button)findViewById(R.id.create);
//		button.setOnClickListener(this);
		button.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
				Intent myIntent = new Intent();
				myIntent.setClassName("com.littlebighead.exploding", "com.littlebighead.exploding.CreateMemberView");
				startActivityForResult(myIntent,1);
			}
		});

		
		gameStartTime = System.currentTimeMillis();
		
        //---use the LocationManager class to obtain GPS locations---
        lm = (LocationManager) 
            getSystemService(Context.LOCATION_SERVICE);    
        
        locationListener = new MyLocationListener();
        
        lm.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 
            0, 
            0, 
            locationListener);   		
		
		
		new CountDownTimer(120*60*1000, 1000) {		
			public void onTick(long millisUntilFinished) {
				//if (gameState == null) {
					updateTimeEvent();			
				//}
			}
			
			public void onFinish() {
			}
		}.start();
		
 
    }
    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
      super.onActivityResult(requestCode, resultCode, data); 
      switch(requestCode) { 
        case (1) : { 
          if (resultCode == Activity.RESULT_OK) { 
//        	  ArrayList<Limb> limbs = (ArrayList<Limb>)data.getExtras().get("limbs");
        	  for (Limb limb: Body.limbs) {
        		  Log.i("limb position", Double.toString(limb.x));
        	  }
          } 
          break; 
        } 
      } 
    }
    
    private class MyLocationListener implements LocationListener 
    {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
            	/*
                Toast.makeText(getBaseContext(), 
                    "Location changed : Lat: " + loc.getLatitude() + 
                    " Lng: " + loc.getLongitude(), 
                    Toast.LENGTH_SHORT).show();
                    */
                	int latInt = (int)(loc.getLatitude() * 1E6);
                	int lngInt = (int)(loc.getLongitude() * 1E6);
                	updateCoords(latInt, lngInt, ZoneService.getZone(latInt, lngInt));
                
            }
        }

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
    }


   
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    

	


	public void updateCoords(final int lat, final int lng, final String zone) {
//		this.runOnUiThread(new Runnable() {
//            public void run() {
            	System.err.println("ZONE IS ..... " + zone);
            	if (! zone.equals("no zone") && !zone.equals(lastzone)){
//            		showToast(zone);
            		TextView zoneTextView = (TextView)findViewById(R.id.ZoneTextView);
            		zoneTextView.setText(zone);

				}
            	
				lastzone = zone;
            	p = new GeoPoint((int)lat,(int)lng);
                mapView.getController().animateTo(p);    
                mapView.invalidate();
            	
//            }
//		});
		
		
		if (gameState == null) {
	        gameState = ZoneService.getGameState();
		}
		
		
	     /*   
	        TimeEventService.setGameState(gameState);
	        startService(new Intent(this, TimeEventService.class));
	        TimeEventService.setUpdateListener(this); 
		}
		*/
		
		
	}
	
	public void updateTimeEvent() {	//TimeEvent te) {
		
		//gameState = ZoneService.getGameState();
		if (gameState == null) return; 
    	List<TimeEvent> timeEvents = gameState.getTimeEvents();
    	long gameTime = System.currentTimeMillis() - gameStartTime;
    	gameTime /= 1000;	//convert to seconds
    	gameTime *= 100;	//convert to game years
    	gameTime /= 30;		//convert to half minutes
		Log.w("GameMapView", "updateTimeEvent: gameTime="+gameTime);

		TextView yearTextView = (TextView)findViewById(R.id.YearTextView);
    	
    	for(TimeEvent te : timeEvents) {
    		if (te.played != false) continue;
    		if (te.getStartTime() < (int)gameTime && te.getEndTime() > (int)gameTime) {
    	    	System.err.println("here: "+te.getName());
    			if (te.track == 0) {
	    			yearTextView.setText(te.getName());
	    			
    			} else {
    				Intent myIntent = new Intent();
    				myIntent.setClassName("com.littlebighead.exploding", "com.littlebighead.exploding.TimeEventDialog");
    				myIntent.putExtra("year", yearTextView.getText());
    				myIntent.putExtra("name", te.getName());
    				myIntent.putExtra("desc", te.getDesc());
    				playAudio();
    				startActivity(myIntent);
    				
    				
//                    Toast.makeText(getBaseContext(), te.getName(), Toast.LENGTH_SHORT).show();
//            		showToast(te.getName());    				
    			}
   	    	
        		te.played = true;
//    			break;
    		}
		}
    	//System.err.println("no timeevent");            	
	}
    
	private void showToast(String zone){
		
		CharSequence text = "Entered zone "+ zone;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}
	
	@Override protected void onDestroy() {
		  super.onDestroy();

		  // stop MyService
		  {
		    Intent svc = new Intent(this, ZoneService.class);
		    stopService(svc);
		  }

	}
	
    private void playAudio () {
        try {
        	if (mMediaPlayer != null) {
	        	// http://www.soundjay.com/beep-sounds-1.html lots of free beeps here
	        	if (mMediaPlayer.isPlaying() == false) {
		            mMediaPlayer = MediaPlayer.create(this, R.raw.beep);
		            mMediaPlayer.setLooping(false);
		            mMediaPlayer.start();
	        	}
        	}
        } catch (Exception e) {
            Log.e("beep", "error: " + e.getMessage(), e);
        }
    }


}