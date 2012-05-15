package dk.aau.sw802f12.musicplayer.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The database helper to interact with the database.
 * 
 * @author sw802f12 (Michael)
 *
 */
class TestDBOpenHelper extends SQLiteOpenHelper {
	static final String TAG = "SW8DBH";
	
	/**
	 * Database helper class for table and column names
	 * @author sw802f12 (mlisby)
	 */
	class DB {
		/**
		 * Database version.
		 */
		private static final int VERSION = 1;
		/**
		 * Database name.
		 */
		private static final String NAME = "socialMusic";
		
		/**
		 * Artist table name.
		 */
		private static final String TB_ARTIST = "tbl_artist";
		/**
		 * Artist name column of artist table.
		 */
		private static final String ARTIST_NAME = "name";
		
		/**
		 * Song table name.
		 */
		private static final String TB_SONG = "tbl_song";
		/**
		 * Artist column of Song table.
		 */
		private static final String SONG_ARTIST = "artist";
		/**
		 * Song title column of Song table.
		 */
		private static final String SONG_TITLE = "title";
		/**
		 * Song host column of Song table.
		 */
		private static final String SONG_HOST = "host";
		/**
		 * Song location column of Song table.
		 */
		private static final String SONG_LOCATION = "location";
		
		/**
		 * Tag table name.
		 */
		private static final String TB_TAG = "tbl_tag";
		/**
		 * Tag value column of Tag table.
		 */
		private static final String TAG_NAME = "tagname";
		
		/**
		 * SongTag relation table name.
		 */
		private static final String TB_SONGTAGS = "tbl_songtags";
		/**
		 * Song id column of SongTag relation table.
		 */
		private static final String SONGTAG_SONG = "song";
		/**
		 * Tag id column of SongTag relation table.
		 */
		private static final String SONGTAG_TAG = "tag";
		
		/**
		 * ArtistTag relation table name.
		 */
		private static final String TB_ARTISTTAGS = "tbl_artisttags";
		/**
		 * Tag id column of ArtistTag relation table.
		 */
		private static final String ARTISTTAG_TAG = "tag";
		/**
		 * Artist id column of ArtistTag relation table.
		 */
		private static final String ARTISTTAG_ARTIST = "artist";
		/**
		 * User table name.
		 */
		private static final String TB_USER = "tbl_user";
		/**
		 * User address column of User table.
		 */
		private static final String USER_ADDRESS = "address";
		/**
		 * UserArtist relation table name.
		 */
		private static final String TB_USERARTIST = "tbl_userartist";
		/**
		 * User column of the UserArtist relation table.
		 */
		private static final String USERARTIST_USER = "user";
		/**
		 * Artist column of the UserArtist relation table.
		 */
		private static final String USERARTIST_ARTIST = "artist";
		/**
		 * Create artist table.
		 */
		private static final String SQL_ARTIST
									= "CREATE TABLE " + DB.TB_ARTIST + " ("
									+ DB.ARTIST_NAME + " TEXT)";
		
		/**
		 * Create song table.
		 */
		private static final String SQL_SONG
									= "CREATE TABLE " + DB.TB_SONG + " ("
									+ DB.SONG_ARTIST + " INTEGER, "
									+ DB.SONG_TITLE + " TEXT, "
									+ DB.SONG_HOST + " INTEGER, "
									+ DB.SONG_LOCATION + " TEXT)";
		
		/**
		 * Create Tag table.
		 */
		private static final String SQL_TAG
									= "CREATE TABLE " + DB.TB_TAG + " ("
										+ DB.TAG_NAME + " TEXT)";
		
		/**
		 * Create SongTag relation table.
		 */
		private static final String SQL_SONGTAG
									= "CREATE TABLE " + DB.TB_SONGTAGS + "("
									+ DB.SONGTAG_SONG + " INTEGER, "
									+ DB.SONGTAG_TAG + " INTEGER,"
									+ "PRIMARY KEY(" + DB.SONGTAG_SONG + ", " + DB.SONGTAG_TAG + "))";
		
