package com.littlebighead.exploding;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.ac.horizon.ug.AttributeSet;
import uk.ac.horizon.ug.Coordinate;
import uk.ac.horizon.ug.Field;
import uk.ac.horizon.ug.FieldConverter;
import uk.ac.horizon.ug.GameState;
import uk.ac.horizon.ug.IndexList;
import uk.ac.horizon.ug.TimeEvent;
import uk.ac.horizon.ug.Zone;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import android.app.Activity;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import android.os.Bundle;
import android.os.CountDownTimer;

import android.content.Intent;
import android.content.res.AssetManager;


public class ExplodingPlaces extends Activity {
	
//	Thread myRefreshThread = null;
//	int secondsCount = 0;
	GameMapView gameMapView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	setContentView(R.layout.start);
 
    	new CountDownTimer(3000, 1000) {

    	     public void onTick(long millisUntilFinished) {
    	     }

    	     public void onFinish() {
 //   	    	 setContentView(R.layout.main);
//    	    	 gameMapView = new GameMapView();
    	    	 
    	    	 Intent myIntent = new Intent();	//"com.littlebighead.exploding.GameMapView");
    	    	 myIntent.setClassName("com.littlebighead.exploding", "com.littlebighead.exploding.GameMapView");
    	    	 startActivity(myIntent);  
    	    	 finish();
    	     }
    	}.start();
    	  

 
    	  
//    	  test();
    }


    
}

