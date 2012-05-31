package dk.aau.sw802f12.proto3.database;

import java.util.Collection;
import java.util.HashSet;

/**
 * Tags meta-classifies {@link Song} and {@link Artist}, and is used to group them together, 
 * allowing the recommender to suggest similar songs.
 * The id of the tag represents the database ID of the tag, and should only be modified by the database wrapper.
 * If the Tag is not added to the database, the Id is -1.
 * The tagName is a convenience representation of the Tag. This could, for instance, be "Rock", "Pop" or "Jazz".
 *  
 * @author sw802f12
 *
 */
public class Tag {
	private volatile String tagName;
	private volatile long id;
	volatile HashSet<Artist> artists;
	volatile HashSet<Song> songs;
	
	/**
	 * Create new Tag instance. The Id is, by default, set to -1.
	 * @param tag The name, the tag should carry.
	 */
	Tag(String tag) {
		setTagName(tag);
		id = -1;
		artists = new HashSet<Artist>();
		songs = new HashSet<Song>();
	}
	
	/**
	 * Retrieve the Tag name.
	 * @return The Tag name.
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Retrieve the ID, the tag is stored at in the database.
	 * If the tag is not yet created, this will return -1.
	 * @return The ID of the tag in the database.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Set the tag name.
	 * @param tag The name to set.
	 */
	public void setTagName(String tag) {
		this.tagName = tag;
	}
	
	/**
	 * Set the database id of the Tag.
	 * @param id The id to set.
	 */
	void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Tag an {@link Artist} the Tag.
	 * @param artist The Artist to Tag.
	 */
	public void tagArtist(Artist artist) {
		artist.tags.add(this);
		this.artists.add(artist);
	}
	
	/**
	 * Retrieve {@link Artist}s tagged with the Tag.
	 * @return Artists with the Tag.
	 */
	public HashSet<Artist> getTaggedArtists() {
		return artists;
	}
	
	/**
	 * Remove the {@link Artist} from the Tag, and un-tag the Artist.
	 * @param artist
	 */
	public void untagArtist(Artist artist) {
		artist.tags.remove(this);
		artists.remove(artist);
	}
	
	/**
	 * Tag several {@link Artist}s with the Tag.
	 * @param artists The Artist collection to Tag.
	 */
	public void tagArtists(Collection<Artist> artists) {
		for (Artist a : artists) {
			a.tags.add(this);
		}
		artists.addAll(artists);
	}
	
	/**
	 * Untag the Tag from several {@link Artist}s 
	 * @param artists The Artist collection to un-tag.
	 */
	public void untagArtists(Collection<Artist> artists) {
		for (Artist a : artists) {
			a.tags.remove(this);
		}
		artists.removeAll(artists);
	}
	
	/**
	 * Tag the provided {@link Song} with current Tag.
	 * @param song The Song to tag.
	 */
	public void tagSong(Song song) {
		song.tags.add(this);
		songs.add(song);
	}
	
	/**
	 * Untag the provided {@link Song}.
	 * @param song The Song to untag.
	 */
	public void untagSong(Song song) {
		song.tags.remove(this);
		songs.remove(song);
	}
	
	/**
	 * Retrieve {@link Song}s with tagged with the current Tag.
	 * @return The Songs with the current Tag.
	 */
	public HashSet<Song> getTaggedSongs() {
		return songs;
	}
	
	/**
	 * Tag the provided {@link Song}s with current Tag.
	 * @param songs The Songs to tag.
	 */
	public void tagSongs(Collection<Song> songs) {
		for (Song s : songs) {
			s.tags.add(this);
		}
		this.songs.addAll(songs);
	}
	
	/**
	 * Untag the provided {@link Song}s.
	 * @param songs The Songs to untag.
	 */
	public void untagSongs(Collection<Song> songs) {
		for (Song s : songs) {
			s.tags.remove(this);
		}
		this.songs.removeAll(songs);
	}
}
