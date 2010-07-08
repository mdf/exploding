package com.littlebighead.exploding;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import android.os.Bundle;
//import android.os.CountDownTimer;

//import android.content.Intent;

import android.view.View.OnClickListener;



public class CommunityView extends Activity {	//implements OnClickListener {
	public Dialog dialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	setContentView(R.layout.community);  
    	
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));



        /*
        Context mContext = getApplicationContext();
        dialog = new Dialog(mContext);

        dialog.setContentView(R.layout.com_attrib_dialogue);
        dialog.setTitle("Custom Dialog"); 
        */ 
        
        /*
		Button button = (Button)dialog.findViewById(R.id.dismiss_member_props);
		//button.setOnClickListener(this);
		button.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	Log.i("click","clicked");
		    	finish();
//		    	finish();
		    }
		});       
    	*/


		/*
		button = (Button)findViewById(R.id.cancel_button);
		//button.setOnClickListener(this);
		button.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	finish();
		    }
		});
		*/

    }
    
    private class OnReadyListener implements CommunityPropsDialog.ReadyListener {
        @Override
        public void ready(String name) {
            Toast.makeText(CommunityView.this, name, Toast.LENGTH_LONG).show();
        }
    }
    
    /*
    protected Dialog onPrepareDialog(int id) {
        Dialog dialog = null;
        switch(id) {
        case R.layout.com_attrib_dialogue:
            // do the work to define the pause Dialog
            break;
//        case DIALOG_GAMEOVER_ID:
            // do the work to define the game over Dialog
//            break;
        default:
            dialog = null;
        }
        return dialog;
    }
    
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch(id) {
        case R.layout.com_attrib_dialogue:
            // do the work to define the pause Dialog
        	//dialog = 
            break;
//        case DIALOG_GAMEOVER_ID:
            // do the work to define the game over Dialog
//            break;
        default:
            dialog = null;
        }
        return dialog;
    }
    */
    


    public class ImageAdapter extends BaseAdapter {	//implements onClickListener {
        private Context mContext;
        
        //private Dialog dialog;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
 //               imageView.setOnClickListener(this);
                /*
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                      Log.d("onClick","position ["+position+"]");
                    	Log.d("onClick","position ["+position+"]");
                    	//showDialog(R.layout.com_attrib_dialogue);
                    	 CommunityView cv = (CommunityView) mContext;
                    	 cv.dialog.show();
                    }

                 });
                 */
                
        		imageView.setOnClickListener(new OnClickListener() {
        		    public void onClick(View v) {
        		        
        		        CommunityPropsDialog myDialog = new CommunityPropsDialog(mContext, "", new OnReadyListener());
        		        myDialog.show();
        		        
        		        /*
        				Intent myIntent = new Intent();
        				myIntent.setClassName("com.littlebighead.exploding", "com.littlebighead.exploding.CommunityMemberPropsView");
        				startActivity(myIntent);
        				*/
        				
        			}
        		});                
                
            } else {
            	imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }
        
        public void onClick(View view) {
        	((Button) view).setText("*");
        	
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001,
                R.drawable.image001, R.drawable.image001
        };
    }

}

