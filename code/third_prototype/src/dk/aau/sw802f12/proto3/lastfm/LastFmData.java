package dk.aau.sw802f12.proto3.lastfm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dk.aau.sw802f12.proto3.Settings;

import android.util.Log;

public class LastFmData{
	private String _url = "http://ws.audioscrobbler.com/2.0/";
	private String _api = "d9934b94a48ffbd994097060b96cda17";
	
	/**
	 * 
	 * @author brian
	 *
	 */
	private class ArtistHandler implements XmlResponseHandler{
		public final String lastfmuser;
		
		public ArtistHandler(String user){
			lastfmuser = user;
		}	
		@Override	
		public void execute(Document xmlDocument) {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			int playCountIndex = 0;
			
			try {
				XPathExpression artists_expr = xpath.compile("//lfm/topartists/artist/name/text()");
				XPathExpression playcount_expr = xpath.compile("//lfm/topartists/artist/playcount/text()");
				
				NodeList artists = (NodeList) artists_expr.evaluate(xmlDocument, XPathConstants.NODESET);
				NodeList playcount = (NodeList) playcount_expr.evaluate(xmlDocument, XPathConstants.NODESET);
				
				if( artists.getLength() != playcount.getLength()){
					// something is wrong
					return;
				}

				for (int i = 0; i < artists.getLength(); i++) {
					String a = artists.item(i).getNodeValue();
					int p = Integer.parseInt(playcount.item(i).getNodeValue());
					
					if (playCountIndex == 0)
						playCountIndex = p;
					p = p * 100 / playCountIndex; 
					
					Log.d("LASTFM", a + ": " + p);
					//TODO: write result to database
					
					/* debug remove these */
					getTags(a);
					getSimilarArtists(a);
				}
			} catch (XPathExpressionException e) {
				Log.d("LASTFM","XPathExpressionException " + e.getMessage());
			}
		}
	}
	
	private class TagHandler implements  XmlResponseHandler {
		public final String artist;
		public final int maxTags = Settings.getInstance().getMaximumArtistTags();

		public TagHandler(String _artist){
			artist = _artist;
		}

		@Override	
		public void execute(Document xmlDocument) {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			try {
				XPathExpression tagname = xpath.compile("//lfm/toptags/tag/name/text()");
				Object tags = tagname.evaluate(xmlDocument, XPathConstants.NODESET);
				NodeList nodes = (NodeList) tags;
				for (int i = 0; i < nodes.getLength(); i++) {
					if (i > maxTags) return;
				    Log.d("LASTFM","> " + artist + ": " + nodes.item(i).getNodeValue());
				    //TODO: write result to database
				}
			} catch (XPathExpressionException e) {
				Log.d("LASTFM","XPathExpressionException " + e.getMessage());
			}
		}
	}
	
	private class SimilarArtistHandler implements  XmlResponseHandler {
		public final String artist;
		private final double threshold = Settings.getInstance().getSimilarArtistThreshold();
		
		public SimilarArtistHandler(String _artist){
			artist = _artist;
		}

		@Override	
		public void execute(Document xmlDocument) {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			try {
				XPathExpression artist_expr = xpath.compile("//lfm/similarartists/artist/name/text()");
				XPathExpression match_expr = xpath.compile("//lfm/similarartists/artist/match/text()");
				NodeList artists = (NodeList) artist_expr.evaluate(xmlDocument, XPathConstants.NODESET);
				NodeList match = (NodeList) match_expr.evaluate(xmlDocument, XPathConstants.NODESET);
				
				if( artists.getLength() != match.getLength()){
					// something is wrong
					return;
				}
				
				for (int i = 0; i < artists.getLength(); i++) {
					String a = artists.item(i).getNodeValue();
					Double m = Double.parseDouble(match.item(i).getNodeValue());
					if (m < threshold)
						return;
					// TODO: write result to database
				    Log.d("LASTFM","> " + a + " is similar to " + artist + " with a match on " + m); 
				}
			} catch (XPathExpressionException e) {
				Log.d("LASTFM","XPathExpressionException " + e.getMessage());
			}
		}
	}
	
	public void getTopArtists(String username){
		String u = urlencode(username);
		String uri = String.format("%s?method=user.gettopartists&user=%s&api_key=%s",_url,u,_api);
		new HttpRequest(uri, new ArtistHandler(username));
	}

	public void getTags(String artist){
		String a = urlencode(artist);
		String uri = String.format("%s?method=artist.gettoptags&artist=%s&api_key=%s&autocorrect=1",_url,a,_api);
		new HttpRequest(uri, new TagHandler(artist));
	}
	
	public void getSimilarArtists(String artist){
		String a = urlencode(artist);
		String uri = String.format("%s?method=artist.getsimilar&artist=%s&api_key=%s&autocorrect=1",_url,a,_api);
		new HttpRequest(uri, new SimilarArtistHandler(artist));
	}
	
	private String urlencode(String url){
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

