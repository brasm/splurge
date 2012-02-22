package sw8.proto1;


public class Song {

	private String absPath;
	private String name;

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

	public Song(String name, String abspath) {
		this.name = name;
		this.absPath = abspath;
	}

	
}
