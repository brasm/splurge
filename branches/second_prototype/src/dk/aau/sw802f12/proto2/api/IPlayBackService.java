package dk.aau.sw802f12.proto2.api;

import dk.aau.sw802f12.proto2.Song;

public interface IPlayBackService {
	public boolean next();
	public boolean previous();
	public boolean stop();
	public boolean searchToTime(int progress);
	public boolean remove(Song song);
	public boolean append(Song song);
	public boolean clear();
}
