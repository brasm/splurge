package sw8.proto1;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class PlayService extends Service {

	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();
	// Mediaplayer
	MediaPlayer songPlayer = new MediaPlayer();

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
	}
	
	public void die(){
		songPlayer.release();
	}

}
