package dk.aau.sw802f12.musicplayer.util;

import java.util.Collection;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class MusicFactory {
	HashMap<Long, Artist> artists;
	HashMap<Long, Song> songs;
	HashMap<Long, Tag> tags;
	HashMap<Long, User> users;
	private static TestDBOpenHelper db;
	private static MusicFactory instance = null;
	
	public static MusicFactory getInstance(Context context) {
		if (instance == null) {
			instance = new MusicFactory(context);
			db = new TestDBOpenHelper(context);
			db.getWritableDatabase();
		}
		return instance;
	}
	
	/**
	 * Create new MusicFactory, for loading {@link Artist}s, {@link Song}s, and {@link Tag}s from the database.
	 * @param context
	 */
	private MusicFactory(Context context) {
		artists = new HashMap<Long, Artist>();
		songs = new HashMap<Long, Song>();
		tags = new HashMap<Long, Tag>();
		users = new HashMap<Long, User>();
	}
	
	/**
	 * Retrieve the {@link Artist} with the provided id from the Database.
	 * @param artist Id The id to search for.
	 * @return The Artist with the provided id.
	 */
	public Artist getArtist(long artistId) {
		Artist artist = artists.get(artistId);
		if (artist == null) {
			artist = db.getArtist(artistId);
			if (artist != null) add(artist);
		}
		return artist;
	}
	
	/**
	 * Retrieve the {@link Song} with the provided id from the Database.
	 * @param songId The id to search for.
	 * @return The Song with the provided id.
	 */
	public Song getSong(long songId) {
		Song song = songs.get(songId);
		if (song == null) {
			song = db.getSong(songId);
			if (song != null) add(song);
		}
		return song;
	}
	
	/**
	 * Retrieve the {@link Tag} with the provided id from the Database.
	 * @param tagId The id to search for.
	 * @return The Tag with the provided id.
	 */
	public Tag getTag(long tagId) {
		Tag tag = tags.get(tagId);
		if (tag == null) {
			tag = db.getTag(tagId);
			if (tag != null) add(tag);
		}
		return tag;
	}
	
	/**
	 * Retrieve the {@link User} with the provided id from the Database.
	 * @param userId The id to search for.
	 * @return The User with the provided id.
	 */
	public User getUser(long userId) {
		User user = users.get(userId);
		if (user == null) {
			user = db.getUser(userId);
			if (user != null) add(user);
		}
		return user;
	}
	
	/**
	 * Add {@link Artist} to Factory Artist list.
	 * @param artist The Artist to add.
	 */
	void add(Artist artist) {
		artists.put(artist.getId(), artist);
	}
	
	/**
	 * Add {@link Song} to Factory Song list.
	 * @param song The Song to add.
	 */
	void add(Song song) {
		songs.put(song.getId(), song);
	}
	
	/**
	 * Add {@link Tag} to Factory Tag list.
	 * @param tag The Tag to add.
	 */
	void add(Tag tag) {
		tags.put(tag.getId(), tag);
	}
	
	/**
	 * Add {@link User} to Factory User list.
	 * @param user The User to add.
	 */
	void add(User user) {
		users.put(user.getId(), user);
	}
	
	/**
	 * Update the database with the provided {@link Artist}.
	 * If the {@link Artist} exists in the database already, the {@link Artist} will be updated.
	 * Else, the {@link Artist} will be inserted, and the id it has been assigned, will be set in the {@link Artist}.
	 * Additionally, member element lists will be updated.
	 * @param artist The {@link Artist} to update.
	 */
	public void updateDB(Artist artist) {
		db.updateDB(artist);
	}
	
	/**
	 * Update the database with the provided {@link Song}.
	 * If the {@link Song} exists in the database already, the {@link Song} will be updated.
	 * Else, the {@link Song} will be inserted, and the id it has been assigned, will be set in the {@link Song}.
	 * Additionally, member element lists will be updated.
	 * @param artist The {@link Song} to update.
	 */
	public void updateDB(Song song) {
		db.updateDB(song);
	}
	
	/**
	 * Update the database with the provided {@link Tag}.
	 * If the {@link Tag} exists in the database already, the {@link Tag} will be updated.
	 * Else, the {@link Tag} will be inserted, and the id it has been assigned, will be set in the {@link Tag}.
	 * @param artist The {@link Tag} to update.
	 */
	public void updateDB(Tag tag) {
		db.updateDB(tag);
	}
	
	/**
	 * Update the database with the provided {@link User}.
	 * If the {@link User} exists in the database already, the {@link User} will be updated.
	 * Else, the {@link User} will be inserted, and the id it has been assigned, will be set in the {@link User}.
	 * Additionally, member element lists will be updated.
	 * @param artist The {@link User} to update.
	 */
	public void updateDB(User user) {
		db.updateDB(user);
	}
	
	/**
	 * Retrieve all {@link User}s from the database.
	 * @return The {@link User}s present in the database. 
	 */
	public Collection<User> getUsers() {
		return db.getUsers();
	}
	
	/**
	 * Retrieve all {@link Tag}s from the database.
	 * @return The {@link Tag}s present in the database. 
	 */
	public Collection<Tag> getTags() {
		return db.getTags();
	}
	
	/**
	 * Retrieve all {@link Song}s from the database.
	 * @return The {@link Song}s present in the database. 
	 */
	public Collection<Song> getSongs() {
		return db.getSongs();
	}
	
	/**
	 * Retrieve all {@link Artist}s from the database.
	 * @return The {@link Artist}s present in the database. 
	 */
	public Collection<Artist> getArtists() {
		return db.getArtists();
	}

	/**
	 * Retrieve a {@link Song} from the registry by id.
	 * Returns null if the {@link Song} has not yet been loaded.
	 * @param songId The id to check.
	 * @return The {@link Song}, or null.
	 */
	Song songLoaded(long songId) {
		return songs.get(songId);
	}
	
	/**
	 * Retrieve a {@link Artist} from the registry by id.
	 * Returns null if the {@link Artist} has not yet been loaded.
	 * @param songId The id to check.
	 * @return The {@link Artist}, or null.
	 */
	Artist artistLoaded(long artistId) {
		return artists.get(artistId);
	}
	
	/**
	 * Retrieve a {@link Tag} from the registry by id.
	 * Returns null if the {@link Tag} has not yet been loaded.
	 * @param songId The id to check.
	 * @return The {@link Tag}, or null.
	 */	
	Tag tagLoaded(long tagId) {
		return tags.get(tagId);
	}
	
	/**
	 * Retrieve a {@link User} from the registry by id.
	 * Returns null if the {@link User} has not yet been loaded.
	 * @param songId The id to check.
	 * @return The {@link User}, or null.
	 */
	User userLoaded(long userId) {
		return users.get(userId);
	}
}