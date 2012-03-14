package dk.aau.sw802f12.proto2;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class Library {
	
	private ArrayList<Song> local = new ArrayList<Song>();	
	public ArrayList<Song> getLocal(){
		return local;
	}
	
	public void updateLocal(File path){
		if (! path.isDirectory())
			throw new IllegalArgumentException("Not a directory: " + path.getAbsolutePath());
		
		for(File f : path.listFiles()){
			if( f.isDirectory()){
				updateLocal(f);
				continue;
			}
			
			if ( new MusicFilter().accept(f, f.getName())) {
				Song s = new Song(f.getAbsolutePath());
				if ( ! local.contains(s))
					local.add(s);
			}
		}
	}
	
	class MusicFilter implements FilenameFilter{
		public boolean accept(File dir, String name){
			return name.toLowerCase().matches(".*\\.(ogg|mp3|flac|wma)");	
		}
	}
}
