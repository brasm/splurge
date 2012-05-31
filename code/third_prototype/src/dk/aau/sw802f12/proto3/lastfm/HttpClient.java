package dk.aau.sw802f12.proto3.lastfm;

import java.util.concurrent.Semaphore;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import android.util.Log;

/**
 * This class is responsible for HTTP communication with last.fm 
 * , or any other rest service that returns XML.
 * Package protected 
 * 
 */
class HttpClient{
	
	// limit the amount of concurrent http request threads
	private Object _mutex = new Object();
	private Semaphore _pool = new Semaphore(10, true);
	// The last.fm api allows 5 request/sec
	private long _requestInterval = 1000/5;
	private long _lastRequestTimestamp = 0;
	static HttpClient _instance = null;
	static final HttpClient getInstance(){
		if (_instance == null)
			_instance = new HttpClient();
		return _instance;
	}
	
	private HttpClient(){}
	
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
				requestQueue();
			} 
			catch (InterruptedException e) {}
			finally {
				_pool.release();
			}
			Document xmlDocument = sendHttpRequest(_query.getRequest());
			_query.setResponse(xmlDocument);
		}
		// park thread until requestinterval adheres to last.fm api rules
		private void requestQueue() throws InterruptedException{
			synchronized (_mutex) {
				long now = System.currentTimeMillis();
				long diff = now - _lastRequestTimestamp;
				long timewait = _requestInterval - diff;
				if (timewait > 0){
					Log.d("LASTFM", "wait for " + timewait + "ms");
					_mutex.wait(timewait);
				}
				_lastRequestTimestamp = now;
			}			
		}
	}
}
