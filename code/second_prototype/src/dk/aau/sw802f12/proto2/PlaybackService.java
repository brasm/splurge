package dk.aau.sw802f12.proto2;

import java.io.IOException;

import dk.aau.sw802f12.proto2.api.Playback;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlaybackService extends Service implements OnCompletionListener, Playback{
	public static final String UPDATESTATE = PlaybackService.class.getCanonicalName(); 
	private final IBinder mBinder = new LocalBinder();
	private MediaPlayer mPlayer;
	private boolean mPaused = false;
	private PlayQueue mPlayQueue;
	private Song mSong;

	public class LocalBinder extends Binder {
		Playback getService() {
			return PlaybackService.this;
		}
	}

	@Override
	public void onCreate() {
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
	 * @see dk.aau.sw802f12.proto2.Playback#play()
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
	 * @see dk.aau.sw802f12.proto2.Playback#stop()
	 */
	public void stop(){
		mPlayer.stop();
		updatedState();
	}

	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto2.Playback#pause()
	 */
	public void pause() {
		mPlayer.pause();
		mPaused = true;
		updatedState();
	}

	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto2.Playback#resume()
	 */
	public void resume() {
		mPlayer.start();
		mPaused = false;
		updatedState();
	}
	
	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto2.Playback#next()
	 */
	public void next(){
		mSong = mPlayQueue.getNext();
		play();
	}

	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto2.Playback#toggle()
	 */
	public void toggle() {
		Log.d("PLAYBACK", "toggle playback");
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
	 * @see dk.aau.sw802f12.proto2.Playback#nowPlaying()
	 */
	public Song nowPlaying(){
		return mSong;
	}

	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto2.Playback#getDuration()
	 */
	public int getDuration(){
		return mPlayer.getDuration();
	}
	
	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto2.Playback#getCurrentPosition()
	 */
	public int getCurrentPosition(){
		return mPlayer.getCurrentPosition();
	}
	
	/* (non-Javadoc)
	 * @see dk.aau.sw802f12.proto2.Playback#getProgress()
	 */
	public int getProgress(){
		return 100 * getCurrentPosition() / getDuration();
	}
	
	private void updatedState(){
		Intent intent = new Intent(UPDATESTATE);
		sendBroadcast(intent);
	}
}
