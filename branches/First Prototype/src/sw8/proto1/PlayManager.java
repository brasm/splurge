package sw8.proto1;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;

public class PlayManager extends AsyncTask<Uri, Integer, Boolean> {

	Context mContext;
	
	MediaPlayer songPlayer = new MediaPlayer();


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

	}

	@Override
	protected Boolean doInBackground(Uri... trackUri) {
		// TODO Auto-generated method stub
		
		
		
		songPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			songPlayer.setDataSource(mContext, trackUri[0]);
			songPlayer.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		songPlayer.start();
		
		return true;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		songPlayer.release();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
	
	
	public PlayManager(Context c){
		this.mContext = c;
	}

}
