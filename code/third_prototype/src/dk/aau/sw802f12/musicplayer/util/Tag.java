package dk.aau.sw802f12.musicplayer.util;

/**
 * Tags meta-classifies {@link Song} and {@link Artist}, and is used to group them together, 
 * allowing the recommender to suggest similar songs.
 * The id of the tag represents the database ID of the tag, and should only be modified by the database wrapper.
 * If the Tag is not added to the database, the Id is -1.
 * The tagName is a convenience representation of the Tag. This could, for instance, be "Rock", "Pop" or "Jazz".
 *  
 * @author sw802f12 (mlisby)
 *
 */
public class Tag {
	private String tagName;
	private long id;
	
	/**
	 * Create new Tag instance. The Id is, by default, set to -1.
	 * @param tag The name, the tag should carry.
	 */
	public Tag(String tag) {
		setTagName(tag);
		id = -1;
	}
	
	/**
	 * Retrieve the Tag name.
	 * @return The Tag name.
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Retrieve the ID, the tag is stored at in the database.
	 * If the tag is not yet created, this will return -1.
	 * @return The ID of the tag in the database.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Set the tag name.
	 * @param tag The name to set.
	 */
	public void setTagName(String tag) {
		this.tagName = tag;
	}
	
	/**
	 * Set the database id of the Tag.
	 * @param id The id to set.
	 */
	void setId(long id) {
		this.id = id;
	}
}
