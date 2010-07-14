package com.littlebighead.exploding;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import android.os.Bundle;
//import android.os.CountDownTimer;

//import android.content.Intent;

import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;



public class CreateMemberView extends Activity {	//implements OnClickListener {
    DrawView drawView;	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
//    	setContentView(R.layout.create_member);  

		//View view = (View)findViewById(R.id.View01);
		
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        
        TextView title = new TextView(this);
        title.setText("Create member");
        mainLayout.addView(title);

        drawView = new DrawView(this);
        drawView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 600));
        mainLayout.addView(drawView);
        drawView.requestFocus();
        
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        mainLayout.addView(buttonLayout);
        
        for (int f=0; f<5; f++) {
        	Button button = new Button(this);
        	button.setText("H");
        	button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1));
        	switch(f) {
        		case 0:
        			button.setBackgroundColor(0xff00aeef);
        			break;
        		case 1:
        			button.setBackgroundColor(0xffFFCC00);
        			break;
        		case 2:
        			button.setBackgroundColor(0xffFF6633);
        			break;
        		case 3:
        			button.setBackgroundColor(0xff999933);
        			break;
        		default:
            		button.setText("Done");
//                	button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,2));
            		button.setOnClickListener(new OnClickListener() {
            		    public void onClick(View v) {
            		    	Intent resultIntent = new Intent();

//            		    	Bundle extras = new Bundle();
//            		    	extras.putSerializable("limbs", drawView.body.limbs);
//            		    	resultIntent.putExtras(extras);
            		    	          		    	
//            		    	resultIntent.putExtra("limbs", drawView.body.limbs);
            		    	setResult(Activity.RESULT_OK, resultIntent);            		    	
            		    	finish();
            		    }
            		});
        	}
        	
        	buttonLayout.addView(button);
        	
        }
        
        
        setContentView(mainLayout);
/*        
        
        Button button = new Button(this);
        button.setText("Done");
        button.setWidth(100);
        button.setHeight(100);
        button.x = 100;
        */
        
        /*
		Button button = (Button)findViewById(R.id.done_button);
		//button.setOnClickListener(this);
		button.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	finish();
		    }
		});
		*/


    }
    


}

