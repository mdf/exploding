package com.littlebighead.exploding;


import android.app.Activity;
import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;
//import android.os.CountDownTimer;

//import android.content.Intent;

import android.view.View.OnClickListener;





public class TimeEventDialog extends Activity {	//implements OnClickListener {
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	setContentView(R.layout.event_dialogue);  
    	
		Button button = (Button)findViewById(R.id.dismiss_button);
		//button.setOnClickListener(this);
		button.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	finish();
		    }
		});
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Log.i("EXTRAS", "Found extras");
			TextView tv = (TextView)findViewById(R.id.year);
			tv.setText(extras.getString("year"));
//			String value = extras.getString("keyName");
			tv = (TextView)findViewById(R.id.name);
			tv.setText(extras.getString("name"));
			tv = (TextView)findViewById(R.id.description);
			tv.setText(extras.getString("desc"));
			
		}		

    }
    

}

