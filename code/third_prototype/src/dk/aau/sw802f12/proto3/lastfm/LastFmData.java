package dk.aau.sw802f12.proto3.lastfm;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class LastFmData{
	private String _url = "http://ws.audioscrobbler.com/2.0/";
	private String _api = "d9934b94a48ffbd994097060b96cda17";
	
	
	public void getTopArtists(String username){
	
		String uri = String.format("%s?method=user.gettopartists&user=%s&api_key=%s",_url,username,_api);
		
		XmlResponseHandler handler = new XmlResponseHandler() {
			@Override
			public void execute(Document xmlDocument) {
				String artist = null;
				int artistIndex = 0;
				int divisor = 0;
			
				NodeList artists = xmlDocument.getElementsByTagName("artist");	
				for(int i = 0; i < artists.getLength(); ++i){
					
					// traverse childnodes, 
					Node element =  artists.item(i).getChildNodes().item(0);
					do {	
						if (element.getNodeName().equals("name"))
							artist = element.getTextContent();
						
						if (element.getNodeName().equals("playcount")){
							int playcount = Integer.parseInt(element.getTextContent());
							if ( divisor == 0 ) divisor = playcount;
							artistIndex = playcount*100 / divisor;
						}
						
						if (artist != null && artistIndex > 0) break;
					}
					while((element = element.getNextSibling()) != null);
					
					// create artist <-> preference object 			
					Log.d("LASTFM", artist + " : " + artistIndex);
					artist=null;
					artistIndex=0;
				}
			}
		};
		new HttpRequest(uri, handler);
	}

	public void getTags(String artist){
		String uri = String.format("%s?method=artist.gettoptags&artist=%s&api_key=%s",_url,artist,_api);	
		XmlResponseHandler handler = new XmlResponseHandler() {
			@Override
			public void execute(Document xmlDocument) {
				String tagname = null;
				int count = 0;
				int tagcount = 3;
				
				NodeList tags = xmlDocument.getElementsByTagName("tag");	
				for(int i = 0; i < tags.getLength(); ++i){
					
					// traverse childnodes, 
					Node element =  tags.item(i).getChildNodes().item(0);
					do {	
						if (element.getNodeName().equals("name"))
							tagname = element.getTextContent();
						
						if (element.getNodeName().equals("count"))
							count = Integer.parseInt(element.getTextContent());
					}
					while((element = element.getNextSibling()) != null);
					Log.d("LASTFM", " > " + tagname + " : " + count);
				}
			}
		};
		new HttpRequest(uri, handler);
	}
	
	/*
	public void getRecommendedArtists(String username){
		// requires user authentification, implement later
	}
	*/
	
	
	
	
}
