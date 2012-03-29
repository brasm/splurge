package dk.aau.sw802f12.king;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlaybackService extends Service implements OnCompletionListener {
	private final IBinder mBinder = new LocalBinder();
	private MediaPlayer mPlayer;
	private boolean mPaused = false;
	private PlayQueue mPlayQueue;
	private Song mNext;

	public class LocalBinder extends Binder {
		PlaybackService getService() {
			return PlaybackService.this;
		}
	}

	@Override
	public void onCreate() {
		mPlayer = new MediaPlayer();
		mPlayer.setOnCompletionListener(this);
		mPlayQueue = PlayQueue.getInstance();
		Log.d("KING", "onCreate done");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void onCompletion(MediaPlayer mp) {
		next();
	}


	
	// Playback Controls //////////////////////////////////////////////////////
	
	public void play() {
		try {
			mPlayer.reset();
			mPlayer.setDataSource(mNext.getPath());
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
	}
	
	public int getDuration(){
		return mPlayer.getDuration();
	}
	
	public int getCurrentPosition(){
		return mPlayer.getCurrentPosition();
	}
	
	public int getProgress(){
		return (int) 100 * getCurrentPosition() / getDuration();
	}
	
	public void stop(){
		mPlayer.stop();
	}

	public void pause() {
		mPlayer.pause();
		mPaused = true;
	}

	public void resume() {
		mPlayer.start();
		mPaused = false;
	}
	
	public void next(){
		Log.d("KING", "PlaybackService.next()");
		mNext = mPlayQueue.getNext();
		Log.d("KING",String
				.format("Artist: %s, Title: %s, Album: %s", 
						mNext.getArtist(), mNext.getTitle(), mNext.getAlbum()));
		
		play();
	}

	public void toggle() {
		if (mNext == null) {
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
}
