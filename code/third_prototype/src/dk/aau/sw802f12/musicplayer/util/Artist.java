package dk.aau.sw802f12.musicplayer.util;

import java.util.Collection;
import java.util.HashSet;

/**
 * Wraps Artist info from the Database, as well as holds {@link Tag}s related to the Artist.
 * The id of the Artist is the Database id, it is assigned at database insertion. 
 * This should only be accessed by the Database wrapper.
 * 
 * @author sw802f12 (mlisby)
 *
 */
public class Artist {
	private String name;
	private HashSet<Tag> tags;
	private long id;
	
	/**
	 * 
	 * @param name
	 * @param tags
	 */
	public Artist(String name, Collection<Tag> tags) {
		setName(name);
		setId(-1);
		if (tags == null) tags = new HashSet<Tag>();
		
		this.tags = new HashSet<Tag>(tags);
	}
	
	public Artist(String name) {
		this(name, null);
	}

	/**
	 * Retrieve the artist name.
	 * @return The name of the artist.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Retrieve the database id of the IArtist.
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
	 * @return Set of {@link Tag} of the Artist.
	 */
	public HashSet<Tag> getTags() {
		return tags;
	}

	/**
	 * Add an {@link Tag} to the Artist's tags.  
	 * @param tag The {@link Tag} to add.
	 */
	public void addTag(Tag tag) {
		tags.add(tag);
	}
}