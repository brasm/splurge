package dk.aau.sw802f12.proto2;

import java.util.ArrayList;
import java.util.Collection;

import dk.aau.sw802f12.proto2.api.IPlaylist;

public class Playlist implements IPlaylist {
	private ArrayList<Song> playlist;
	private int position = 0;
	
	private int getNextPosition() {
		if (position++ == playlist.size()) position = 0;
		return position;
	}
	
	private int getPreviousPosition() {
		if (position-- < 0) position = playlist.size() - 1;
		return position;
	}
	
	public Playlist() {
		playlist = new ArrayList<Song>();
	}
	
	@Override
	public Song next() {
		return playlist.get(getNextPosition());
	}

	@Override
	public Song previous() {
		return playlist.get(getPreviousPosition());
	}

	@Override
	public boolean remove(Song song) {
		return playlist.remove(song);
	}
	
	@Override
	public boolean remove(Song song, boolean all) {
		if (all) {
			Collection<Song> c = new ArrayList<Song>();
			c.add(song);
			return playlist.removeAll(c);
		} else {
			return playlist.remove(song);
		}
	}
	
	@Override
	public boolean remove(int index) {
		if (index > 0 && index < playlist.size()) {
			playlist.remove(index);
			return true;
		}
		return false;
	}

	@Override
	public boolean append(Song song) {
		playlist.add(song);
		return true;
	}

	@Override
	public boolean clear() {
		playlist.clear();
		return true;
	}
}