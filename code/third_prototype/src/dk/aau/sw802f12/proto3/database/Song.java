package dk.aau.sw802f12.proto3.database;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import android.media.MediaMetadataRetriever;

/**
 * A Song is a playable track, the Media Player can Play. 
 * This Class wraps data of songs from the Database, as well as it stores the {@link Artist} of the Song, and related {@link Tag}s.
 * The id of the Song is the Database id, it is assigned at database insertion. This should only be accessed by the Database wrapper.
 * Host and location provides a way of accessing Songs of the library. 
 * Host holds the Bluetooth MAC address (local if the current device has the Song), and location is the file path to the song file.
 * 
 * @author sw802f12
 *
 */
public class Song {
	private volatile String title;
	private volatile Artist artist;
	private volatile User host;
	private volatile String location;
	volatile HashSet<Tag> tags;
	private volatile long id;
	private volatile File song;
	
	/**
	 * Create new Song Instance.
	 * @param title The title of the song (eg. "Help!" if the song is "The Beatles - Help!").
	 * @param artist The {@link Artist} of the song.
	 * @param host The Bluetooth MAC address of the unit, the song is located at. Should be "local", if the current device holds the Song.
	 * @param location The file path to the song.
	 * @param tags A list of {@link Tag}s associated with the Song.
	 */
	Song(String title, Artist artist, User host, String location) {
		setTitle(title);
		setArtist(artist);
		setHost(host);
		setLocation(location);
		id = -1;
		tags = new HashSet<Tag>();
	}
	
	/**
	 * Create new Song Instance from the provided path.
	 * @param path The path, the Song is located at.
	 * @throws IllegalArgumentException If the provided path does not contain a file.
	 */
	Song(String path) throws InstantiationException {
		this(new File(path));
	}
	
	/**
	 * Create new Song instance from the provided file.
	 * @param f The file, the Song is located at.
	 * @throws IllegalArgumentException If the provided file does not contain a file.
	 * @throws IllegalStateException If the Music Registry was not instantiated with context before calling.
	 */
	Song(File f) throws IllegalArgumentException, InstantiationException {
		String fileTypeRegex = ".*\\.(flac|ogg|oga|mp3|wma|m4a)";
		if (f.getName().toLowerCase().matches(fileTypeRegex))
			song = f.getAbsoluteFile();
		else
			throw new IllegalArgumentException();
		MusicRegistry mr = MusicRegistry.getInstance();
		setLocation(f.getAbsolutePath());
		MediaMetadataRetriever mmdr = new MediaMetadataRetriever();
		mmdr.setDataSource(location);
		String title = mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		setTitle((title != null) ? title : "Unknown");
		String artName = mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		setArtist(mr.createArtist((artName != null)? artName : "Unknown"));
		setHost(mr.createUser("local")); //TODO: Currently ignores unit, song is located at!!!
		id = -1;
		tags = new HashSet<Tag>();
	}
	
	/**
	 * Retrieve the {@link File}, the Song is represented by.	
	 * @return
	 */
	public File getFile() {
		if (song == null) {
			song = new File(location);
		}
		return song;
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
	 * Tag the Song with the provided {@link Tag}.
	 * @param tag The Tag to add.
	 */
	public void tag(Tag tag) {
		tag.songs.add(this);
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
	
	/**
	 * Tag the Song with the provided {@link Tag}s.
	 * @param tags The Tags to add.
	 */
	public void tag(Collection<Tag> tags) {
		for (Tag t : tags) {
			t.songs.add(this);
		}
		this.tags.addAll(tags);
	}
}