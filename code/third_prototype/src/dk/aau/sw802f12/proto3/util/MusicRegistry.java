package dk.aau.sw802f12.proto3.util;

import java.util.Collection;
import java.util.HashMap;

import android.content.Context;

public class MusicRegistry {
	HashMap<Long, Artist> artists;
	HashMap<Long, Song> songs;
	HashMap<Long, Tag> tags;
	HashMap<Long, User> users;
	private static DBHelper db;
	private static MusicRegistry instance = null;
	
	public static MusicRegistry getInstance(Context context) {
		if (instance == null) {
			instance = new MusicRegistry(context);
			db = new DBHelper(context);
			db.getWritableDatabase();
		}
		return instance;
	}
	
	/**
	 * Create new MusicRegistry, for loading {@link Artist}s, {@link Song}s, and {@link Tag}s from the database.
	 * @param context
	 */
	private MusicRegistry(Context context) {
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
	
	/**
	 * Retrieve an {@link Artist} from the database, or, should it not exist, create it.
	 * Note that the provided name must match exactly, or else a new Artist will be created.
	 * @param name The name of the Artist to get.
	 * @return The Artist with the provided name.
	 */
	public Artist createArtist(String name) {
		Artist a = db.searchArtist(name);
		if (a != null) return a;

		a = new Artist(name);
		updateDB(a);
		return a;
	}
	
	/**
	 * Retrieve a {@link Song} from the database, or, should it not exist, create it.
	 * Note that the provided attributes must match exactly, or else a new Song will be created.
	 * @param title The title of the Song.
	 * @param artist The name of the {@link Artist} of the Song.
	 * @param host The bluetooth MAC address of the {@link User} hosting the Song.
	 * @param location The path to the Song.
	 * @return The Song with the parameters passed.
	 */
	public Song createSong(String title, String artist, String host, String location) {
		Song s = db.searchSong(title, artist, host, location);
		
		if (s == null) {
			User u = new User(host);
			db.updateDB(u);
			s = new Song(title, createArtist(artist), u, location);
			db.updateDB(s);
		}
		return s;
	}
	
	/**
	 * Retrieve a {@link Song} from the database, or, should it not exist, create it.
	 * Note that the provided attributes must match exactly, or else a new Song will be created.
	 * @param title The title of the Song.
	 * @param artist The {@link Artist} of the Song.
	 * @param host The bluetooth MAC address of the {@link User} hosting the Song.
	 * @param location The path to the Song.
	 * @return The Song with the parameters passed.
	 */
	public Song createSong(String title, Artist artist, String host, String location) {
		Song s = db.searchSong(title, artist, host, location);
		if (s == null) {
			User u = new User(host);
			db.updateDB(u);
			s = new Song(title, artist, u, location);
			db.updateDB(s);
		}
		return s;
	}
	
	/**
	 * Retrieve a {@link Song} from the database, or, should it not exist, create it.
	 * Note that the provided attributes must match exactly, or else a new Song will be created.
	 * @param title The title of the Song.
	 * @param artist The {@link Artist} of the Song.
	 * @param host The {@link User} hosting the Song.
	 * @param location The path to the Song.
	 * @return The Song with the parameters passed.
	 */
	public Song createSong(String title, Artist artist, User host, String location) {
		Song s = db.searchSong(title, artist, host, location);
		if (s == null) {
			s = new Song(title, artist, host, location);
			db.updateDB(s);
		}
		return s;
	}
	
	/**
	 * Retrieve a {@link Song} from the database, or, should it not exist, create it.
	 * Note that the provided attributes must match exactly, or else a new Song will be created.
	 * @param title The title of the Song.
	 * @param artist The name of the {@link Artist} of the Song.
	 * @param host The {@link User} hosting the Song.
	 * @param location The path to the Song.
	 * @return The Song with the parameters passed.
	 */
	public Song createSong(String title, String artist, User host, String location) {
		Song s = db.searchSong(title, artist, host, location);
		if (s == null) {
			s = new Song(title, createArtist(artist), host, location);
			db.updateDB(s);
		}
		return s;
	}
	
	/**
	 * Retrieve a {@link User} from the database, or, should it not exist, create it.
	 * Note that the provided bluetooth address must match exactly - though casing is not important.
	 * @param btAddress The Bluetooth address of the User.
	 * @return The User with the Bluetooth address passed.
	 */
	public User createUser(String btAddress) {
		btAddress = btAddress.toUpperCase();
		User u = db.searchUser(btAddress);
		if (u == null) {
			u = new User(btAddress);
			db.updateDB(u);
		}
		return u;
	}
	
	/**
	 * Retrieve a {@link Tag} from the database, or, should it not exist, create it.
	 * Note that the provided tag name must match exactly, or else, a new tag will be created.
	 * @param tagName The Tag name to search for.
	 * @return The matching tag.
	 */
	public Tag createTag(String tagName) {
		Tag t = db.searchTag(tagName);
		if (t == null) {
			t = new Tag(tagName);
			db.updateDB(t);
		}
		return t;
	}
}