package sw8.proto1;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ChoosePlaylistActivity extends ListActivity {

	private static final String MEDIA_PATH = new String("/sdcard/Music/Alphabeat/The Best of Blue Magic_ Soulful Spell");
	private List<String> songs = new ArrayList<String>();
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
				songs.add(file.getAbsolutePath());
			}
		}

		if (home.listFiles(new WmaFilter()).length > 0) {
			for (File file : home.listFiles(new WmaFilter())) {
				songs.add(file.getAbsolutePath());
			}

		}

		ArrayAdapter<String> songList = new ArrayAdapter<String>(this,
				R.layout.songlist_item, songs);
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
