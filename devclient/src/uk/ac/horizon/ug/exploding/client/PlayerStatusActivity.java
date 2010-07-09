/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.util.List;

import uk.ac.horizon.ug.exploding.client.model.Player;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * @author cmg
 *
 */
public class PlayerStatusActivity extends Activity implements ClientStateListener {

	private static final String TAG = "PlayerStatus";

	private static Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_status);
		handler = new Handler();
		BackgroundThread.addClientStateListener(this, this, Player.class.getName());
		// initialise
		clientStateChanged(BackgroundThread.getClientState(this));
	}

	@Override
	public void clientStateChanged(final ClientState clientState) {
		handler.post(new Runnable() {
			public void run() {
				clientStateChangedInThread(clientState);
			}
		});
	}
	private void clientStateChangedInThread(final ClientState clientState) {
		Player player = null;
		if (clientState!=null) {
		// get players...
			List<Object> facts = clientState.getCache().getFacts(Player.class.getName());
			Log.d(TAG,"Found "+facts.size()+" Player objects in cache");
			if (facts.size()>0)
				player = (Player)facts.get(0);
		}
		TextView tv;
		tv = (TextView)findViewById(R.id.player_status_can_author_text_view);
		tv.setText(player==null ? "-" : ""+player.getCanAuthor());
		tv = (TextView)findViewById(R.id.player_status_id_text_view);
		tv.setText(player==null ? "-" : ""+player.getID());
		tv = (TextView)findViewById(R.id.player_status_name_text_view);
		tv.setText(player==null ? "-" : ""+player.getName());
		tv = (TextView)findViewById(R.id.player_status_new_member_quota_text_view);
		tv.setText(player==null ? "-" : ""+player.getNewMemberQuota());
		tv = (TextView)findViewById(R.id.player_status_points_text_view);
		tv.setText(player==null ? "-" : ""+player.getPoints());
	}
	
}
