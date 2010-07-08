package com.littlebighead.exploding;


import android.app.Activity;
import android.content.Intent;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.os.Bundle;
//import android.os.CountDownTimer;

//import android.content.Intent;

import android.view.View.OnClickListener;



public class CommunityMemberPropsView extends Activity {	//implements OnClickListener {
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	setContentView(R.layout.com_attrib_dialogue);  
    	

    }
    

}

