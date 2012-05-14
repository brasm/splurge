package dk.aau.sw802f12.musicplayer.util;

import java.util.Collection;
import java.util.HashSet;

/**
 * Wraps User MAC address, as well as {@link Artist}s suggested to the User. 
 * The id of the User is set to -1, if the User is not set in the database.
 * 
 * @author sw802f12 (mlisby)
 *
 */
public class User {
	/**
	 * The address of the bluetooth device. (Bluetooth adapter can instantiate a BluetoothDevice from this.)
	 */
	private String btdeviceAddress;
	/**
	 * The {@link Artist}s that has been suggested to the user.
	 */
	private HashSet<Artist> suggArtists;
	/**
	 * The id assigned to the User in the database.
	 */
	private long id;
	
	/**
	 * Create new User instance with suggested {@link Artist}s.
	 * @param btda The Bluetooth device address of the device.
	 * @param suggestedArtists {@link Artist}s suggested to the user.
	 */
	public User(String btda, Collection<Artist> suggestedArtists) {
		this(btda);
		setId(-1);
		suggArtists = new HashSet<Artist>(suggestedArtists);
	}
	
	/**
	 * Create new User instance.
	 * @param btda The Bluetooth device address of the device.
	 */
	public User(String btda) {
		setBtdeviceAddress(btda);
		setId(-1);
		suggArtists = new HashSet<Artist>();
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
	 * Retrieve {@link Artist}s, the user has been suggested.
	 * @return Artists suggested to the user.
	 */
	public Collection<Artist> getSuggestedArtists() {
		return suggArtists;
	}
	
	/**
	 * Add an {@link Artist} to the collection of suggested artists.
	 * @param artist The artist to add.
	 */
	public void addSuggestedArtist(Artist artist) {
		suggArtists.add(artist);
	}
	
	/**
	 * Remove an {@link Artist} from the collection of suggested artists.
	 * @param artist The artist to remove.
	 */
	public void removeSuggestedArtist(Artist artist) {
		suggArtists.remove(artist);
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
}
