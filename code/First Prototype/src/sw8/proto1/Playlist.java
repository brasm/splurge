package sw8.proto1;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.util.Log;

public class Playlist extends ArrayList<Song> {
	
	/**
	 * Den brokkede sig over det her:S
	 */
	private static final long serialVersionUID = 530722742350436837L;
	private static final String tag = "sw8tag.Playlist";
	private static final String MEDIA_PATH = "/sdcard/Music";
	
	public Playlist(){
		this.updateSongList(MEDIA_PATH);
		Comparator<Song> comperator = new Comparator<Song>() {
			public int compare(Song object1, Song object2) {
				return object1.getAbsPath().compareTo(object2.getAbsPath());
			}
		};
		Collections.sort(this, comperator);
	}
	
	public void updateSongList(String path) {
		File home = new File(path);
		if (home.listFiles(new Mp3Filter()).length > 0) {
			for (File file : home.listFiles(new Mp3Filter())) {
				Song s = new Song(file.getName(), file.getAbsolutePath());
				this.add(s);
			}
		}

		if (home.listFiles(new WmaFilter()).length > 0) {
			for (File file : home.listFiles(new WmaFilter())) {
				Song s = new Song(file.getName(), file.getAbsolutePath());
				this.add(s);
			}

		}

		if (home.listFiles().length > 0) {
			for (File file : home.listFiles()) {
				if (file.isDirectory()) {
					Log.d(tag, "going into new folder. Path is: " + file.getAbsolutePath());
					this.updateSongList(file.getAbsolutePath());
				}
			}

		}

	}

	class Mp3Filter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3"));
		}
	}

	class WmaFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".wma"));
		}
	}
	
	
}
