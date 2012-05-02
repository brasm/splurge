package dk.aau.sw802f12.proto3;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import dk.aau.sw802f12.proto3.api.Playback;

public class PlayService extends Service implements OnCompletionListener, Playback {
	public static final String UPDATESTATE = PlayService.class.getCanonicalName(); 
	private final IBinder mBinder = new LocalBinder();
	private MediaPlayer mPlayer;
	private boolean mPaused = false;
	private PlayQueue mPlayQueue;
	private Song mSong;

	public class LocalBinder extends Binder {
		PlayService getService() {
			return PlayService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mPlayer = new MediaPlayer();
		mPlayer.setOnCompletionListener(this);
		mPlayQueue = PlayQueue.getInstance();
	}	

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void onCompletion(MediaPlayer mp) {
		next();
	}
	
	// Playback Controls //////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#play()
	 */
	public void play() {
		try {
			mPlayer.reset();
			mPlayer.setDataSource(mSong.getPath());
			mPlayer.prepare();
			mPlayer.start();
			mPaused = false;
		} catch (IllegalArgumentException e) {
			// TODO
		} catch (IllegalStateException e) {
			// TODO
		} catch (IOException e) {
			// TODO
		}
		updatedState();
	}
	
	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#stop()
	 */
	public void stop(){
		mPlayer.stop();
		updatedState();
	}

	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#pause()
	 */
	public void pause() {
		mPlayer.pause();
		mPaused = true;
		updatedState();
	}

	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#resume()
	 */
	public void resume() {
		mPlayer.start();
		mPaused = false;
		updatedState();
	}
	
	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#next()
	 */
	public void next(){
		Log.d(MainActivity.tag, "Start next.");
		mSong = mPlayQueue.getNext();
		play();
	}

	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#toggle()
	 */
	public void toggle() {
		if (mSong == null) {
			next();
			return;
		}
		
		if (mPlayer.isPlaying()) {
			pause();
			return;
		}
	
		if (mPaused)
			resume();
		else
			play();
	}
	
	// Playback Info //////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#nowPlaying()
	 */
	public Song nowPlaying(){
		return mSong;
	}

	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#getDuration()
	 */
	public int getDuration(){
		return mPlayer.getDuration();
	}
	
	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#getCurrentPosition()
	 */
	public int getCurrentPosition(){
		return mPlayer.getCurrentPosition();
	}
	
	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto3.Playback#getProgress()
	 */
	public int getProgress(){
		return 100 * getCurrentPosition() / getDuration();
	}
	
	private void updatedState(){
		Intent intent = new Intent(UPDATESTATE);
		String artist = mSong.getArtist();
		String title = mSong.getTitle();
		if (artist == null) artist = "Unknown Artist";
		if (title == null) title = "Unknown Title";
		
		intent.putExtra("current", artist + " - " + title);
		intent.putExtra("song1", "Not available.");
		intent.putExtra("song2", "Not available.");
		intent.putExtra("song3", "Not available.");
		intent.putExtra("song4", "Not available.");
		Log.d(MainActivity.tag, intent.getStringExtra("current"));
		sendBroadcast(intent);
	}
}