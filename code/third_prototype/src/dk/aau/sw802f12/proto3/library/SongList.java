package dk.aau.sw802f12.proto3.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.util.Log;

import dk.aau.sw802f12.proto3.database.Artist;
import dk.aau.sw802f12.proto3.database.Song;

public class SongList {
	public final boolean LOCAL;
	public final String NAME;
	private List<Song> asList;
	private HashMap<Artist,List<Song>> artistMap;
	
	public SongList(boolean local,String listname){
		LOCAL = local;
		NAME = listname;
		asList = new ArrayList<Song>();
		artistMap = new HashMap<Artist, List<Song>>();
	}
	
	public void put(Song s){
		Log.d("LASTFM", "Adding song to list: " + s.getLocation());
		asList.add(s);
		List<Song> songs = artistMap.get(s.getArtist());
		if(songs == null) songs = new Vector<Song>();
		songs.add(s);	
		artistMap.put(s.getArtist(), songs);
	}
	
	public Song getRandom(Artist a){
		List<Song> songs = artistMap.get(a);
		if(songs == null) return null;	
		int size = songs.size() - 1;
		Random r = new Random();
		int index = r.nextInt(size);
		return songs.get(index);
	}
	
	public Song getRandom(){
		Random r = new Random();
		int size = asList.size();
		if(size == 0){
			return null;
		}	
		return asList.get(r.nextInt(size));
	}
	
	public boolean containsArtist(Artist a){
		List<Song> s = artistMap.get(a);
		return s != null ? true : false;
	}
}
