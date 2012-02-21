package sw8.proto1;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PlayService extends Service {

	private static final String tag = "SW8tag.PlayService";
	public static final String UPDATE_INFO = "com.sw802f12.playservice.update_info";
	private final Handler handler = new Handler();
	Intent intent;
	boolean playing;

	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();
	// Mediaplayer
	MediaPlayer songPlayer = new MediaPlayer();

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(UPDATE_INFO);
		Log.d(tag, "in oncreate. made intent.");
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 100); // 0.1 second
		Log.d(tag, "in onCreate. Handler started runnable.");
	}

//	@Override
//	public void onStart(Intent intent, int startId) {
//		handler.removeCallbacks(sendUpdatesToUI);
//		handler.postDelayed(sendUpdatesToUI, 100); // 0.1 second
//		Log.d(tag, "in onStart. Handler started runnable.");
//	}

	private Runnable sendUpdatesToUI = new Runnable() {
		public void run() {
			Log.d(tag, "runnable started.");
			updateInfo();
			handler.postDelayed(this, 100); // 0.1 seconds
			
		}
	};

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		PlayService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return PlayService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/** method for clients */
	public void playTrack(Uri trackUri) {
		songPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			songPlayer.setDataSource(getApplicationContext(), trackUri);
			songPlayer.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		songPlayer.start();
		Log.d(tag, "music started.");
		playing = true;
	}

	public void die() {
		songPlayer.release();
	}

	private void updateInfo() {
		if (playing) {
			Log.d(tag, "Started building track info.");
			// Current progress
			float current = songPlayer.getCurrentPosition();
			float dur = songPlayer.getDuration();
			float progress = (current/dur) * 100;
			Log.d(tag, "Current progress was: " + progress + "     " + songPlayer.getCurrentPosition() + "      " + songPlayer.getDuration());
			// Checking to make sure that progress is calculated correctly.
			if (0 > progress || progress > 100) {
				progress = 0;
				Log.d(tag, "Progress reset to 0");
			}
			intent.putExtra("progress", progress);
			// Any other things we might need.

			// Sending the broadcast
			sendBroadcast(intent);
		}
		else
			Log.d(tag, "Track was not playing.");
	}
}