		/**
		 * Create ArtistTag relation table.
		 */
		private static final String SQL_ARTISTTAG
									= "CREATE TABLE " + DB.TB_ARTISTTAGS + "("	
									+ DB.ARTISTTAG_ARTIST + " INTEGER, " 
									+ DB.ARTISTTAG_TAG + " INTEGER, "
									+ " PRIMARY KEY(" + DB.ARTISTTAG_ARTIST + "," + DB.ARTISTTAG_TAG + "))";
		
		private static final String SQL_USER
									= "CREATE TABLE " + DB.TB_USER + "("
									+ DB.USER_ADDRESS + " STRING)";
		
		private static final String SQL_USERARTIST
									= "CREATE TABLE " + DB.TB_USERARTIST + "("
									+ DB.USERARTIST_ARTIST + " INTEGER, "
									+ DB.USERARTIST_USER + " INTEGER, "
									+ "PRIMARY KEY(" + DB.USERARTIST_ARTIST + "," + DB.USERARTIST_USER + "))";
	}
		
	private SQLiteDatabase db;
	private MusicFactory mf;
	
	public TestDBOpenHelper(Context context) {
		super(context, DB.NAME, null, DB.VERSION);
		mf = MusicFactory.getInstance(context);
	}

	private void create() throws SQLException {
		Log.d(TAG, "CREATING TABLES.");
		db.execSQL(DB.SQL_ARTIST);
		db.execSQL(DB.SQL_SONG);
		db.execSQL(DB.SQL_TAG);
		db.execSQL(DB.SQL_SONGTAG);
		db.execSQL(DB.SQL_ARTISTTAG);
		db.execSQL(DB.SQL_USER);
		db.execSQL(DB.SQL_USERARTIST);
	}
	
