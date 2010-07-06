/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import android.util.Log;

/** Background thread to handle communication with the server.
 * A full service is not required as it is all within a single application.
 * 
 * @author cmg
 *
 */
public class BackgroundThread implements Runnable {
	public static final String TAG = "ExplodingPlacesBackgroundThread";
	private static final long THREAD_SLEEP_MS = 1000;
	/** cons - private */
	private BackgroundThread() {
		super();
	}
	/** singleton */
	private static Thread singleton;
	/** run method */
	@Override
	public void run() {
		while (Thread.currentThread()==singleton) {
			try {
				synchronized (BackgroundThread.class) {
					switch (currentClientState.getClientStatus()) {
					case NEW:
						// TODO log in
					}
				}
				Thread.sleep(THREAD_SLEEP_MS);
			}
			catch (Exception e) {
				Log.e(TAG, "Exception in background thread "+Thread.currentThread(), e);
				// TODO ERROR?
			}
		}
		Log.i(TAG, "Background thread "+Thread.currentThread()+" exiting (interrupted="+Thread.interrupted()+")");
	}
	/** listeners */
	private static LinkedList<WeakReference<ClientStateListener>> listeners = new LinkedList<WeakReference<ClientStateListener>>();
	/** add listener */
	public static void addClientStateListener(ClientStateListener listener) {
		checkThread();
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
	/** check */
	private static synchronized void checkThread() {
		if (currentClientState==null)
			currentClientState = new ClientState(ClientStatus.NEW, GameStatus.UNKNOWN);
		if (singleton==null || !singleton.isAlive()) {
			Log.i(TAG, (singleton!=null ? "(Re)": "")+"starting background thread");
			singleton = new Thread(new BackgroundThread());
			singleton.start();			
		}
		// TODO
	}
	/** get state (copy) */
	public static synchronized ClientState getClientState() {
		checkThread();
		return currentClientState.clone();
	}
}
