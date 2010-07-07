/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

/**
 * @author cmg
 *
 */
public class ExplodingPreferences extends PreferenceActivity {

	/**
	 * 
	 */
	public ExplodingPreferences() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		// force initialisation of device ID
		getDeviceId(this);
	}
	/** get default device id (imei) */
	public static String getDefaultDeviceId(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mTelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE  
		return imei;
	}
	public static final String CLIENT_ID = "clientId";
	/** get device id */
	public static String getDeviceId(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (!preferences.contains(CLIENT_ID) || preferences.getString(CLIENT_ID, "").length()==0) {
			preferences.edit().putString(CLIENT_ID, getDefaultDeviceId(context)).commit();
		}
		return preferences.getString(CLIENT_ID, null);
	}
}
