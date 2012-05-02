package dk.aau.sw802f12.proto3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SongList {
	public final boolean LOCAL;
	public final String NAME;
	
	private Map<String,Song> byArtist;
	private Map<String,Song> byAlbum;
	private Map<String,Song> byPath;
	private List<Song> asList;
	
	public SongList(boolean local,String listname){
		LOCAL = local;
		NAME = listname;
		byArtist = new HashMap<String, Song>();
		byAlbum = new HashMap<String, Song>();
		byPath = new HashMap<String, Song>();
		asList = new ArrayList<Song>();
	}
	
	public void put(Song s){
		byArtist.put(s.getArtist(), s);
		byAlbum.put(s.getAlbum(), s);
		byPath.put(s.getPath(), s);
		asList.add(s);
	}
	
	public Song getRandom(){
		Random r = new Random();
		int size = asList.size();
		if(size == 0){
			return null;
		}
			
		return asList.get(r.nextInt(size));
	}
}
