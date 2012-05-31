package dk.aau.sw802f12.proto3.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import dk.aau.sw802f12.proto3.lastfm.LastFmData;

/**
 * Wraps Artist info from the Database, as well as holds {@link Tag}s related to the Artist.
 * The id of the Artist is the Database id, it is assigned at database insertion. 
 * This should only be accessed by the Database wrapper.
 * 
 * @author sw802f12
 *
 */
public class Artist {
	private volatile String name;
	private volatile long id;
	volatile HashSet<Tag> tags;
	volatile HashMap<User, Short> userRates;
	volatile HashMap<Artist, Short> similarArtists;
	
	/**
	 * Create a new Artist with the Artist name.
	 * @param name The name of the Artist.
	 */
	Artist(String name) {
		setName(name);
		setId(-1);
		if (tags == null) tags = new HashSet<Tag>();
		this.userRates = new HashMap<User, Short>();
		this.tags = new HashSet<Tag>();
	}

	/**
	 * Add a {@link User} to the list of Users who have a rating of the Artist.
	 * @param user The User to add.
	 * @param rating The provided Users rating of the Artist.
	 */
	public void addUser(User user, short rating) {
		user.ratedArtists.put(this, rating);
		userRates.put(user, rating);
	}
	
	/**
	 * Remove a {@link User} from the list of Users who have a rating of the Artist.
	 * Also removes the Artist from the User's list of Artists
	 * @param user The User to remove.
	 * @return The User's rating of the Artist.
	 */
	public short removeUser(User user) {
		user.ratedArtists.remove(this);
		return userRates.remove(user);
	}
	
	/**
	 * Retrieve users that have a rating for the Artist.
	 * @return All {@link User}s that have a rating for the Artist.
	 */
	public HashMap<User, Short> getUsers() {
		return userRates;
	}
	
	/**
	 * Add {@link User}s and their ratings of the Artist to the Artist list of users.
	 * Also adds the Artist to the Users with the provided rating.
	 * @param userRatings The Users and their rating of the artist.
	 */
	public void addUserRatings(HashMap<User,Short> userRatings) {
		for (User u : userRatings.keySet()) {
			u.addSuggestedArtist(this, userRatings.get(u));
		}
		userRates.putAll(userRatings);
	}
	
	/**
	 * Retrieve the rating of the Artist by the provided {@link User}.
	 * @param user The User to retrieve the rating of.
	 * @return The user Rating, or null.
	 */
	public Short getUserRating(User user) {
		return userRates.get(user);
	}
	
	/**
	 * Retrieve the artist name.
	 * @return The name of the artist.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Retrieve the database id of the Artist.
	 * @return The database id of the artist.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Set the name of the artist.
	 * @param n The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Set the id, the Artist is represented by in the database.
	 * Visibility is package on purpose - only DB wrapper should use this!
	 * @param id The id to set.
	 */
	void setId(long id) {
		this.id = id;
	}

	/**
	 * Retrieve {@link Tag}s that the Artist has been tagged with.
	 * @return Set of Tag of the Artist.
	 */
	public HashSet<Tag> getTags() {
		try {
			if (tags.isEmpty())	new LastFmData().getTags(this);
		} catch (InstantiationException e) { }
		return tags;
	}

	/**
	 * Add an {@link Tag} to the Artist's tags.  
	 * @param tag The Tag to add.
	 */
	public void addTag(Tag tag) {
		tag.artists.add(this);
		tags.add(tag);
	}
	
	/**
	 * Add several {@link Tag}s to the Artist's assigned tags.
	 * @param tags the Tags to add.
	 * @param tags
	 */
	public void addTags(Collection<Tag> tags) {
		for (Tag t : tags) {
			t.artists.add(this);
		}
		this.tags.addAll(tags);
	}
	
	public void removeTag(Tag tag) {
		tag.artists.remove(this);
		tags.remove(tag);
	}
	
	/**
	 * Retrieve Artists similar to this artist.
	 * @return The similar Artists, and the similarity with this Artist.
	 * @throws IllegalStateException If the Music registry was not instantiated with Context before calling.
	 */
	public HashMap<Artist, Short> getSimilarArtists() throws InstantiationException {
		if (similarArtists == null) {
			MusicRegistry mr = MusicRegistry.getInstance();
			initiateSimilar();
			mr.loadSimilarArtists(this);
			// only request similar artists if artist is in library
			if (mr.existsSongsByArtist(this) && similarArtists.size() == 0) {
				new LastFmData().getSimilarArtists(this);
				return new HashMap<Artist, Short>();
			}
		}
		return similarArtists;
	}
	
	/**
	 * Add an Artist similar to this.
	 * @param artist The Artist to add as similar.
	 * @param similarity The similarity between this and the provided Artist.
	 */
	public void addSimilarArtist(Artist artist, Short similarity) {
		initiateSimilar();
		MusicRegistry.instance.addSimilarArtist(this, artist, similarity);
		similarArtists.put(artist, similarity);
	}
	
	/**
	 * Instantiate the similarArtist map.
	 */
	private void initiateSimilar() {
		if (similarArtists == null) similarArtists = new HashMap<Artist, Short>();
	}

	/**
	 * Add a HashMap to the similar Artists.
	 * @param similarArtists The similar artists to add.
	 */
	public void addSimilarArtists(HashMap<Artist, Short> similarArtists) {
		initiateSimilar();
		MusicRegistry.instance.addSimilarArtist(this, similarArtists);
		for (Artist artist : similarArtists.keySet()) {
			similarArtists.put(artist, similarArtists.get(artist));
		}
	}
	
	/**
	 * Remove an Artist from the list of similar Artists.
	 * @param artist The Artist to remove.
	 */
	public void removeSimilarArtist(Artist artist) {
		MusicRegistry.instance.removeSimilarArtist(this, artist);
		similarArtists.remove(artist);
	}
}