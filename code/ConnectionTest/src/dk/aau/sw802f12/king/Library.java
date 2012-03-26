package dk.aau.sw802f12.king;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Library {
	private List<Song> local = new ArrayList<Song>();
	private int elements = 0;

	public Song getRandomSong() {
		if (elements == 0)
			return null;
		Random r = new Random();
		int index = r.nextInt(elements);
		local.get(index);
		return local.get(index);
	}

	public Library(File path) {
		update(path);
	}

	public void update(File path) {
		if (!path.isDirectory())
			throw new IllegalArgumentException("Not a directory: "
					+ path.getAbsolutePath());

		for (File f : path.listFiles()) {
			if (f.isDirectory()) {
				update(f);
				continue;
			}
			try {
				local.add(new Song(f));
				elements++;
			} catch (IllegalSongFormatException e) {
				continue;
			}
		}
	}
}
