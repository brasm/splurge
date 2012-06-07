package dk.aau.sw802f12.splurge.library;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Vector;

import android.util.Log;

import dk.aau.sw802f12.splurge.Settings;
import dk.aau.sw802f12.splurge.database.Artist;
import dk.aau.sw802f12.splurge.database.Song;

public class PlayQueue {
	
	private Library mLibrary = Library.getInstance();
	private Vector<Song> mSongsPlayed = new Vector<Song>();
	private Vector<Artist> mArtistsPlayed = new Vector<Artist>();
	
	private void getSimilarArtists(Artist source, int d_zero, int d_max, Map<Artist,Integer> solution) {
		Map<Artist,Integer> neighbours = new HashMap<Artist,Integer>();
		// evaluate all neighbours
		HashMap<Artist, Short> similar;
		try {
			similar = source.getSimilarArtists();
		} catch (InstantiationException e) {
			return;
		}
		
		for(Map.Entry<Artist, Short> map : similar.entrySet()) {
			Artist a = map.getKey();
			int distance  = map.getValue() + d_zero;
			// dont add if distance to artist is to far 
			if(distance > d_max) continue;
			// no duplicate entries
			if(solution.containsKey(a))	continue;
			// dont add if artist was recently played 
			int listsize = Settings.getInstance().getRecentlyPlayedBlacklist();
			int start = mArtistsPlayed.size() - (listsize + 1); 
			start = start < 0 ? 0 : start;
			int end = mArtistsPlayed.size() - 1;
			List<Artist> recently = mArtistsPlayed.subList(start, end);
			if (recently.contains(a)) continue;
			// append candidate
			neighbours.put(a, distance);
		}
		// merge into solution space
		solution.putAll(neighbours);
		// find more candidates on next level
		for(Artist a: neighbours.keySet()){
			d_zero = solution.get(a);
			getSimilarArtists(a, d_zero, d_max, solution);
		}
	}
	
	private List<Artist> getCandidates(Artist nowPlaying){
		HashMap<Artist,Integer> artistSet = new HashMap<Artist,Integer>();
		Vector<Artist> result = new Vector<Artist>();
		getSimilarArtists(nowPlaying,0,5,artistSet);
		for(Artist a:  artistSet.keySet())
			if(mLibrary.hasArtist(a)) result.add(a);
		return result;
	}
	
	private Artist selectArtist(List<Artist> candidates){
		// TODO: replace with fancy recommender system that makes the perfect desicion	
		Random r = new Random();
		int size = candidates.size();
		if (size == 0) return null;	
		int index = r.nextInt(size - 1);
		return candidates.get(index);
	}
	
	public Song getNext(){
		//return mLibrary.getRandom();	
		Song nextSong = null;
		Artist lastArtist;
		Artist nextArtist;	
		try{
			lastArtist = mArtistsPlayed.lastElement();
			List<Artist> candidates = getCandidates(lastArtist);
			nextArtist = selectArtist(candidates);
			nextSong = mLibrary.getRandom(nextArtist);
		} 
		catch (NoSuchElementException e){}
		if (nextSong == null){
			nextSong = mLibrary.getRandom();
			Log.d("LASTFM", "no song, select random");
		}
		mArtistsPlayed.add(nextSong.getArtist());
		mSongsPlayed.add(nextSong);
		return nextSong;
	}
	
	private static PlayQueue mInstance = null;
	public static PlayQueue getInstance(){
		if( mInstance == null)
			mInstance = new PlayQueue();
		return mInstance;
	}
	
	private PlayQueue(){}
}
		
	


