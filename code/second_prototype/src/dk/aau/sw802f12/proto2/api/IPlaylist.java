package dk.aau.sw802f12.proto2.api;

import dk.aau.sw802f12.proto2.Song;

public interface IPlaylist {
	public Song next();
	public Song previous();
	public boolean remove(Song song);
	public boolean append(Song song);
	public boolean clear();
}
