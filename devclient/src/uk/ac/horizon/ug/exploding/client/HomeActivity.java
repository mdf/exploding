package uk.ac.horizon.ug.exploding.client;

import uk.ac.horizon.ug.exploding.client.AudioUtils.SoundAttributes;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
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
        BackgroundThread.addClientStateListener(this, this, ClientState.Part.STATUS.flag());
        
        //AudioUtils.addSoundFile(this, R.raw.buzzing, new SoundAttributes(1.0f, 1.0f, true, 1.0f));
        //AudioUtils.play(R.raw.buzzing);
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
		// FIXME - currently allowing "play" at any time
		menu.findItem(R.id.main_menu_play).setEnabled(true/*enablePlay*/);
		return super.onPrepareOptionsMenu(menu);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_play:
		{
			Intent intent = new Intent();
			intent.setClass(this, GameMapActivity.class);
			startActivity(intent);
			return true;
		}			
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
		case R.id.main_menu_gps:
		{
			Intent intent = new Intent();
			intent.setClass(this, GpsStatusActivity.class);
			startActivity(intent);
			return true;
		}			
		case R.id.main_menu_player_status:
		{
			Intent intent = new Intent();
			intent.setClass(this, PlayerStatusActivity.class);
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
			connectingPd.setCancelable(true);
			connectingPd.setMessage("Connecting...");
			connectingPd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					BackgroundThread.cancel(HomeActivity.this);
				}
			});
			return connectingPd;
		}
		if (id==DialogId.GETTING_STATE.ordinal()) {
			gettingStatePd = new ProgressDialog(this);
			gettingStatePd.setCancelable(true);
			gettingStatePd.setMessage("Getting Information...");
			gettingStatePd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					BackgroundThread.cancel(HomeActivity.this);
				}
			});
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
		//if (clientState.getClientStatus()!=ClientStatus.LOGGING_IN && clientState.getClientStatus()!=ClientStatus.GETTING_STATE)
		//Toast.makeText(this, "State: "+clientState.getClientStatus(), Toast.LENGTH_SHORT).show();
	
		if (clientState.isStatusChanged())
			updateDialogs(clientState);
	}

	/** update visible dialogs */
	private void updateDialogs(ClientState clientState) {
		switch (clientState.getClientStatus()) {
		case ERROR_GETTING_STATE:
		case ERROR_DOING_LOGIN:
		case ERROR_IN_SERVER_URL:
		case CANCELLED_BY_USER:
		case ERROR_AFTER_STATE:
			enableRetry = true;
			enablePlay = false;
			break;
		case POLLING:
		case IDLE:
		case PAUSED:
			enablePlay = true;
			enableRetry = false;
			break;
		default:
			enableRetry = false;
			enablePlay = false;
			break;
		}

		if (clientState.getClientStatus()==ClientStatus.LOGGING_IN) 
			showDialog(DialogId.CONNECTING.ordinal());
		else if (connectingPd!=null && connectingPd.isShowing())
			dismissDialog(DialogId.CONNECTING.ordinal());
		if (clientState.getClientStatus()==ClientStatus.GETTING_STATE) 
			showDialog(DialogId.GETTING_STATE.ordinal());
		else if (gettingStatePd!=null && gettingStatePd.isShowing())
			dismissDialog(DialogId.GETTING_STATE.ordinal());
		
		// update status
		TextView statusTextView = (TextView)findViewById(R.id.main_status_text_view);
		statusTextView.setText(clientState.getClientStatus().name());
		// update status
		TextView gameStatusTextView = (TextView)findViewById(R.id.main_game_status_text_view);
		gameStatusTextView.setText(clientState.getGameStatus().name());
		// update login status
		TextView loginStatusTextView = (TextView)findViewById(R.id.main_login_status_text_view);
		loginStatusTextView.setText(clientState.getLoginStatus().name());
		// update login message
		TextView loginMessageTextView = (TextView)findViewById(R.id.main_login_message_text_view);
		loginMessageTextView.setText(clientState.getLoginMessage());
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause()");
		AudioUtils.autoPause();
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
		boolean shutdownClient = preferences.getBoolean("shutdownClient", false);
		if (shutdownClient) {
			BackgroundThread.shutdown(this);
			preferences.edit().putBoolean("shutdownClient", false).commit();
		}
		boolean restartClient = preferences.getBoolean("restartClient", false);
		if (restartClient) {
			BackgroundThread.restart(this);
			preferences.edit().putBoolean("restartClient", false).commit();
		}
		urlTextView.setText(preferences.getString("serverUrl", "(not set)"));
		
		//AudioUtils.autoResume();
		// TEST
        //AudioUtils.play(R.raw.buzzing);
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