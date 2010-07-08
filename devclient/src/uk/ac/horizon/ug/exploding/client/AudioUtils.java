/**
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

/**
 * @author cmg
 *
 */
public class AudioUtils {
	private static final int MAX_STREAMS = 4;
	private static final String TAG = "AudioUtils";
	private static final int MAX_WAIT_COUNT = 500;
	private static final int MAX_WAIT_MS = 10;
	/** singleton application sound poool */
	private static SoundPool soundPool;
	/** map of sound resource id to sound id */
	private static Map<Integer,Integer> resourceToSound = new HashMap<Integer,Integer>();
	/** map of sound id to stream id, at least for single play sounds */
	private static Map<Integer,Integer> soundToStream = new HashMap<Integer,Integer>();
	/** sound characteristics */
	public static class SoundAttributes {
		public float leftVolume = 1.0f;
		public float rightVolume = 1.0f;
		public boolean loop = false;
		public float rate = 1.0f;
		/** default */
		public SoundAttributes() {}
		public SoundAttributes(float leftVolume, float rightVolume,
				boolean loop, float rate) {
			super();
			this.leftVolume = leftVolume;
			this.rightVolume = rightVolume;
			this.loop = loop;
			this.rate = rate;
		}
		
	}
	/** attributes by resource id */
	private static Map<Integer, SoundAttributes> resourceToAttributes = new HashMap<Integer, SoundAttributes>();
	public static synchronized void init() {
		if (soundPool==null)
			soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
	}
	/** add audio files specified by resource ids to pool */
	public static synchronized void addSoundFiles(Context context, int resIds[]) {
		init();
		for (int i=0; i<resIds.length; i++)
			addSoundFile(context, resIds[i], new SoundAttributes());
	}
	/** add audio files specified by resource ids to pool */
	public static synchronized void addSoundFile(Context context, int resId, SoundAttributes attributes) {		
		init();
		// default "prioirity" = 1 (future compat.)
		if (!resourceToSound.containsKey(resId))
			resourceToSound.put(resId, soundPool.load(context, resId, 1));
		resourceToAttributes.put(resId, attributes);
	}
	/** play resource id from start (stop if already playing) */
	public static synchronized void play(int resId, float leftVolume, float rightVolume) {
		SoundAttributes attributes = resourceToAttributes.get(resId);
		if (attributes==null) {
			attributes = new SoundAttributes();
			resourceToAttributes.put(resId, attributes);
		}
		attributes.leftVolume = leftVolume;
		attributes.rightVolume = rightVolume;
		play(resId);
	}
	/** play resource id from start (stop if already playing) */
	public static synchronized void play(int resId) {
		SoundAttributes attributes = resourceToAttributes.get(resId);
		if (attributes==null)
			attributes = new SoundAttributes(); // default
		if (!resourceToSound.containsKey(resId)) {
			Log.e(TAG, "Sound resource "+resId+" unknown");
			return;
		}
		int sound = resourceToSound.get(resId);
		if (soundToStream.containsKey(sound)) 
			soundPool.stop(soundToStream.get(sound));
		int waiti = 0;
		int stream = 0;
		try {
			while(waiti<MAX_WAIT_COUNT) {
				stream = soundPool.play(sound, attributes.leftVolume, attributes.rightVolume, 1, attributes.loop ? -1 : 0, attributes.rate);
				if (stream!=0) {
					break;
				}
				Thread.sleep(MAX_WAIT_MS);
				if (waiti==0) 
					Log.w(TAG, "Waiting in play resource...");
				waiti++;
			}
		}
		catch (InterruptedException ie) {
			Log.e(TAG, "delayed play interrupted", ie);
		}
		if (stream!=0) {
			Log.d(TAG, "Playing resource "+resId);
			soundToStream.put(sound, stream);
			streamsPlaying.add(stream);
			if (paused>0)
				// pause immediately (best we can do without getting much more complicated)
				soundPool.pause(stream);
		}
		else
			Log.e(TAG, "Gave up trying to play resource "+resId);
			
	}
	/** stop (pause) resource id (no action if not playing) */
	public static synchronized void stop(int resId) {
		if (!resourceToSound.containsKey(resId)) {
			Log.e(TAG, "Sound resource "+resId+" unknown");
			return;
		}
		int sound = resourceToSound.get(resId);
		if (soundToStream.containsKey(sound)) {
			int stream = soundToStream.get(sound);
			soundPool.pause(stream);
			streamsPlaying.remove(stream);
		}
	}
	/** resume resource id (no action if not paused) */
	public static synchronized void resume(int resId) {
		if (!resourceToSound.containsKey(resId)) {
			Log.e(TAG, "Sound resource "+resId+" unknown");
			return;
		}
		int sound = resourceToSound.get(resId);
		if (soundToStream.containsKey(sound)) {
			int stream = soundToStream.get(sound);
			streamsPlaying.add(stream);
			if (paused==0)
				soundPool.resume(soundToStream.get(sound));
		}
	}
	/** set volume of resource (and stream if playing) */
	public static synchronized void setVolume(int resId, float leftVolume, float rightVolume) {
		if (!resourceToSound.containsKey(resId)) {
			Log.e(TAG, "Sound resource "+resId+" unknown");
			return;
		}
		SoundAttributes attributes = resourceToAttributes.get(resId);
		if (attributes==null) {
			attributes = new SoundAttributes();
			resourceToAttributes.put(resId, attributes);
		}
		attributes.leftVolume = leftVolume;

		attributes.rightVolume = rightVolume;
		int sound = resourceToSound.get(resId);
		if (soundToStream.containsKey(sound))
			soundPool.setVolume(soundToStream.get(sound), leftVolume, rightVolume);
	}
	/** stream /was/ playing */
	private static Set<Integer> streamsPlaying = new HashSet<Integer>();
	private static int paused = 0;
	/** ensure all paused */
	public static synchronized void autoPause() {
		for (int stream : soundToStream.values()) {
			soundPool.pause(stream);
		}
		paused = 1;
	}
	/** resume all paused only pauseAll */
	public static synchronized void autoResume() {
		paused = 0;
		for (int stream : streamsPlaying) {
			soundPool.resume(stream);
		}
	}
	// TODO fade in / out
}
