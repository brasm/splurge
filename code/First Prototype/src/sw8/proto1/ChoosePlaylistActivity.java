package sw8.proto1;

import java.io.File;
import java.io.FilenameFilter;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.os.Bundle;

public class ChoosePlaylistActivity extends ListActivity {

	private static final String MEDIA_PATH = new String("/sdcard/Music");
	private Playlist songs = new Playlist();
	private MediaPlayer mp = new MediaPlayer();
	private int currentPosition = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songlist);
		updateSongList();
	}

	public void updateSongList() {
		File home = new File(MEDIA_PATH);
		if (home.listFiles(new Mp3Filter()).length > 0) {
			for (File file : home.listFiles(new Mp3Filter())) {
				Song s = new Song(file.getName(), file.getAbsolutePath());
				songs.add(s);
			}
		}

		if (home.listFiles(new WmaFilter()).length > 0) {
			for (File file : home.listFiles(new WmaFilter())) {
				Song s = new Song(file.getName(), file.getAbsolutePath());
				songs.add(s);
			}

		}

		if (home.listFiles().length > 0) {
			for (File file : home.listFiles()) {
				if (file.isDirectory()) {
					Song s = new Song(file.getName(), file.getAbsolutePath());
					songs.add(s);
				}
			}

		}

		SongListAdapter songList = new SongListAdapter(this, songs);
		setListAdapter(songList);

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
