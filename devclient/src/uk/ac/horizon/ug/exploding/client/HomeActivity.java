package uk.ac.horizon.ug.exploding.client;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
        BackgroundThread.addClientStateListener(this, this);
    }
    /** create menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();    
    	inflater.inflate(R.menu.main_menu, menu);    
    	return true;
    }
    private boolean enableRetry = false;
    private boolean enablePlay = false;
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.main_menu_retry).setEnabled(enableRetry);
		menu.findItem(R.id.main_menu_play).setEnabled(enablePlay);
		return super.onPrepareOptionsMenu(menu);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_play:
			// TODO
			return true;
		case R.id.main_menu_retry:
			BackgroundThread.retry(this);
			return true;	
		case R.id.main_menu_preferences:
		{
			Intent intent = new Intent();
			intent.setClass(this, ExplodingPreferences.class);
			startActivity(intent);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);			
		}
	}
	private static enum DialogId {
		CONNECTING, GETTING_STATE
	}
	private ProgressDialog connectingPd;
	private ProgressDialog gettingStatePd;
    /* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id==DialogId.CONNECTING.ordinal()) {
			connectingPd = new ProgressDialog(this);
			connectingPd.setCancelable(false);
			connectingPd.setMessage("Connecting...");
			return connectingPd;
		}
		if (id==DialogId.GETTING_STATE.ordinal()) {
			gettingStatePd = new ProgressDialog(this);
			gettingStatePd.setCancelable(false);
			gettingStatePd.setMessage("Getting Information...");
			return gettingStatePd;
		}
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}
	// Need handler for callbacks to the UI thread    
	final Handler handler = new Handler();    
	// Create runnable for posting    

	/** NB this is not the GUI thread */
	@Override
	public void clientStateChanged(final ClientState clientState) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				guiClientStateChanged(clientState);
			}
		});
	}
	/** this is the GUI thread */
	public void guiClientStateChanged(ClientState clientState) {
		Log.d(TAG, "clientStateChanged: "+clientState);
		// TODO Auto-generated method stub
		if (clientState.getClientStatus()!=ClientStatus.LOGGING_IN && clientState.getClientStatus()!=ClientStatus.GETTING_STATE)
			Toast.makeText(this, "State: "+clientState.getClientStatus(), Toast.LENGTH_SHORT).show();
	
		switch (clientState.getClientStatus()) {
		case ERROR_GETTING_STATE:
		case ERROR_DOING_LOGIN:
		case ERROR_IN_SERVER_URL:
			enableRetry = true;
			break;
		default:
			enableRetry = false;
			break;
		}
		updateDialogs(clientState);
	}

	/** update visible dialogs */
	private void updateDialogs(ClientState clientState) {
		if (clientState.getClientStatus()==ClientStatus.LOGGING_IN) 
			showDialog(DialogId.CONNECTING.ordinal());
		else if (connectingPd!=null && connectingPd.isShowing())
			dismissDialog(DialogId.CONNECTING.ordinal());
		if (clientState.getClientStatus()==ClientStatus.GETTING_STATE) 
			showDialog(DialogId.GETTING_STATE.ordinal());
		else if (gettingStatePd!=null && gettingStatePd.isShowing())
			dismissDialog(DialogId.GETTING_STATE.ordinal());
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
		ClientState clientState = BackgroundThread.getClientState(this);
		updateDialogs(clientState);
		Log.d(TAG, "onResume(), clientState="+clientState);
		TextView urlTextView = (TextView)findViewById(R.id.main_server_url_text_view);
		// preferences edited by PreferencesActivity
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean restartClient = preferences.getBoolean("restartClient", false);
		if (restartClient) {
			BackgroundThread.restart(this);
			preferences.edit().putBoolean("restartClient", false).commit();
		}
		urlTextView.setText(preferences.getString("serverUrl", "(not set)"));
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