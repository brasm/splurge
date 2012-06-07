package dk.aau.sw802f12.splurge.player;

import dk.aau.sw802f12.splurge.database.Song;


public interface Playback {
	// Playback Controls
	public abstract void play();

	public abstract void stop();

	public abstract void pause();

	public abstract void resume();

	public abstract void next();

	public abstract void toggle();

	// Playback Info 
	public abstract Song nowPlaying();

	public abstract int getDuration();

	public abstract int getCurrentPosition();

	public abstract int getProgress();
}
