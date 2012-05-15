package dk.aau.sw802f12.proto3.lastfm;


import java.util.concurrent.Semaphore;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import dk.aau.sw802f12.proto3.Settings;

import android.util.Log;

public class HttpClient{
	
	// limit the amount of concurrent http requests to a number specified by
	// this semaphore. 
	private Semaphore _pool;

	private static HttpClient _instance = null;
	public static final HttpClient getInstance(){
		if (_instance == null)
			_instance = new HttpClient();
		return _instance;
	}
	
	private HttpClient(){
		int numThreads = Settings.getInstance().getHttpThreads();
		_pool = new Semaphore(numThreads, true);
	}
	
	/**
	 * Spawn new request thread
	 * @param request
	 */
	public void request(HttpRequest request){	
		new Thread(new HttpRequestThread(request)).start();
	}
	
	/**
	 * Make request and create XML document
	 */
	private Document sendHttpRequest(String uri){
		String url = uri.toString();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			db = dbf.newDocumentBuilder();
			return db.parse(url);
		} catch( Exception e){
			return null;
		}
	}
	
	/**
	 * Asyncron http request thread.
	 * Makes http request, and writes result back into the request object.
	 */
	private class HttpRequestThread implements Runnable {
		private HttpRequest _query;	
		public HttpRequestThread(HttpRequest q) {
			_query = q;
		}
		
		@Override
		public void run() {
			
			try {
				_pool.acquire();
			} catch (InterruptedException e) {
				_query.setResponse(null);
				return;
			}
			
			Document xmlDocument = sendHttpRequest(_query.getRequest());
			_query.setResponse(xmlDocument);
			_pool.release();
		}
	}
}
