package dk.aau.sw802f12.proto3;

import android.util.Log;


public class PlayQueue {
	
	private Library mLibrary = Library.getInstance();
	
	public Song getNext(){
		return mLibrary.getLocal().getRandom();
	}
	
	// Singelton //////////////////////////////////////////////////////////////
	private static PlayQueue mInstance = null;
	public static PlayQueue getInstance(){
		if( mInstance == null)
			mInstance = new PlayQueue();
		return mInstance;
	}
	private PlayQueue(){	}
}
		
	

