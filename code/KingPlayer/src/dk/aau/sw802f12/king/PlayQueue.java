package dk.aau.sw802f12.king;

import android.util.Log;

public class PlayQueue {
	
	private Library mLibrary;
	private static PlayQueue mInstance = null;
	
	public Song getNext(){
		return mLibrary.getLocal().getRandom();
	}
	
	public static PlayQueue getInstance(){
		Log.d("KING","PlayQueue.getInstance()");
		if( mInstance == null){
			mInstance = new PlayQueue();
		}
		return mInstance;
	}
	
	private PlayQueue(){
		mLibrary = Library.getInstance();
	}
}
		
	

