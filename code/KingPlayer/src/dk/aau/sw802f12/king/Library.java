package dk.aau.sw802f12.king;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class Library {
	
	private Map<String,SongList> mAllSongs;
	private SongList mLocal;
	
	public SongList getLocal(){
		return mLocal;
	}
	
	private static Library mInstance = null;
	public static Library getInstance(){
		Log.d("KING", "Library.getInstance()");
		if(mInstance == null){
			mInstance = new Library();
		}
		return mInstance;
	}
		
	private Library() {
		mAllSongs = new HashMap<String,SongList>();
		mLocal = new SongList(true,"local");
		mAllSongs.put(mLocal.NAME,mLocal);
		updateLocal(new File("/sdcard/Music"));
	}

	private void updateLocal(File dir) {
		File[] files;
		try {
			files = dir.listFiles();
		} catch (NullPointerException e){
			throw new IllegalArgumentException("Not a directory");
		}
		
		for (File f : files) {
			if (f.isDirectory()) {
				updateLocal(f);
				continue;
			}
			
			try {
				mLocal.add(new Song(f));
			} 
			catch (IllegalArgumentException e) {
				continue;
			}
		}
	}
}
