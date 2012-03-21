package dk.aau.sw802f12.king;

import java.io.File;

public class Song {

	File song;

	public String getPath() {
		return song.getAbsolutePath();
	}

	public Song(File f) throws IllegalSongFormatException {
		if (!f.getAbsolutePath().toLowerCase()
				.matches(".*\\.(ogg|mp3|flac|wma)"))
			throw new IllegalSongFormatException();
		song = f.getAbsoluteFile();
	}
}
