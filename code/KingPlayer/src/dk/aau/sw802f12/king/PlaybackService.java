package dk.aau.sw802f12.king;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;

public class PlaybackService extends Service implements OnCompletionListener {
	private final IBinder mBinder = new LocalBinder();
	private MediaPlayer mp;
	private boolean mPaused = false;
	private Library mLibrary;

	public class LocalBinder extends Binder {
		PlaybackService getService() {
			return PlaybackService.this;
		}
	}

	@Override
	public void onCreate() {
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mLibrary = new Library(new File("/sdcard/Music"));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void onCompletion(MediaPlayer mp) {
		play();
	}

	public void play() {
		Song s = nextSong();
		mp.reset();
		try {
			mp.setDataSource(s.getPath());
			mp.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mp.start();
		mPaused = false;
	}

	public void pause() {
		mp.pause();
		mPaused = true;
	}

	public void resume() {
		mp.start();
		mPaused = false;
	}

	public void toggle() {
		if (mp.isPlaying()) {
			pause();
			return;
		}

		if (mPaused)
			resume();
		else
			play();
	}

	private Song nextSong() {
		return mLibrary.getRandomSong();
	}
}