	private void sampleData() throws IOException {
		Log.d(TAG, "LOADING SAMPLE DATA.");
		ContentValues cvA = new ContentValues(1);
		cvA.put(DB.ARTIST_NAME, "testArtist");
		long iId = db.insert(DB.TB_ARTIST, null, cvA);
		if (iId == -1) throw new IOException("DB failure.");
		
		User u = new User("local");
		long hostId = db.insert(DB.TB_USER, null, getCV(u));
		
		for (int i = 0; i < 1000; ++i) {
			ContentValues cvS = new ContentValues(4);
			cvS.put(DB.SONG_ARTIST, iId);
			cvS.put(DB.SONG_TITLE, "SampleTitle " + i);
			cvS.put(DB.SONG_HOST, hostId);
			cvS.put(DB.SONG_LOCATION, "none");
			db.insert(DB.TB_SONG, null, cvS);
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase dbs) {
		Log.d(TAG, "ONCREATE");
		db = dbs;
		
		//Create database skeleton
		db.beginTransaction();
		try {
			create();
			db.setTransactionSuccessful();
			Log.d(TAG, "Database created.");
		} catch (SQLException e) {
			Log.d(TAG, "Failed creating database.");
		} finally {
			db.endTransaction();
		}
		
		//Insert sample data
		db.beginTransaction();
		try {
			sampleData();
			db.setTransactionSuccessful();
			Log.d(TAG, "Sample data inserted...");
		} catch (IOException e) {
		} finally {
			db.endTransaction();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + DB.TB_ARTIST);
		db.execSQL("drop table if exists " + DB.TB_SONG);
		db.execSQL("drop table if exists " + DB.TB_TAG);
		db.execSQL("drop table if exists " + DB.TB_SONGTAGS);
		db.execSQL("drop table if exists " + DB.TB_ARTISTTAGS);
		db.execSQL("drop table if exists " + DB.TB_USER);
		db.execSQL("drop table if exists " + DB.TB_USERARTIST);
		onCreate(db);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		onUpgrade(db, 0,0);
	}
	
	/**
	 * Empty the database (when the library is no longer up to date).
	 */
	public void clearDB() {
		String truncate = "truncate table if exists ";
		db.execSQL(truncate + DB.TB_ARTIST);
		db.execSQL(truncate + DB.TB_SONG);
		db.execSQL(truncate + DB.TB_TAG);
		db.execSQL(truncate + DB.TB_SONGTAGS);
		db.execSQL(truncate + DB.TB_ARTISTTAGS);
		db.execSQL(truncate + DB.TB_USER);
		db.execSQL(truncate + DB.TB_USERARTIST);
	}
	
	/**
	 * Update database with the provided {@link Song}.
	 * If the Song already exists in the database, the Song is updated.
	 * If it does not yet exist, it will be inserted.
	 * @param song The {@link Song} to insert or update.
	 * @return The id of the Song in the database.
	 */
	long updateDB(Song song) {
		long ret = -1;
		if (song.getId() == -1) ret = add(song);
		else ret = update(song);
		
		for (Tag tag : song.getTags()) {
			add(song, tag);
		}
		
		return ret;
	}
	
	/**
	 * Update database with the provided {@link Artist}.
	 * If the Artist already exists in the database, the Artist is updated.
	 * If it does not yet exist, it will be inserted.
	 * @param artist The {@link Artist} to insert or update.
	 * @return The id of the Artist in the database.
	 */
	long updateDB(Artist artist) {
		long ret;
		if (artist.getId() == -1) ret = add(artist);
		else ret = update(artist);
		
		for (Tag tag : artist.getTags()) {
			add(artist, tag);
		}
		
		return ret;
	}
	
	/**
	 * Update database with the provided {@link Tag}.
	 * If the Tag already exists in the database, the Tag is updated.
	 * If it does not yet exist, it will be inserted.
	 * @param tag The Tag to update or add.
	 * @return The id of the Tag in the Database.
	 */
	long updateDB(Tag tag) {
		if (tag.getId() == -1) return add(tag);
		return update(tag);
	}
	
	/**
	 * Update database with the provided {@link User}.
	 * If the User already exists in the database, the User is updated.
	 * If it does not yet exist, it will be inserted.
	 * @param user The User to update or add.
	 * @return The id of the User in the Database.
	 */
	long updateDB(User user) {
		long ret;
		if (user.getId() == -1) ret = add(user);
		else ret = update(user);
		
		for (Artist artist : user.getSuggestedArtists()) {
			add(artist, user);
		}
		
		return ret;
	}
	
	/**
	 * Add a {@link Song} to the database.
	 * Also updates the id of the Song.
	 * @param song The {@link Song} to add.
	 * @return The id, the Song has been assigned.
	 */
	private long add(Song song) {
		long res = db.insert(DB.TB_SONG, null, getCV(song));
		song.setId(res);
		mf.add(song);
		return res;
	}
	
	/**
	 * Update the {@link Song} in the database.
	 * @param song The {@link Song} to update.
	 * @return The id of the Song.
	 */
	private long update(Song song) {
		String whereClause = "rowid = " + song.getId();
		return db.update(DB.TB_SONG, getCV(song), whereClause, null);
	}
	
	/**
	 * Add the {@link Artist} to the database.
	 * @param artist The {@link Artist} to insert.
	 * @return The id, the Song has been assigned.
	 */
	private long add(Artist artist) {
		long res = db.insert(DB.TB_ARTIST, null, getCV(artist));
		artist.setId(res);
		mf.add(artist);
		return res;
	}

	/**
	 * Update the {@link Artist} in the database.
	 * @param artist The {@link Artist} to update.
	 * @return The id of the Artist.
	 */
	private long update(Artist artist) {
		String whereClause = "rowid = " + artist.getId();	
		return db.update(DB.TB_ARTIST, getCV(artist), whereClause, null);
	}
	
	/**
	 * Add the {@link Tag} to the database.
	 * @param tag The {@link Tag} to insert.
	 * @return The id, the Tag has been assigned.
	 */
	private long add(Tag tag) {
		long res = db.insert(DB.TB_TAG, null, getCV(tag));
		tag.setId(res);
		mf.add(tag);
		return res;
	}

	/**
	 * Update the {@link Tag} in the database.
	 * @param tag The {@link Tag} to update.
	 * @return The id of the Tag.
	 */
	private long update(Tag tag) {
		String whereClause = "rowid = " + tag.getId();
		return db.update(DB.TB_TAG, getCV(tag), whereClause, null);
	}
	
	/**
	 * Add the {@link User} to the database.
	 * @param user The {@link User} to insert.
	 * @return The id, the User has been assigned.
	 */
	private long add(User user) {
		long res = db.insert(DB.TB_USER, null, getCV(user));
		user.setId(res);
		mf.add(user);
		return res;
	}
	
	/**
	 * Update the {@link User} in the database.
	 * @param user The {@link User} to update.
	 * @return The id of the User.
	 */
	private long update(User user) {
		String whereClause = "rowid = " + user.getId();
		return db.update(DB.TB_USER, getCV(user), whereClause, null);
	}
	
	/**
	 * Add the provided {@link Tag} to the provided {@link Song}.
	 * @param song The Song to add the Tag to.
	 * @param tag The Tag to add.
	 * @return The database id of the added relation.
	 */
	public void add(Song song, Tag tag) {
		//TODO: CHECK WHAT HAPPENS IF ATTEMPTING TO INSERT EXISTING RELATION
		if (song.getId() == -1) add(song);
		if (tag.getId() == -1) add(tag);
		db.insert(DB.TB_SONGTAGS, null, getCV(song, tag));
	}
	
	/**
	 * Add the provided {@link Tag} to the provided {@link Artist}.
	 * @param artist The Artist to add the Tag to.
	 * @param tag The tag to add.
	 * @return The database id of the added relation.
	 */
	public void add(Artist artist, Tag tag) {
		//TODO: CHECK WHAT HAPPENS IF ATTEMPTING TO INSERT EXISTING RELATION
		if (artist.getId() == -1) add(artist);
		if (tag.getId() == -1) add(tag);
		db.insert(DB.TB_ARTISTTAGS, null, getCV(artist, tag));		
	}
	
	public void add(Artist artist, User user) {
		//TODO: CHECK WHAT HAPPENS IF ATTEMPTING TO INSERT EXISTING RELATION
		if (artist.getId() == -1) add(artist);
		if (user.getId() == -1) add(user);
		db.insert(DB.TB_USERARTIST, null, getCV(artist, user));
	}
	
	/**
	 * Generate content values (for insertion and update) for the provided parameter objects.
	 * @param song The {@link Song} to generate {@link ContentValues} for.
	 * @return The {@link ContentValues} object holding the values.
	 */
	private ContentValues getCV(Song song) {
		ContentValues cv = new ContentValues(4);
		cv.put(DB.SONG_ARTIST, song.getArtist().getId()); //TODO: Fail-check
		cv.put(DB.SONG_TITLE, song.getTitle());
		cv.put(DB.SONG_HOST, song.getHost().getId());
		cv.put(DB.SONG_LOCATION, song.getLocation());
		
		return cv;
	}
	
	/**
	 * Generate content values (for insertion and update) for the provided parameter objects.
	 * @param artist The {@link Artist} to generate {@link ContentValues} for.
	 * @return The {@link ContentValues} object holding the values.
	 */
	private ContentValues getCV(Artist artist) {
		ContentValues cv = new ContentValues(1);
		cv.put(DB.ARTIST_NAME, artist.getName());
		return cv;
	}
	
	/**
	 * Generate content values (for insertion and update) for the provided parameter objects.
	 * @param tag The {@link Tag} to generate {@link ContentValues} for.
	 * @return The {@link ContentValues} object holding the values.
	 */
	private ContentValues getCV(Tag tag) {
		ContentValues cv = new ContentValues(1);
		cv.put(DB.TAG_NAME, tag.getTagName());
		return cv;
	}
	
	/**
	 * Generate content values (for insertion and update) for the provided parameter objects.
	 * @param user The {@link User} to generate {@link ContentValues} for.
	 * @return The {@link ContentValues} object holding the values.
	 */
	private ContentValues getCV(User user) {
		ContentValues cv = new ContentValues(1);
		cv.put(DB.USER_ADDRESS, user.getBtdeviceAddress());
		return cv;
	}
	
	/**
	 * Generate content values (for insertion and update) for the provided parameter objects.
	 * @param song The {@link Song} that is part of the Song-Tag relation to generate {@link ContentValues} for.
	 * @param tag The {@link Tag} that is part of the Song-Tag relation to generate {@link ContentValues} for.
	 * @return The {@link ContentValues} object holding the values.
	 */
	private ContentValues getCV(Song song, Tag tag) {
		ContentValues cv = new ContentValues(2);
		cv.put(DB.SONGTAG_SONG, song.getId());
		cv.put(DB.SONGTAG_TAG, tag.getId());
		return cv;
	}
	
	/**
	 * Generate content values (for insertion and update) for the provided parameter objects.
	 * @param artist The {@link Artist} that is part of the Artist-Tag relation to generate {@link ContentValues} for.
	 * @param tag The {@link Tag} that is part of the Artist-Tag relation to generate {@link ContentValues} for.
	 * @return The {@link ContentValues} object holding the values.
	 */
	private ContentValues getCV(Artist artist, Tag tag) {
		ContentValues cv = new ContentValues(2);
		cv.put(DB.ARTISTTAG_ARTIST, artist.getId());
		cv.put(DB.ARTISTTAG_TAG, tag.getId());
		return cv;
	}
	
	/**
	 * Generate ContentValues (for insertion and update) for the provided parameter objects.
	 * @param artist The {@link Artist} that is part of the Artist-User relation to generate {@link ContentValues} for.
	 * @param user The {@link User} that is part of the Artist-User relation to generate (@link ContentValues} for.
	 * @return The {@link ContentValues} object holding the values.
	 */
	private ContentValues getCV(Artist artist, User user) {
		ContentValues cv = new ContentValues(2);
		cv.put(DB.USERARTIST_ARTIST, artist.getId());
		cv.put(DB.USERARTIST_USER, user.getId());
		return cv;
	}
	
	/**
	 * Retrieve the {@link Song} with the provided id from the database.
	 * Returns null if the Song does not exist.
	 * @param songId The id to search for.
	 * @return The Song with matching id.
	 */
	Song getSong(long songId) {
		String[] cols = {DB.SONG_ARTIST, DB.SONG_TITLE, DB.SONG_HOST, DB.SONG_LOCATION};
		Cursor c = db.query(DB.TB_SONG, cols, "rowid = " + songId, null, null, null, null);
		
		if (!c.moveToFirst()) return null;
		
		long aId = c.getLong(0);
		long uId = c.getLong(2);
		String sTitle = c.getString(1);
		String sLoc = c.getString(3);
		
		c.close();
		
		Artist a = mf.getArtist(aId);
		User u = mf.getUser(uId);
		Song s = new Song(sTitle, a, u, sLoc, songTags(songId));
		s.setId(songId);
		return s;
	}

	/**
	 * Retrieve the {@link Artist} with the provided id from the Database.
	 * Returns null if the Artist does not exist.
	 * @param artistId Id to search for.
	 * @return Artist with matching id.
	 */
	Artist getArtist(long artistId) {
		String[] cols = {DB.ARTIST_NAME};
		Cursor c = db.query(DB.TB_ARTIST, cols, "rowid = " + artistId, null, null, null, null);
		
		if (!c.moveToFirst()) return null;
		String aName = c.getString(0);
		c.close();
		
		Artist a = new Artist(aName, artistTags(artistId));
		return a;
	}
	
	/**
	 * Retrieve the {@link Tag} with the provided id from the database.
	 * Returns null if the Tag does not exist.
	 * @param tagId The id to search for.
	 * @return The Tag matching the provided id.
	 */
	Tag getTag(long tagId) {
		String[] cols = {DB.TAG_NAME};
		Cursor c = db.query(DB.TB_TAG, cols, "rowid = " + tagId, null, null, null, null);
		
		if (!c.moveToFirst()) return null;
		String tName = c.getString(0);
		c.close();
		
		Tag t = new Tag(tName);
		return t;
	}
	
	User getUser(long userId) {
		String[] cols = {DB.USER_ADDRESS};
		Cursor c = db.query(DB.TB_USER, cols, "rowid = " + userId, null, null, null, null);
		
		if (!c.moveToFirst()) return null;
		String uAddr = c.getString(0);
		c.close();
		
		User u = new User(uAddr);
		return u;
	}

	/**
	 * Retrieve {@link Tag}s the {@link Song} of the provided id has applied.
	 * @param songId The id to find tags for.
	 * @return The Tags applied to the Song of the provided id.
	 */
	private Collection<Tag> songTags(long songId) {
		Collection<Tag> c = new HashSet<Tag>();
		
		String[] cols = {DB.SONGTAG_TAG};
		String selection = DB.SONGTAG_SONG + " = " + songId;
		Cursor cu = db.query(DB.TB_SONGTAGS, cols, selection, null, null, null, null);
		
		ArrayList<Long> tagIds = new ArrayList<Long>();
		
		while (cu.moveToNext()) {
			tagIds.add(cu.getLong(0));
		}
		cu.close();
		
		for (Long tagid : tagIds)
			c.add(mf.getTag(tagid));

		return c;
	}
	
	/**
	 * Retrieve {@link Tag}s the {@link Artist} of the provided id has applied.
	 * @param artistId The id to find tags for.
	 * @return The Tags applied to the Artist of the provided id.
	 */
	private Collection<Tag> artistTags(long artistId) {
		Collection<Tag> c = new HashSet<Tag>();
		
		String[] cols = {DB.ARTISTTAG_TAG};
		String selection = DB.ARTISTTAG_ARTIST + " = " + artistId;
		Cursor cu = db.query(DB.TB_ARTISTTAGS, cols, selection, null, null, null, null);
		
		ArrayList<Long> tagIds = new ArrayList<Long>();
		
		while (cu.moveToNext()) {
			tagIds.add(cu.getLong(0));
		}
		
		cu.close();
		
		for (Long tagId : tagIds)
			c.add(mf.getTag(tagId));
		
		return c;
	}
	
	/**
	 * Retrieve {@link Artist}s, the {@link User} of the provided id has been suggested.
	 * @param userId The id to find Artists for.
	 * @return The Artists suggested to the User of the provided id.
	 */
	private Collection<Artist> userArtists(long userId) {
		Collection<Artist> a = new HashSet<Artist>();
		String[] cols = {DB.USERARTIST_ARTIST};
		String selection = DB.USERARTIST_USER + " = " + userId;
		Cursor cu = db.query(DB.TB_USERARTIST, cols, selection, null, null, null, null);
		
		ArrayList<Long> artistIds = new ArrayList<Long>();
		
		while (cu.moveToNext()) {
			artistIds.add(cu.getLong(0));
		}
		
		cu.close();
		
		for (Long artistId : artistIds)
			a.add(mf.getArtist(artistId));
		
		return a;
	}
	
	/**
	 * Retreive all songs currently stored in the database.
	 * @return All songs stored in the database.
	 */
	ArrayList<Song> getSongs() {
		ArrayList<Song> songs = new ArrayList<Song>();
		String[] cols = {DB.SONG_ARTIST, DB.SONG_TITLE, DB.SONG_HOST, DB.SONG_LOCATION, "rowid"};
		Cursor cu = db.query(DB.TB_SONG, cols, null, null, null, null, null);
		
		ArrayList<Long> songIds = new ArrayList<Long>();
		
		HashMap<Long, Long> artistIds 	= new HashMap<Long, Long>();
		HashMap<Long, Long> userIds 	= new HashMap<Long, Long>();
		HashMap<Long, String> titles 	= new HashMap<Long, String>();
		HashMap<Long, String> locations = new HashMap<Long, String>();
		
		while (cu.moveToNext()) {
			Long l = cu.getLong(4);
			Song s = mf.songLoaded(l);
			if (s == null) {
				songIds.add(l);
				artistIds.put(l, cu.getLong(0));
				userIds.put(l, cu.getLong(2));
				titles.put(l, cu.getString(1));
				locations.put(l, cu.getString(3));
			} else {
				songs.add(s);
			}
		}
		
		cu.close();
		
		for (Long sid : songIds) {
			songs.add(new Song(titles.get(sid), mf.getArtist(artistIds.get(sid)), 
					mf.getUser(userIds.get(sid)), locations.get(sid), songTags(sid)));
		}
		
		return songs;
	}
	
	/**
	 * Retreive artists from the database.
	 * @return Artists from the database.
	 */
	ArrayList<Artist> getArtists() {
		ArrayList<Artist> artists = new ArrayList<Artist>();
		String[] cols = {DB.ARTIST_NAME, "rowid"};
		Cursor cu = db.query(DB.TB_ARTIST, cols, null, null, null, null, null);
		
		while(cu.moveToNext()) {
			Artist artist = mf.artistLoaded(cu.getLong(1));
			if (artist == null) {
				artist = new Artist(cu.getString(0));
				artist.setId(cu.getLong(1));
			}
			artists.add(artist);
		}
		//If other entities should be loaded during the above loop. 
		//Load data to temporary storage first, then close cursor, THEN assign to new Artist objects!
		
		cu.close();
		
		return artists;
	}
	
	/**
	 * Retreive users from the database.
	 * @return Users from the database.
	 */
	ArrayList<User> getUsers() {
		ArrayList<User> users = new ArrayList<User>();
		String[] cols = {DB.USER_ADDRESS, "rowid"};
		Cursor cu = db.query(DB.TB_USER, cols, null, null, null, null, null);
		
		ArrayList<Long> userIds = new ArrayList<Long>();
		ArrayList<String> userLocs = new ArrayList<String>();
		
		while (cu.moveToNext()) {
			Long uId = cu.getLong(1);
			User u = mf.getUser(uId);
			if (u != null) users.add(u);
			else {
				userIds.add(cu.getLong(1));
				userLocs.add(cu.getString(0));
			}
		}
		cu.close();
		
		for (int i = 0, s = userIds.size(); i < s; ++i) {
			long sid = userIds.get(i);
			User u = new User(userLocs.get(i), userArtists(sid));
			u.setId(sid);
			users.add(u);
		}
		
		return users;
	}
	
	/**
	 * Retrieve all tags from the database.
	 * @return Tags stored in the database.
	 */
	ArrayList<Tag> getTags() {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		String[] cols = {DB.TAG_NAME, "rowid"};
		Cursor cu = db.query(DB.TB_TAG, cols, null, null, null, null, null);
		
		while (cu.moveToNext()) {
			Tag tag = mf.tagLoaded(cu.getLong(1));
			if (tag == null) {
				tag = new Tag(cu.getString(0));
				tag.setId(cu.getLong(1));
			}
			tags.add(tag);
		}
		return tags;
	}
}
