package dk.aau.sw802f12.proto3;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dk.aau.sw802f12.proto3.util.Artist;
import dk.aau.sw802f12.proto3.util.MusicRegistry;
import dk.aau.sw802f12.proto3.util.Song;

import android.os.Handler;
import android.util.Log;

public class Library {
	private SongList mLocal;
	private Map<File, Integer> mModificationTimes = new HashMap<File, Integer>();
	private long mLibraryChecksum;
	private Handler mHandler = new Handler();
	private Settings mSettings = Settings.getInstance();

	public SongList getLocal() {
		return mLocal;
	}

	public Song getRandom() {
		return mLocal.getRandom();
	}
	
	public Song getRandom(Artist a) {
		return mLocal.getRandom(a);
	}

	public boolean hasArtist(Artist a) {
		return mLocal.containsArtist(a);
	}

	public long getChecksum() {
		return mLibraryChecksum;
	}

	private static Library mInstance = null;

	public static Library getInstance() {
		if (mInstance == null) {
			mInstance = new Library();
		}
		return mInstance;
	}

	private Library() {
		mLocal = new SongList(true, mSettings.getDeviceName());
		new Thread(updateRunner).start();
	}

	// Folder Scanning Thread /////////////////////////////////////////////////
	private Runnable updateRunner = new Runnable() {
		public void run() {
			File musicFolder = mSettings.getLocalMusicFolder();
			int updateInterval = mSettings.getLibraryUpdateInterval();

			scanFilesystem(musicFolder);
			updateLibraryChecksum();
			mHandler.postDelayed(updateRunner, updateInterval);
		}
	};

	private void updateLibraryChecksum() {
		long modsum = 0;
		for (Entry<File, Integer> e : mModificationTimes.entrySet()) {
			modsum += (long) e.getValue();
		}
		mLibraryChecksum = modsum;
	}

	/*
	 * check if filesystem has changed, and update library accordingly
	 */
	private void scanFilesystem(File dir) {
		if (!dir.isDirectory())	return;
		Log.d("LASTFM", "Scan Filesystem " + dir.getAbsolutePath());

		Integer lastModifiedOld = mModificationTimes.get(dir);
		Integer lastModifiedNew = new Integer((int) dir.lastModified());

		if (!lastModifiedNew.equals(lastModifiedOld)) {
			boolean success = updateFolder(dir);
			if (success){
				mModificationTimes.put(dir, lastModifiedNew);
			}
		}

		for (File folder : dir.listFiles()) {
			if (! folder.isDirectory()) continue;
			scanFilesystem(folder);
		}
	}

	private boolean updateFolder(File dir) {
		Log.d("LASTFM", "update folder");
		if (!dir.isDirectory())	return false;
		MusicRegistry registry = null;
		
		try {
			registry = MusicRegistry.getInstance();
		} catch (InstantiationException e) {
			Log.d("LASTFM", "oh no, cannot instantiate musicregistry");
		}
			
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) continue;
			Log.d("LASTFM", "song is " + f.getAbsolutePath());
			try {
				Song s = registry.createSong(f.getAbsolutePath());
				mLocal.put(s);
			} catch (IllegalArgumentException e) {
				Log.d("LASTFM", "not a valid audio file:" + f.getAbsolutePath() );
			} catch (InstantiationException e) {
				Log.d("LASTFM", "oh no, cannot instantiate musicregistry through song");
			}
			
			
		}

		
		return true;
	}
}
