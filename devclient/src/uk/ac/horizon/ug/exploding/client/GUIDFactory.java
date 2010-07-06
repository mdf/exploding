/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author cmg
 *
 */
public class GUIDFactory {
	private static final String TAG = "GUIDFactory";
	private static MessageDigest md;
	private static boolean inited = false;
	private static long count = 0;
	private static byte bytes[] = new byte[8];
	public static synchronized String newGUID(String deviceId) {
		if (!inited) {
			try {
				md = MessageDigest.getInstance("MD5");
			}
			catch (NoSuchAlgorithmException nsae) {
				Log.e(TAG,"No support for MD5");
			}
		}
		if (md!=null) {
			md.update(deviceId.getBytes());
			long time = System.currentTimeMillis();
			md.update(getBytes(time, bytes));
			byte dig[] = md.digest(getBytes(count++, bytes));
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<dig.length; i++) {
				int nibble = (dig[i] >> 4) & 0xf;
				sb.append(nibble>=10 ? (char)('a'+nibble-10) : (char)('0'+nibble));
				nibble = (dig[i] ) & 0xf;
				sb.append(nibble>=10 ? (char)('a'+nibble-10) : (char)('0'+nibble));
			}
			return sb.toString();
		}
		// fallback
		return deviceId+"-"+System.currentTimeMillis()+"-"+(count++);
	}
	private static byte[] getBytes(long l, byte[] bs) {
		bs[0] = (byte)(l>>56);
		bs[1] = (byte)(l>>48);
		bs[2] = (byte)(l>>40);
		bs[3] = (byte)(l>>32);
		bs[4] = (byte)(l>>24);
		bs[5] = (byte)(l>>16);
		bs[6] = (byte)(l>>8);
		bs[7] = (byte)(l);
		return bs;
	}

}
