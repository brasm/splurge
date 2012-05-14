package dk.aau.sw802f12.musicplayer.util;

import java.util.Collection;
import java.util.HashSet;

/**
 * A Song is a playable track, the Media Player can Play. 
 * This Class wraps data of songs from the Database, as well as it stores the {@link Artist} of the Song, and related {@link Tag}s.
 * The id of the Song is the Database id, it is assigned at database insertion. This should only be accessed by the Database wrapper.
 * Host and location provides a way of accessing Songs of the library. 
 * Host holds the Bluetooth MAC address (local if the current device has the Song), and location is the file path to the song file.
 * 
 * @author sw802f12 (mlisby)
 *
 */
public class Song {
	private String title;
	private Artist artist;
	private User host;
	private String location;
	private HashSet<Tag> tags;
	private long id;
	
	/**
	 * Create new Song Instance.
	 * @param title The title of the song (eg. "Help!" if the song is "The Beatles - Help!").
	 * @param artist The {@link Artist} of the song.
	 * @param host The Bluetooth MAC address of the unit, the song is located at. Should be "local", if the current device holds the Song.
	 * @param location The file path to the song.
	 * @param tags A list of {@link Tag}s associated with the Song.
	 */
	public Song(String title, Artist artist, User host, String location, Collection<Tag> tags) {
		setTitle(title);
		setArtist(artist);
		setHost(host);
		setLocation(location);
		
		if (tags == null) tags = new HashSet<Tag>();
		this.tags = new HashSet<Tag>(tags);
	}
	
	public Song(String title, Artist artist, User host, String location) {
		this(title, artist, host, location, null);
	}
		
	/**
	 * Retrieve song title.
	 * @return Title of the song.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Retrieve the song artist.
	 * @return {@link Artist} of the song.
	 */
	public Artist getArtist() {
		return artist;
	}
	
	/**
	 * Retrieve the {@link User}, holding the song.
	 * @return The MAC address of the owner unit of the song.
	 */
	public User getHost() {
		return host;
	}
	
	/**
	 * Retrieve the location of the song on the host device.
	 * @return Location of the song on the host device.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Retrieve the database id of the song.
	 * @return The database id of the song.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Retrieve a {@link HashSet} of {@link Tag}s of the ISong.
	 * @return The {@link Tag}s, the song is tagged with.
	 */
	public HashSet<Tag> getTags() {
		return tags;
	}
	
	/**
	 * Set the title of the song.
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Set the Artist of the song.
	 * @param artist The {@link Artist} to set.
	 */
	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	
	/**
	 * Set the MAC address of the host of the ISong.
	 * @param host The User, the Song is located at.
	 */
	public void setHost(User host) {
		this.host = host;
	}
	
	/**
	 * Set the location of the song on the host device.
	 * @param location The location to set.
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * Add an additional Tag to the Song.
	 * @param tag The Tag to add.
	 */
	public void addTag(Tag tag) {
		tags.add(tag);
	}
	
	/**
	 * Set the database id of the Song.
	 * (Should only be accessed from the database wrapper).
	 * @param id The id to set.
	 */
	void setId(long id) {
		this.id = id;
	}
}