/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import android.os.Bundle;
import android.preference.PreferenceActivity;

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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
