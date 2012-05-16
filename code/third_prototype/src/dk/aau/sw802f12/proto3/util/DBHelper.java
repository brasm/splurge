package dk.aau.sw802f12.proto3.util;

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
class DBHelper extends SQLiteOpenHelper {
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
		 * Rating column of the UserArtist relation table.
		 */
		private static final String USERARTIST_RATING = "rating";
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
									+ DB.USERARTIST_RATING + "INTEGER, "
									+ "PRIMARY KEY(" + DB.USERARTIST_ARTIST + "," + DB.USERARTIST_USER + "))";
	}
		
	private SQLiteDatabase db;
	private MusicRegistry mf;
	
	public DBHelper(Context context) {
		super(context, DB.NAME, null, DB.VERSION);
		mf = MusicRegistry.getInstance(context);
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
		//onUpgrade(db, 0,0);
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
	 * If the {@link Song} already exists in the database, the Song is updated.
	 * If it does not yet exist, it will be inserted.
	 * @param song The Song to insert or update.
	 * @return The id of the Song in the database.
	 */
	long updateDB(Song song) {
		removeRelations(song);
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
	 * @param artist The Artist to insert or update.
	 * @return The id of the Artist in the database.
	 */
	long updateDB(Artist artist) {
		removeRelations(artist);
		long ret;
		if (artist.getId() == -1) ret = add(artist);
		else ret = update(artist);
		
		HashMap<User, Short> hm = artist.getUsers();
		for (User user : hm.keySet()) {
			add(artist, user, hm.get(user));
		}
		
		for (Tag tag : artist.getTags()) {
			add(artist, tag);
		}
		
		return ret;
	}
	
	/**
	 * Update database with the provided {@link Tag}.
	 * If the {@link Tag} already exists in the database, the Tag is updated.
	 * If it does not yet exist, it will be inserted.
	 * @param tag The Tag to update or add.
	 * @return The id of the Tag in the Database.
	 */
	long updateDB(Tag tag) {
		removeRelations(tag);
		
		long tId = (tag.getId() == -1) ? add(tag) : update(tag);
		
		for (Artist artist : tag.getTaggedArtists()) {
			add(artist, tag);
		}
		
		for (Song song : tag.getTaggedSongs()) {
			add(song, tag);
		}
		return tId;
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
		removeRelations(user);
		if (user.getId() == -1) ret = add(user);
		else ret = update(user);
		
		HashMap<Artist, Short> hm = user.getRatedArtists();
		for (Artist artist : hm.keySet()) {
			add(artist, user, hm.get(artist));
		}
		
		return ret;
	}
	
	/**
	 * Add a {@link Song} to the database.
	 * Also updates the id of the Song.
	 * @param song The Song to add.
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
	 * @param song The Song to update.
	 * @return The id of the Song.
	 */
	private long update(Song song) {
		String whereClause = "rowid = " + song.getId();
		return db.update(DB.TB_SONG, getCV(song), whereClause, null);
	}
	
	/**
	 * Add the {@link Artist} to the database.
	 * @param artist The Artist to insert.
	 * @return The id, the Artist has been assigned.
	 */
	private long add(Artist artist) {
		long res = db.insert(DB.TB_ARTIST, null, getCV(artist));
		artist.setId(res);
		mf.add(artist);
		return res;
	}

	/**
	 * Update the {@link Artist} in the database.
	 * @param artist The Artist to update.
	 * @return The id of the Artist.
	 */
	private long update(Artist artist) {
		String whereClause = "rowid = " + artist.getId();	
		return db.update(DB.TB_ARTIST, getCV(artist), whereClause, null);
	}
	
	/**
	 * Add the {@link Tag} to the database.
	 * @param tag The Tag to insert.
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
	 * @param tag The Tag to update.
	 * @return The id of the Tag.
	 */
	private long update(Tag tag) {
		String whereClause = "rowid = " + tag.getId();
		return db.update(DB.TB_TAG, getCV(tag), whereClause, null);
	}
	
	/**
	 * Add the {@link User} to the database.
	 * @param user The User to insert.
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
	 * @param user The User to update.
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
	void add(Song song, Tag tag) {
		//TODO: CHECK WHAT HAPPENS IF ATTEMPTING TO INSERT EXISTING RELATION
		if (song.getId() == -1) add(song);
		if (tag.getId() == -1) add(tag);
		db.insert(DB.TB_SONGTAGS, null, getCV(song, tag));
	}
	
	/**
	 * Add the provided {@link Tag} to the provided {@link Artist}.
	 * @param artist The Artist to add the Tag to.
	 * @param tag The Tag to add.
	 * @return The database id of the added relation.
	 */
	void add(Artist artist, Tag tag) {
		//TODO: CHECK WHAT HAPPENS IF ATTEMPTING TO INSERT EXISTING RELATION
		if (artist.getId() == -1) add(artist);
		if (tag.getId() == -1) add(tag);
		db.insert(DB.TB_ARTISTTAGS, null, getCV(artist, tag));		
	}
	
	/**
	 * Add the provided {@link Artist}-{@link User} relation, with the provided rating.
	 * @param artist The Artist of the Artist-User relation.
	 * @param user The User of the Artist-User relation.
	 * @param rating The rating of the Artist-User relation
	 */
	void add(Artist artist, User user, short rating) {
		//TODO: CHECK WHAT HAPPENS IF ATTEMPTING TO INSERT EXISTING RELATION
		if (artist.getId() == -1) add(artist);
		if (user.getId() == -1) add(user);
		db.insert(DB.TB_USERARTIST, null, getCV(artist, user, rating));
	}
	
	/**
	 * Generate {@link ContentValues} (for insertion and update) for the provided {@link Song}.
	 * @param song The Song to generate ContentValues for.
	 * @return The ContentValues object holding the values.
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
	 * Generate {@link ContentValues} (for insertion and update) for the provided {@link Artist}.
	 * @param artist The Artist to generate ContentValues for.
	 * @return The ContentValues object holding the values.
	 */
	private ContentValues getCV(Artist artist) {
		ContentValues cv = new ContentValues(1);
		cv.put(DB.ARTIST_NAME, artist.getName());
		return cv;
	}
	
	/**
	 * Generate {@link ContentValues} (for insertion and update) for the provided {@link Tag}.
	 * @param tag The Tag to generate ContentValues for.
	 * @return The ContentValues object holding the values.
	 */
	private ContentValues getCV(Tag tag) {
		ContentValues cv = new ContentValues(1);
		cv.put(DB.TAG_NAME, tag.getTagName());
		return cv;
	}
	
	/**
	 * Generate {@link ContentValues} (for insertion and update) for the provided {@link User}.
	 * @param user The User to generate ContentValues for.
	 * @return The ContentValues object holding the values.
	 */
	private ContentValues getCV(User user) {
		ContentValues cv = new ContentValues(1);
		cv.put(DB.USER_ADDRESS, user.getBtdeviceAddress());
		return cv;
	}
	
	/**
	 * Generate {@link ContentValues} (for insertion and update) for the provided {@link Song}-{@link Tag} relation.
	 * @param song The Song that is part of the Song-Tag relation to generate ContentValues for.
	 * @param tag The Tag that is part of the Song-Tag relation to generate ContentValues for.
	 * @return The ContentValues object holding the values.
	 */
	private ContentValues getCV(Song song, Tag tag) {
		ContentValues cv = new ContentValues(2);
		cv.put(DB.SONGTAG_SONG, song.getId());
		cv.put(DB.SONGTAG_TAG, tag.getId());
		return cv;
	}
	
	/**
	 * Generate {@link ContentValues} (for insertion and update) for the provided {@link Artist}-{@link Tag} relation.
	 * @param artist The Artist that is part of the Artist-Tag relation to generate ContentValues for.
	 * @param tag The Tag that is part of the Artist-Tag relation to generate ContentValues for.
	 * @return The ContentValues object holding the values.
	 */
	private ContentValues getCV(Artist artist, Tag tag) {
		ContentValues cv = new ContentValues(2);
		cv.put(DB.ARTISTTAG_ARTIST, artist.getId());
		cv.put(DB.ARTISTTAG_TAG, tag.getId());
		return cv;
	}
	
	/**
	 * Generate {@link ContentValues} (for insertion and update) for the {@link Artist}-{@link User} relation.
	 * @param artist The Artist that is part of the Artist-User relation to generate ContentValues for.
	 * @param user The User that is part of the Artist-User relation to generate ContentValues for.
	 * @param rating The user rating of the artist.
	 * @return The ContentValues object holding the values.
	 */
	private ContentValues getCV(Artist artist, User user, short rating) {
		ContentValues cv = new ContentValues(2);
		cv.put(DB.USERARTIST_ARTIST, artist.getId());
		cv.put(DB.USERARTIST_USER, user.getId());
		cv.put(DB.USERARTIST_RATING, rating);
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
		Song s = new Song(sTitle, a, u, sLoc);
		songTags(s);
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
		
		Artist a = new Artist(aName);
		artistTags(a);
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
		taggedSongs(t);
		taggedArtists(t);
		return t;
	}
	
	/**
	 * Retrieve the {@link User} with the provided id from the database.
	 * Returns null if the User does not exist.
	 * @param userId The id to search for.
	 * @return The User matching the provided id.
	 */
	User getUser(long userId) {
		String[] cols = {DB.USER_ADDRESS, "rowid"};
		Cursor c = db.query(DB.TB_USER, cols, "rowid = " + userId, null, null, null, null);
		
		if (!c.moveToFirst()) return null;
		String uAddr = c.getString(0);
		long rid = c.getLong(1);
		c.close();
		
		User u = new User(uAddr);
		u.setId(rid);
		userArtists(u);
		return u;
	}

	/**
	 * Add applied {@link Tag}s to the provided {@link Song}.
	 * @param song The Song to find Tags for.
	 * @return The Tags applied to the provided Song.
	 */
	private void songTags(Song s) {
		Collection<Tag> c = new HashSet<Tag>();
		
		String[] cols = {DB.SONGTAG_TAG};
		String selection = DB.SONGTAG_SONG + " = " + s.getId();
		Cursor cu = db.query(DB.TB_SONGTAGS, cols, selection, null, null, null, null);
		
		ArrayList<Long> tagIds = new ArrayList<Long>();
		
		while (cu.moveToNext()) {
			tagIds.add(cu.getLong(0));
		}
		cu.close();
		
		for (Long tagid : tagIds)
			c.add(mf.getTag(tagid));

		s.tag(c);
	}
	
	/**
	 * Add {@link Tag}s to the {@link Artist}, they have been applied to.
	 * @param artist The Artist to find Tags for.
	 */
	private void artistTags(Artist artist) {
		Collection<Tag> c = new HashSet<Tag>();
		
		String[] cols = {DB.ARTISTTAG_TAG};
		String selection = DB.ARTISTTAG_ARTIST + " = " + artist.getId();
		Cursor cu = db.query(DB.TB_ARTISTTAGS, cols, selection, null, null, null, null);
		
		ArrayList<Long> tagIds = new ArrayList<Long>();
		
		while (cu.moveToNext()) {
			tagIds.add(cu.getLong(0));
		}
		
		cu.close();
		
		for (Long tagId : tagIds)
			c.add(mf.getTag(tagId));
		
		artist.addTags(c);
	}
	
	/**
	 * Add {@link Artist}s, the provided {@link User} has been suggested, to the User.
	 * @param user The User to find Artists for.
	 */
	private void userArtists(User user) {
		HashMap<Artist, Short> hm = new HashMap<Artist, Short>();
		String[] cols = {DB.USERARTIST_ARTIST, DB.USERARTIST_RATING};
		String selection = DB.USERARTIST_USER + " = " + user.getId();
		Cursor cu = db.query(DB.TB_USERARTIST, cols, selection, null, null, null, null);
		
		ArrayList<Long> artistIds = new ArrayList<Long>();
		ArrayList<Short> ratings = new ArrayList<Short>();
		
		while (cu.moveToNext()) {
			artistIds.add(cu.getLong(0));
			ratings.add(cu.getShort(1));
		}
		
		cu.close();
		
		for (int i = 0, s = artistIds.size(); i < s; ++i)
			hm.put(mf.getArtist(artistIds.get(i)), ratings.get(i));
		
		user.addArtistRatings(hm);
	}
	
	/**
	 * Add {@link Song}s to the {@link Tag}s list of Songs.
	 * @param tag The Tag to add Songs to.
	 */
	private void taggedSongs(Tag tag) {
		Collection<Song> songs = new HashSet<Song>();
		
		String[] cols = {DB.SONGTAG_SONG};
		String select = DB.SONGTAG_TAG + "=" + tag.getId();
		Cursor c = db.query(DB.TB_SONGTAGS, cols, select, null, null, null, null);
		
		ArrayList<Long> songIds = new ArrayList<Long>();
		while (c.moveToNext()) {
			songIds.add(c.getLong(0));
		}
		c.close();
		
		for (long l : songIds) {
			songs.add(mf.getSong(l));
		}		
		tag.tagSongs(songs);
	}
	
	/**
	 * Add {@link Artist}s to the {@link Tag}s list of artists.
	 * @param tag The Tag to retrieve Artists for.
	 */
	private void taggedArtists(Tag tag) {
		Collection<Artist> artists = new HashSet<Artist>();
		
		String[] cols = {DB.ARTISTTAG_ARTIST};
		String select = DB.ARTISTTAG_TAG + "=" + tag.getId();
		Cursor c = db.query(DB.TB_ARTISTTAGS, cols, select, null, null, null, null);
		
		ArrayList<Long> artistIds = new ArrayList<Long>();
		while (c.moveToNext()) {
			artistIds.add(c.getLong(0));
		}
		c.close();
		
		for (long l : artistIds) {
			artists.add(mf.getArtist(l));
		}
		tag.tagArtists(artists);
	}
	
	/**
	 * Add {@link User}s that have rated an {@link Artist}, and the rating, to the Artist.
	 * @param artist The Artist to retrieve Users for.
	 * @return A HashMap of Users and the User's rating of the Artist.
	 */
	private void userArtists(Artist artist) {
		HashMap<User, Short> urat = new HashMap<User, Short>();
		String[] cols = {DB.USERARTIST_USER, DB.USERARTIST_RATING};
		String select = DB.USERARTIST_ARTIST + "=" + artist.getId();
		Cursor c = db.query(DB.TB_USERARTIST, cols, select, null, null, null, null);
		
		HashMap<Long, Short> uIdRat = new HashMap<Long, Short>();
		while(c.moveToNext()) {
			uIdRat.put(c.getLong(0), c.getShort(1));
		}
		c.close();
		
		for (Long l : uIdRat.keySet()) {
			User u = mf.getUser(l);
			urat.put(u, uIdRat.get(l));
		}
		
		artist.addUserRatings(urat);
	}
	
	/**
	 * Retreive all {@link Song}s currently stored in the database.
	 * @return All Songs stored in the database.
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
			} else songs.add(s);
		}
		
		cu.close();
		
		for (Long sid : songIds) {
			Song s = new Song(titles.get(sid), mf.getArtist(artistIds.get(sid)), 
					mf.getUser(userIds.get(sid)), locations.get(sid));
			songTags(s);
			s.setId(sid);
		}
		
		return songs;
	}
	
	/**
	 * Retreive {@link Artist}s from the database.
	 * @return Artists from the database.
	 */
	ArrayList<Artist> getArtists() {
		ArrayList<Artist> artists = new ArrayList<Artist>();
		String[] cols = {DB.ARTIST_NAME, "rowid"};
		Cursor cu = db.query(DB.TB_ARTIST, cols, null, null, null, null, null);
		
		HashMap<Long, String> hm = new HashMap<Long, String>();
		while(cu.moveToNext()) {
			Artist artist = mf.artistLoaded(cu.getLong(1));
			if (artist == null) {
				hm.put(cu.getLong(1), cu.getString(0));
			} else {
				artists.add(artist);
			}
		}
		cu.close();
		
		for (Long l : hm.keySet()) {
			Artist a = new Artist(hm.get(l));
			a.setId(l);
			artistTags(a);
			userArtists(a);
			artists.add(a);
		}
		
		return artists;
	}
	
	/**
	 * Retreive {@link User}s from the database.
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
			User user = mf.userLoaded(uId);

			if (user != null) users.add(user);
			else {
				userIds.add(cu.getLong(1));
				userLocs.add(cu.getString(0));
			}
		}
		cu.close();
		
		for (int i = 0, s = userIds.size(); i < s; ++i) {
			long sid = userIds.get(i);
			User u = new User(userLocs.get(i));
			userArtists(u);
			u.setId(sid);
			userArtists(u);
			users.add(u);
		}
		
		return users;
	}
	
	/**
	 * Retrieve {@link Tag}s from the database.
	 * @return Tags stored in the database.
	 */
	ArrayList<Tag> getTags() {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		String[] cols = {"rowid", DB.TAG_NAME};
		Cursor cu = db.query(DB.TB_TAG, cols, null, null, null, null, null);
		
		HashMap<Long, String> ids = new HashMap<Long, String>();
		while (cu.moveToNext()) {
			Tag t = mf.tagLoaded(cu.getLong(0));
			if (t == null) ids.put(cu.getLong(0), cu.getString(1));
			else tags.add(t);
			
		}
		cu.close();
		
		for (long l : ids.keySet()) {
			Tag t = new Tag(ids.get(l));
			t.setId(l);
			taggedSongs(t);
			taggedArtists(t);
			tags.add(t);
		}
				
		return tags;
	}
	
	/**
	 * Remove the coupling betweeen {@link User}s and {@link Artist}s in the database.
	 * This method clears ALL Song relations in the database, regardless of whether they are associated to the Song object or not.
	 * @param user The User to clear relations on.
	 */
	private void removeRelations(User user) {
		db.delete(DB.TB_USERARTIST, DB.USERARTIST_USER + "=" + user.getId(), null);
	}
	
	/**
	 * Remove the coupling between {@link Artist}s and {@link Tag}s or {@link User}s in the database.
	 * This method clears ALL Artist relations in the database, regardless of whether they are associated to the Artist object or not.
	 * @param artist The Artist to clear relations on.
	 */
	private void removeRelations(Artist artist) {
		db.delete(DB.TB_ARTISTTAGS, DB.ARTISTTAG_ARTIST + "=" + artist.getId(), null);
		db.delete(DB.TB_USERARTIST, DB.USERARTIST_ARTIST + "=" + artist.getId(), null);
	}
	
	/**
	 * Remove the coupling between {@link Tag}s and {@link Artist}s or {@link Song}s.
	 * This method clears ALL Tag relations in the database, regardless of whether they are associated to the Tag object or not.
	 * @param tag The Tag to clear relations on.
	 */
	private void removeRelations(Tag tag) {
		db.delete(DB.TB_ARTISTTAGS, DB.ARTISTTAG_TAG + "=" + tag.getId(), null);
		db.delete(DB.TB_SONGTAGS, DB.SONGTAG_TAG + "=" + tag.getId(), null);
	}
	
	/**
	 * Remove the coupling between {@link Song}s and {@link Tag}s in the database.
	 * This method clears ALL Song relations in the database, regardless of whether they are associated to the Song object or not.
	 * @param song The Song to clear relations on.
	 */
	private void removeRelations(Song song) {
		db.delete(DB.TB_SONGTAGS, DB.SONGTAG_SONG + "=" + song.getId(), null);
	}
	
	Artist searchName(String name) {
		String[] cols = {"rowid"};
		String[] selectionArg = {name};
		Cursor c = db.query(DB.TB_ARTIST, cols, DB.ARTIST_NAME + "= ?", selectionArg, null, null, null, null);
		
		long aId = -1;
		if (c.moveToFirst()) {
			aId = c.getLong(0);
		}
		c.close();
		
		return mf.getArtist(aId);		
	}
}
