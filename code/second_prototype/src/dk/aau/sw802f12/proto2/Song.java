package dk.aau.sw802f12.proto2;

import android.media.MediaMetadataRetriever;

public class Song {

	private String absPath;
	private String name;
	private String artist;
	private String album;
	private String tracknumber;

	public String getAbsPath() {
		return absPath;
	}

	public void setAbsPath(String absPath) {
		this.absPath = absPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getTracknumber() {
		return tracknumber;
	}

	public void setTracknumber(String tracknumber) {
		this.tracknumber = tracknumber;
	}

	public Song(String abspath) {
		this.absPath = abspath;
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(abspath);
		this.name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		this.artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		this.album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		this.tracknumber = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
	}

	
}
