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
	public static final String TRACK_CHANGE_NOTIFICATION = "com.sw802f12.playservice.track_change_notification";
	
	private final Handler handler = new Handler();
	Intent currentPositionIntent;
	Intent trackChangeNotificationIntent;
	Song currentSong;
	/**
	 * Whether a playback is currently in session.
	 */
	boolean playing = false;
	/**
	 * Whether the current playback is paused.
	 */
	boolean paused = false;
	
	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();
	// Mediaplayer
	MediaPlayer songPlayer = new MediaPlayer();

	@Override
	public void onCreate() {
		super.onCreate();
		currentPositionIntent = new Intent(UPDATE_INFO);
		Log.d(tag, "in oncreate. made intent.");
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
		Log.d(tag, "in onCreate. Handler started runnable.");
		
		songPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

//	@Override
//	public void onStart(Intent intent, int startId) {
//		handler.removeCallbacks(sendUpdatesToUI);
//		handler.postDelayed(sendUpdatesToUI, 100); // 0.1 second
//		Log.d(tag, "in onStart. Handler started runnable.");
//	}

	private Runnable sendUpdatesToUI = new Runnable() {
		public void run() {
			updateInfo();
			handler.postDelayed(this, 1000); // 1 seconds
			
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

	/** 
	 * Initiate or pause playback.
	 */
	public void playTrack(Song song) {
		if (paused) {
			songPlayer.start();
			paused = false;
		} else {
			if (playing) {
				songPlayer.pause();
				paused = true;
			} else {
				currentSong = song;
				startTrack(Uri.parse(song.getAbsPath()));
			}
		}
	}
	
	/**
	 * Release the SongPlayer from the current song, and reassign it to the provided URI.
	 * @param song Song to play instead of current.
	 */
	public void switchTrack(Song song) {
		songPlayer.reset();
		currentSong = song;
		playing = false;
		paused = false;
		startTrack(Uri.parse(song.getAbsPath()));
	}
	
	/**
	 * Do initial setup to start the song playback, as well as start it.
	 * @param trackUri The URI to play.
	 */
	private void startTrack(Uri trackUri) {
		try {
			songPlayer.setDataSource(getApplicationContext(), trackUri);
			Log.d(tag, "Player starting song at path: " + trackUri.toString());
			songPlayer.prepare();
		} catch (Exception e) {
			Log.d(tag + "PREP", e.getMessage());
			e.printStackTrace();
		}
		songPlayer.start();
		
		Log.d(tag, "Starting...");
		newSongNotification();
		playing = true;
		paused = false;
	}
	
	/**
	 * Send song notification intent.
	 */
	private void newSongNotification() {
		trackChangeNotificationIntent = new Intent();
		trackChangeNotificationIntent.putExtra("currentTrack", currentSong.getName());
		sendBroadcast(trackChangeNotificationIntent);
		Log.d(tag, "Track change notification sent.");
	}
	
	/**
	 * Seek to progress in the song
	 * @param progress The value of the progressBar
	 */
	public void seekTrack(int progress) {
		float msec = ((float)progress/100)*songPlayer.getDuration();
		songPlayer.seekTo((int)msec);
		Log.d(tag, "Updated song position.");
	}

	public void die() {
		songPlayer.release();
	}

	private void updateInfo() {
		if (playing) {
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
			currentPositionIntent.putExtra("progress", progress);
			// Any other things we might need.

			// Sending the broadcast
			sendBroadcast(currentPositionIntent);
		}
		else
			Log.d(tag, "Track was not playing.");
	}
}
