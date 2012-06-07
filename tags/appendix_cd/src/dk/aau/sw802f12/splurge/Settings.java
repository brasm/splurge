package dk.aau.sw802f12.splurge;

import java.io.File;

/**
 * Contains all settings that should be changeable within the app
 *
 * @author sw802f12
 */
public class Settings {
	private int libraryUpdateInterval = 60000;
	private File localMusicFolder = new File("/sdcard/Music");
	private String deviceName = "MyAndroidDevice";
	private int userInterfaceUpdateInterval = 500;
	private int httpThreads = 10;
	private double similarArtistThreshold = 0.4;
	private int maximumArtistTags = 3;
	private int recentlyPlayedBlacklist = 10;	
	private static Settings mInstance = null;
	public static Settings getInstance(){
		if( mInstance == null)
			mInstance = new Settings();
		return mInstance;
	}
	private Settings(){}

	
	public int getLibraryUpdateInterval() {
		return libraryUpdateInterval;
	}

	public void setLibraryUpdateInterval(int libraryUpdateInterval) {
		this.libraryUpdateInterval = libraryUpdateInterval;
	}
	
	public File getLocalMusicFolder() {
		return localMusicFolder;
	}

	public void setLocalMusicFolder(File localMusicFolder) {
		if (! localMusicFolder.isDirectory())
			throw new IllegalArgumentException();
		this.localMusicFolder = localMusicFolder;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getUserInterfaceUpdateInterval() {
		return userInterfaceUpdateInterval;
	}

	public void setUserInterfaceUpdateInterval(int userInterfaceUpdateInterval) {
		this.userInterfaceUpdateInterval = userInterfaceUpdateInterval;
	}

	public int getHttpThreads() {
		return httpThreads;
	}

	public void setHttpThreads(int httpThreads) {
		this.httpThreads = httpThreads;
	}

	public double getSimilarArtistThreshold() {
		return similarArtistThreshold;
	}

	public void setSimilarArtistThreshold(double similarArtistThreshold) {
		this.similarArtistThreshold = similarArtistThreshold;
	}

	public int getMaximumArtistTags() {
		return maximumArtistTags;
	}

	public void setMaximumArtistTags(int maximumArtistTags) {
		this.maximumArtistTags = maximumArtistTags;
	}
	public int getRecentlyPlayedBlacklist() {
		return recentlyPlayedBlacklist;
	}
	public void setRecentlyPlayedBlacklist(int recentlyPlayedBlacklist) {
		this.recentlyPlayedBlacklist = recentlyPlayedBlacklist;
	}

}
