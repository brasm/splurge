package dk.aau.sw802f12.proto3.lastfm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import dk.aau.sw802f12.proto3.Settings;
import dk.aau.sw802f12.proto3.util.Artist;
import dk.aau.sw802f12.proto3.util.MusicRegistry;
import dk.aau.sw802f12.proto3.util.Tag;
import dk.aau.sw802f12.proto3.util.User;

import android.util.Log;

/*
 * Request data from last.fm, and write it to local database
 */
public class LastFmData{
	private String _url = "http://ws.audioscrobbler.com/2.0/";
	private String _api = "d9934b94a48ffbd994097060b96cda17";
	private MusicRegistry _registry;

	/**
	 * Asynchronously, send request for users top artist to last.fm.
	 * Callback to ArtistHandler
	 * 
	 * @param user
	 */
	public void getTopArtists(User user){
		String u;
		try {
			u = urlencode(user.getLastfmName());
		} catch (IllegalAccessException e) {
			return;
		}
		String uri = String.format("%s?method=user.gettopartists&user=%s&api_key=%s",_url,u,_api);
		new HttpRequest(uri, new ArtistHandler(user));
	}

	/**
	 * Asynchronously, send request for an artists tags.
	 * Callback to TagHandler
	 * 
	 * @param user
	 */
	public void getTags(Artist artist){
		String a = urlencode(artist.getName());
		String uri = String.format("%s?method=artist.gettoptags&artist=%s&api_key=%s&autocorrect=1",_url,a,_api);
		new HttpRequest(uri, new TagHandler(artist));
	}
	

	/**
	 * Asynchronously, send request for similar artists.
	 * Callback to SimilarArtistHandler
	 * 
	 * @param user
	 */
	public void getSimilarArtists(Artist artist){
		String a = urlencode(artist.getName());
		String uri = String.format("%s?method=artist.getsimilar&artist=%s&api_key=%s&autocorrect=1",_url,a,_api);
		new HttpRequest(uri, new SimilarArtistHandler(artist));
	}
	
	private String urlencode(String url){
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {}
		return null;
	}
	
	public LastFmData() throws InstantiationException {
		_registry = MusicRegistry.getInstance();
	}
	
	/*
	 *  XmlResponseHandler's
	 */
	private class ArtistHandler implements XmlResponseHandler{
		public final User lastfmuser;
		
		public ArtistHandler(User user){
			lastfmuser = user;
		}	
		@Override	
		public void execute(Document xmlDocument) {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			NodeList artists;
			NodeList playcount;
			int playCountIndex = 0;

			try {
				XPathExpression artists_expr = xpath.compile("//lfm/topartists/artist/name/text()");
				XPathExpression playcount_expr = xpath.compile("//lfm/topartists/artist/playcount/text()");
				
				artists = (NodeList) artists_expr.evaluate(xmlDocument, XPathConstants.NODESET);
				playcount = (NodeList) playcount_expr.evaluate(xmlDocument, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				Log.d("LASTFM","XPathExpressionException " + e.getMessage());
				return;
			}

			HashMap<Artist,Short> topArtists = new HashMap<Artist, Short>();
			for (int i = 0; i < artists.getLength(); i++) {
				String artistname = artists.item(i).getNodeValue();
				int p = Integer.parseInt(playcount.item(i).getNodeValue());
				
				if (playCountIndex == 0)
					playCountIndex = p;
				p = p * 100 / playCountIndex; 
				
				Artist a = _registry.createArtist(artistname);
				topArtists.put(a,(short) p);		
			}
			lastfmuser.addArtistRatings(topArtists);
			_registry.updateDB(lastfmuser);
		}
	}
	
	private class TagHandler implements  XmlResponseHandler {
		public final Artist artist;
		public final int maxTags = Settings.getInstance().getMaximumArtistTags();

		public TagHandler(Artist _artist){
			artist = _artist;
		}

		@Override	
		public void execute(Document xmlDocument) {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			NodeList tags;
			
			try {
				XPathExpression tagname = xpath.compile("//lfm/toptags/tag/name/text()");
				tags = (NodeList) tagname.evaluate(xmlDocument, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				Log.d("LASTFM","XPathExpressionException " + e.getMessage());
				return;
			}
			
			for (int i = 0; i < tags.getLength(); i++) {
				if (i > maxTags) break;
			    String tag = tags.item(i).getNodeValue();
			    Tag t = _registry.createTag(tag);
			    artist.addTag(t);
			}
			_registry.updateDB(artist);
		}
	}
	
	private class SimilarArtistHandler implements  XmlResponseHandler {
		public final Artist artist;
		private final double threshold = Settings.getInstance().getSimilarArtistThreshold();
		
		public SimilarArtistHandler(Artist _artist){
			artist = _artist;
		}

		@Override	
		public void execute(Document xmlDocument) {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			NodeList artists;
			NodeList match;
			
			try {
				XPathExpression artist_expr = xpath.compile("//lfm/similarartists/artist/name/text()");
				XPathExpression match_expr = xpath.compile("//lfm/similarartists/artist/match/text()");
				artists = (NodeList) artist_expr.evaluate(xmlDocument, XPathConstants.NODESET);
				match = (NodeList) match_expr.evaluate(xmlDocument, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				Log.d("LASTFM","XPathExpressionException " + e.getMessage());
				return;
			}
				
			for (int i = 0; i < artists.getLength(); i++) {
				String a = artists.item(i).getNodeValue();
				Double m = Double.parseDouble(match.item(i).getNodeValue());
				if (m < threshold) continue;
				
				Artist similar = _registry.createArtist(a);
				short similarity = (short) (m * 100);
				similar.addSimilarArtist(artist, similarity);
				artist.addSimilarArtist(similar, similarity);
				_registry.updateDB(similar);
			}
			_registry.updateDB(artist);
		}
	}
		
}

