package dk.aau.sw802f12.proto2;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.IBinder;

public class PlayService extends Service implements OnBufferingUpdateListener,
		OnCompletionListener, OnErrorListener, OnInfoListener,
		OnPreparedListener, OnSeekCompleteListener {

	private MediaPlayer mediaPlayer = new MediaPlayer();
	private String TAG = "PlayService";
	private String audioPath = "/sdcard/Music/02 - Welcome Home.ogg";  

	@Override
	public void onCreate() {
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.reset();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(audioPath);
			mediaPlayer.prepareAsync();
		} catch (IOException e){
			Log.d(TAG,"No Such File: " + audioPath);
		}
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub

	}
}
