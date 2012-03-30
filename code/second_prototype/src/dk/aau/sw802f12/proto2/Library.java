package dk.aau.sw802f12.proto2;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import android.os.Handler;

public class Library {
	private SongList mLocal;
	private Map<File,Integer> mModificationTimes = new HashMap<File, Integer>();
	private long mLibraryChecksum;
	private Handler mHandler = new Handler();
	private Settings mSettings = Settings.getInstance();
	
	public SongList getLocal(){
		return mLocal;
	}
	
	public long getChecksum(){
		return mLibraryChecksum;
	}
	
	// Constructor ////////////////////////////////////////////////////////////
	private static Library mInstance = null;
	public static Library getInstance(){
		if(mInstance == null){
			mInstance = new Library();
		}
		return mInstance;
	}
	
	private Library() {
		mLocal = new SongList(true,mSettings.getDeviceName());
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
	
	private FileFilter folderFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	private FileFilter musicFilter = new FileFilter() {
		public boolean accept(File pathname) {
			String fileTypeRegex = ".*\\.(flac|ogg|oga|mp3|wma)";
			return pathname.getName().toLowerCase().matches(fileTypeRegex);
		}
	};
	
	private void updateLibraryChecksum() {
		long modsum = 0;
		for(Entry<File, Integer> e :mModificationTimes.entrySet()){
			modsum += (long) e.getValue();
		}
		mLibraryChecksum = modsum;		
	}
	
	private void scanFilesystem(File dir){
		if(! dir.isDirectory())
			return;
		
		Integer lastModifiedOld = mModificationTimes.get(dir);
		Integer lastModifiedNew = new Integer((int) dir.lastModified());
		
		if (! lastModifiedNew.equals(lastModifiedOld)){
			updateLibraryFolder(dir,false);
			mModificationTimes.put(dir,lastModifiedNew);
		}
		
		for (File subfolder : dir.listFiles(folderFilter)){
			scanFilesystem(subfolder);
		}
	}
	
	private void updateLibraryFolder(File dir,boolean recursive) {
		if (! dir.isDirectory())
			return;
		
		for (File f : dir.listFiles(musicFilter)) {
			try {
				mLocal.put(new Song(f));
			} catch (IllegalArgumentException e) {
				continue;
			}
		}
		
		if (! recursive)
			return;
					
		for (File sub : dir.listFiles(folderFilter)){
			updateLibraryFolder(sub, true);
		}
	}
}
