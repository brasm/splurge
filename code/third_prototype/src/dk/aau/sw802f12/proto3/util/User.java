package dk.aau.sw802f12.proto3.util;

import java.util.HashMap;

/**
 * Wraps User MAC address, as well as {@link Artist}s suggested to the {@link User}. 
 * The id of the {@link User} is set to -1, if the {@link User} is not set in the database.
 * The user's rating of the {@link Artist} is stored in the suggArtists {@link HashMap}, with the {@link Artist} as key.
 * 
 * @author sw802f12 (mlisby)
 *
 */
public class User {
	/**
	 * The address of the bluetooth device. (Bluetooth adapter can instantiate a BluetoothDevice from this.)
	 */
	private String btdeviceAddress;
	
	private String lastfmName;
	/**
	 * The {@link Artist}s that has been suggested to the user.
	 */
	HashMap<Artist, Short> ratedArtists;
	/**
	 * The id assigned to the User in the database.
	 */
	private long id;
	
	/**
	 * Create new User instance with suggested {@link Artist}s.
	 * @param btda The Bluetooth device address of the device.
	 * @param suggestedArtists {@link Artist}s suggested to the user.
	 */
	User(String btda, String lastfmName) {
		setBtdeviceAddress(btda);
		setId(-1);
		setLastfmName(lastfmName);
		ratedArtists = new HashMap<Artist, Short>();
	}
		
	/**
	 * Set the Bluetooth device MAC address of the User.
	 * @param macaddr The MAC address to set.
	 */
	public void setBtdeviceAddress(String macaddr) {
		btdeviceAddress = macaddr.toUpperCase();	
	}
	
	/**
	 * Retrieve the Bluetooth device MAC address of the User.
	 * @return The MAC address of the device of the user.
	 */
	public String getBtdeviceAddress() {
		return btdeviceAddress;
	}
	
	/**
	 * Add an {@link Artist} to the collection of suggested artists.
	 * @param artist The artist to add.
	 */
	public void addSuggestedArtist(Artist artist, short rating) {
		ratedArtists.put(artist, rating);
		artist.userRates.put(this, rating);
	}
	
	/**
	 * Remove an {@link Artist} from the collection of suggested artists.
	 * @param artist The artist to remove.
	 * @return The rating of the removed artist.
	 */
	public Short removeSuggestedArtist(Artist artist) {
		artist.userRates.remove(this);
		return ratedArtists.remove(artist);
	}
	
	/**
	 * Retrieve all {@link Artist}s, the User has rated.
	 * @return Artists rated by the User.
	 */
	public HashMap<Artist, Short> getRatedArtists() {
		return ratedArtists;
	}
	
	/**
	 * Retrieve the User's rating of the provided {@link Artist}.
	 * @param artist The Artist to retrieve the rating of.
	 * @return The rating of the provided Artist.
	 */
	public Short getArtistRating(Artist artist) {
		return ratedArtists.get(artist);
	}
	
	/**
	 * Add {@link Artist}s and the User's rating of the Artists to the User's list of rated Artists.
	 * @param artistRatings The Artists and the User's rating.
	 */
	public void addArtistRatings(HashMap<Artist, Short> artistRatings) {
		for (Artist a : artistRatings.keySet()) {
			a.addUser(this, artistRatings.get(a));
		}
		ratedArtists.putAll(artistRatings);
	}
	
	/**
	 * The id assigned to the User in the database. (If the user is not existing in the database, this is -1).
	 * @return Database id of the User.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Set the id of the User, assigned in the database.
	 * @param id the id to set.
	 */
	void setId(long id) {
		this.id = id;
	}

	public String getLastfmName() throws IllegalAccessException {
		if (lastfmName == null) throw new IllegalAccessException();
		return lastfmName;
	}

	public void setLastfmName(String lastFMName) {
		this.lastfmName = lastFMName;
	}
}
