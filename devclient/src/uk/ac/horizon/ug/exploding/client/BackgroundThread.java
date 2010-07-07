/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/** Background thread to handle communication with the server.
 * A full service is not required as it is all within a single application.
 * 
 * 
 * @author cmg
 *
 */
public class BackgroundThread implements Runnable {
	public static final String TAG = "ExplodingPlacesBackgroundThread";
	private static final long THREAD_SLEEP_MS = 1000;
	// in preferences.xml
	private static final String DEFAULT_PLAYER = "defaultPlayerName";
	public static final int CLIENT_VERSION = 1;
	private static final String CLIENT_TYPE = "AndroidDevclient";
	private static final String LOGIN_PATH = "login";
	private static final String MESSAGES_PATH = "messages";
	/** cons - private */
	private BackgroundThread() {
		super();
	}
	/** singleton */
	private static Thread singleton;
	/** run method */
	@Override
	public void run() {
		mainloop:
		while (Thread.currentThread()==singleton) {
			try {
				boolean doLogin = false;
				ClientState clientStateEvent = null;
				synchronized (BackgroundThread.class) {
					// Synchronized!
					//Log.d(TAG, "Background action on state "+currentClientState);
					switch(currentClientState.getClientStatus()) {
					case CANCELLED_BY_USER:
					case ERROR_DOING_LOGIN:
					case ERROR_GETTING_STATE:
					case ERROR_IN_SERVER_URL:
						Log.i(TAG, "Background thread give up on state "+currentClientState.getClientStatus());
						break mainloop;
					case NEW:
						// TODO log in
						currentClientState.setClientStatus(ClientStatus.LOGGING_IN);
						clientStateEvent = currentClientState.clone();
						doLogin = true;
						break;
					}
					// End Synchronized!
				}
				// unsync
				if (clientStateEvent!=null)
					fireClientStateChanged(clientStateEvent);
				if (doLogin) 
					doLogin();
				Thread.sleep(THREAD_SLEEP_MS);
			}
			catch (Exception e) {
				Log.e(TAG, "Exception in background thread "+Thread.currentThread(), e);
				// TODO ERROR?
			}
		}
		Log.i(TAG, "Background thread "+Thread.currentThread()+" exiting (interrupted="+Thread.interrupted()+")");
	}
	/** HTTP client */
	private HttpClient httpClient;
	/** get HTTP Client */
	private synchronized HttpClient getHttpClient() {
		if (httpClient!=null)
			return httpClient;
		httpClient = new DefaultHttpClient();
		return httpClient;
	}
	/** conversation */
	private String conversationId;
	//private static String server
	/** attempt login - called from background thread, unsync. */
	private void doLogin() {
		// TODO Auto-generated method stub
		if (contextRef==null)
		{
			Log.e(TAG,"doLogin: contextRef==null");
			setClientStatus(ClientStatus.ERROR_IN_SERVER_URL);
			return;
		}
		Context context = contextRef.get();
		if (context==null) {
			Log.e(TAG,"doLogin: context==null");
			setClientStatus(ClientStatus.ERROR_IN_SERVER_URL);
			return;			
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String serverUrl = preferences.getString("serverUrl", null);
		if (serverUrl==null || serverUrl.length()==0) {
			Log.e(TAG,"doLogin: serverUrl==null");
			setClientStatus(ClientStatus.ERROR_IN_SERVER_URL, "The Server URL is not set\n(See Preferences)");
			return;
		}
        // get device unique ID(s)
		String clientId = ExplodingPreferences.getDeviceId(context);

        conversationId = GUIDFactory.newGUID(clientId);
        
        HttpClient httpClient = getHttpClient();
		HttpPost request = null;
		try {
			serverUrl = serverUrl+LOGIN_PATH;
			request = new HttpPost(new URI(serverUrl));
		} catch (Exception e) {
			Log.e(TAG, "parsing serverUrl "+serverUrl, e);
			setClientStatus(ClientStatus.ERROR_IN_SERVER_URL, "There is a problem with the Server URL\n("+e.getMessage()+")");
			return;
		}
		try {
			LoginMessage login = new LoginMessage();
			login.setClientId(clientId);
			if (preferences.contains(DEFAULT_PLAYER) && preferences.getString(DEFAULT_PLAYER, "").length()>0)
				login.setPlayerName(preferences.getString(DEFAULT_PLAYER, null));
			login.setConversationId(conversationId);
			login.setClientVersion(CLIENT_VERSION);
			login.setClientType(CLIENT_TYPE);
			// TODO XPP3 driver?
			XStream xs = new XStream(new DomDriver());
			xs.alias("login", LoginMessage.class);
			xs.alias("reply", LoginReplyMessage.class);
			String xmlText = xs.toXML(login);
			// name?
			Log.d(TAG,"Login: "+xmlText);
			//request.setHeader("Content-Type", )
			request.setEntity(new StringEntity(xmlText));
			HttpResponse response = httpClient.execute(request);
			StatusLine statusLine = response.getStatusLine();
			Log.d(TAG, "Http status on login: "+statusLine);
			if (statusLine.getStatusCode()!=200) {
				Log.e(TAG, "Error - Http status on login: "+statusLine);
				setClientStatus(ClientStatus.ERROR_DOING_LOGIN, "Error logging in!\n("+statusLine.getReasonPhrase()+")");				
				return;
			}
			LoginReplyMessage reply = (LoginReplyMessage )xs.fromXML(response.getEntity().getContent());
			Log.d(TAG,"Reply: "+reply);
			synchronized (BackgroundThread.class) {
				checkCurrentThread();

				currentClientState.setGameStatus(reply.getGameStatus());
				currentClientState.setLoginStatus(reply.getStatus());
				currentClientState.setLoginMessage(reply.getMessage());
				//fireClientStateChanged(currentClientState.clone());

				if (reply.getStatus()==LoginReplyMessage.Status.OK && reply.getGameStatus()==GameStatus.ACTIVE) {
					currentClientState.setClientStatus(ClientStatus.GETTING_STATE);
				} else {
					currentClientState.setClientStatus(ClientStatus.ERROR_DOING_LOGIN);
				}
				fireClientStateChanged(currentClientState.clone());
				
			}
		} catch (Exception e) {
			Log.e(TAG, "Attempting post to serverUrl "+serverUrl, e);
			setClientStatus(ClientStatus.ERROR_DOING_LOGIN, "Error logging in!\n("+e.getMessage()+")");
			return;
		}
	}
	/** listeners */
	private static LinkedList<WeakReference<ClientStateListener>> listeners = new LinkedList<WeakReference<ClientStateListener>>();
	/** add listener */
	public static void addClientStateListener(ClientStateListener listener, Context context) {
		checkThread(context);
		listeners.add(new WeakReference<ClientStateListener>(listener));
	}
	/** add listener */
	public static void removeClientStateListener(ClientStateListener listener) {
		for (int i=0; i<listeners.size(); i++) {
			if (listeners.get(i).get()==listener) {
				listeners.remove(i);
				i--;
			}
		}
	}
	/** check we are current background thread */
	private static synchronized void checkCurrentThread() {
		if (singleton!=Thread.currentThread()) {
			Log.e(TAG, "setClientStatus called by thread non-current thread");
			throw new RuntimeException("checkCurrentThread called by thread non-current thread");
		} else if (currentClientState!=null && currentClientState.getClientStatus()==ClientStatus.CANCELLED_BY_USER) {
			Log.e(TAG, "setClientStatus called by thread when Cancelled by user");
			throw new RuntimeException("checkCurrentThread called by cancelled thread");
		}
	}
	/** set client status and fire */
	private static synchronized void setClientStatus(ClientStatus clientStatus) {
		setClientStatus(clientStatus, null);
	}
	/** set client status and fire */
	private static synchronized void setClientStatus(ClientStatus clientStatus, String message) {
		checkCurrentThread();
		if (currentClientState!=null && (currentClientState.getClientStatus()!=clientStatus || message!=currentClientState.getLoginMessage())) {
			currentClientState.setLoginMessage(message);
			currentClientState.setClientStatus(clientStatus);
			fireClientStateChanged(currentClientState.clone());
		}
	}
	/** set game status and fire */
	private static synchronized void setGameStatus(GameStatus gameStatus) {
		if (singleton!=Thread.currentThread()) {
			Log.e(TAG, "setGameStatus called by thread non-current thread");
			throw new RuntimeException("setGameStatus called by thread non-current thread");
		}
		if (currentClientState!=null && currentClientState.getGameStatus()!=gameStatus) {
			currentClientState.setGameStatus(gameStatus);
			fireClientStateChanged(currentClientState.clone());
		}
	}
	/** fire event listeners */
	private static void fireClientStateChanged(ClientState clientState) {
		for (WeakReference<ClientStateListener> listenerRef : listeners) {
			ClientStateListener listener = listenerRef.get();
			if (listener!=null) {
				try {
					// TODO GUI thread?
					listener.clientStateChanged(clientState);
				}
				catch (Exception e) {
					Log.e(TAG, "Error calling listener "+listener, e);
				}
			}
		}
	}
	/** client state */
	private static ClientState currentClientState;
	/** context */
	private static WeakReference<Context> contextRef;
	/** check */
	private static synchronized void checkThread(Context context) {
		if (currentClientState==null)
			currentClientState = new ClientState(ClientStatus.NEW, GameStatus.UNKNOWN);
		if (singleton==null || !singleton.isAlive()) {
			Log.i(TAG, (singleton!=null ? "(Re)": "")+"starting background thread");
			singleton = new Thread(new BackgroundThread());
			singleton.start();			
		}
		if (contextRef==null || contextRef.get()==null) {
			contextRef = new WeakReference<Context>(context);
//			PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(new SharedPreferenceChangeListener());
		}
		// TODO
	}
	/** get state (copy) */
	public static synchronized ClientState getClientState(Context context) {
		checkThread(context);
		return currentClientState.clone();
	}
	/** restart client */
	public static void restart(Context context) {
		Log.i(TAG, "Restart client - explicit request");
		currentClientState = new ClientState(ClientStatus.NEW, GameStatus.UNKNOWN);
		if (singleton!=null && singleton.isAlive()) 
			singleton.interrupt();
		singleton = new Thread(new BackgroundThread());
		singleton.start();			
	}
	/** retry client */
	public static void retry(Context context) {
		checkThread(context);
		Log.i(TAG, "Retry client - explicit request (state "+currentClientState.getClientStatus());
		synchronized (BackgroundThread.class) {
			switch (currentClientState.getClientStatus()) {
			case ERROR_DOING_LOGIN:
			case ERROR_GETTING_STATE:
			case ERROR_IN_SERVER_URL:
			case CANCELLED_BY_USER:
				Log.i(TAG, "Retry from "+currentClientState.getClientStatus()+" to NEW");
				restart(context);
				break;
			default:
				// no-op
			}			
		}
	}
	public static void cancel(Context context) {
		checkThread(context);
		Log.i(TAG, "Cancel client - explicit request (state "+currentClientState.getClientStatus());
		synchronized (BackgroundThread.class) {
			switch (currentClientState.getClientStatus()) {
			case LOGGING_IN:
			case GETTING_STATE:
				Log.i(TAG, "Cacnel from "+currentClientState.getClientStatus()+" to CANCELLED_BY_USER");
				if (singleton!=null && singleton.isAlive())
					singleton.interrupt();
				currentClientState.setLoginMessage("Cancelled by user");
				currentClientState.setClientStatus(ClientStatus.CANCELLED_BY_USER);
				fireClientStateChanged(currentClientState.clone());
				break;
			default:
				// no op
			}
		}
	}
}
