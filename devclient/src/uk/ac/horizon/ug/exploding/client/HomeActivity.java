package uk.ac.horizon.ug.exploding.client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/** activity launched from Home.
 * Note: should be singleTask in manifest, i.e. only one should ever exist as task root.
 * Also manages background thread shared by other Activities. 
 * 
 * @author cmg
 *
 */
public class HomeActivity extends Activity implements ClientStateListener {
	private static final String TAG = "HomeActivity";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        BackgroundThread.addClientStateListener(this);
    }

	@Override
	public void clientStateChanged(ClientState clientState) {
		Log.d(TAG, "clientStateChanged: "+clientState);
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause()");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d(TAG, "onRestart()");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ClientState clientState = BackgroundThread.getClientState();
		Log.d(TAG, "onResume(), clientState="+clientState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "onStart()");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "onStop()");
	}
    
}