package dk.aau.sw802f12.proto3;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.media.MediaMetadataRetriever;

public class Song {
	private enum ID3 {
		ARTIST, ALBUM, PATH, TITLE
	}

	private File song;
	private Map<ID3,String> mId3;
	
	public String getAlbum(){
		return getMetadata(ID3.ALBUM);
	}
		
	public String getArtist(){
		return getMetadata(ID3.ARTIST);
	}
	
	public String getTitle() {
		return getMetadata(ID3.TITLE);	
	}
	
	public String getPath() {
		return song.getAbsolutePath();
	}
	
	public String getName(){
		return song.getName();
	}
	
	private String getMetadata(ID3 key){
		String data = mId3.get(key);
		if (data == null){
			extractId3();
			data = mId3.get(key);
		}
		return data;	
	}
	
	private void extractId3(){
		MediaMetadataRetriever mmdr = new MediaMetadataRetriever();
		mmdr.setDataSource(song.getAbsolutePath());
		
		String artist = mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		String album = mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		String title = mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		
		mId3.put(ID3.ARTIST, artist);
		mId3.put(ID3.ALBUM, album);
		mId3.put(ID3.TITLE, title);
	}
	
	public Song(String path) throws IllegalArgumentException{
		File f = new File(path);
		init(f);
	}

	public Song(File f) throws IllegalArgumentException{
		init(f);
	}
	
	private void init(File f) throws IllegalArgumentException{
		mId3 = new HashMap<Song.ID3, String>();
		String fileTypeRegex = ".*\\.(flac|ogg|oga|mp3|wma)";
		if (f.getName().toLowerCase().matches(fileTypeRegex))
			song = f.getAbsoluteFile();
		else
			throw new IllegalArgumentException();	
	}
}