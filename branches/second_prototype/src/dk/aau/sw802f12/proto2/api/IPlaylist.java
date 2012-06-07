package dk.aau.sw802f12.proto2.api;

import dk.aau.sw802f12.proto2.Song;

public interface IPlaylist {
	/**
	 * Retrieve the next song in the playlist. If at end of playlist, loops back to start.
	 * @return next Song.
	 */
	public Song next();
	
	/**
	 * Retrieve the previous song in the playlist. If at the start of the playlist, loops back to end.
	 * @return previous song.
	 */
	public Song previous();
	
	/**
	 * Removes the first occurrence of the song.
	 * @param song
	 * @return true on success, false on failure
	 */
	public boolean remove(Song song);
	
	/**
	 * Removes either one or all occurrences of the provided song.
	 * @param song Song to remove
	 * @param all Whether to remove one single song, or all occurrences.
	 * @return
	 */
	public boolean remove(Song song, boolean all);
	
	/**
	 * Remove the song at the provided index.
	 * @param index
	 * @return true if index exists in list, else false.
	 */
	public boolean remove(int index);
	public boolean append(Song song);
	public boolean clear();
}