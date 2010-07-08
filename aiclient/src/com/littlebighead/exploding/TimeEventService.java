package com.littlebighead.exploding;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
//import java.lang.System;

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

public class TimeEventService extends Service {
	ArrayList<TimeEvent> timeEvents; 
	private Timer timer = new Timer();
	
	private static GameState gameState;
	
	static TimeEventUpdateListener TIMEEVENT_UPDATE_LISTENER;

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		  super.onDestroy();

		  _shutdownService();
	}
	
	private void _runService(){
		Log.w("TimeEventService", "_runService");
		timer.scheduleAtFixedRate(
				new TimerTask() {
					public void run() {
						_updateTimeEvent();
						Log.w("TimeEventService", "run()");
						
					}
				},
				0,
				1000);
	}
	
	private void _updateTimeEvent() {
		Log.w("TimeEventService", "_updateTimeEvent");
		if (TIMEEVENT_UPDATE_LISTENER != null){
			TIMEEVENT_UPDATE_LISTENER.updateTimeEvent();	//getTimeEvent(System.currentTimeMillis()));	//new Date().getTime()));
		} else {
			System.err.println("TimeEventService: No listener");
		}
		
	}
	
	/*
	private TimeEvent getTimeEvent(long now) {
		Log.w("TimeEventService", "getTimeEvent");
    	for(TimeEvent te : timeEvents) {
    		if (te.getStartTime() < now && te.getEndTime() > now){
    			return te;
    		}
		}
		Log.w("TimeEventService", "getTimeEvent: return null");
    	return null; 
    }	
    */
	
	public static void setGameState(GameState gs) {
		gameState = gs;
	}
	
	@Override
	public void onCreate() {
		Log.w("TimeEventService", "onCreate");
		// TODO Auto-generated method stub
		super.onCreate();
		
		 // init the service here
		new Thread(new Runnable(){
		 public void run(){
			_initService();
		 }
		}).start();
	}

	public static void setUpdateListener(TimeEventUpdateListener l) {
		  TIMEEVENT_UPDATE_LISTENER = l;
	}
	
	private void _initService(){
		_runService();
	}

	
	

	
	private void _shutdownService() {
		  if (timer != null) timer.cancel();
		  Log.i(getClass().getSimpleName(), "Timer stopped!!!");
		}

}
