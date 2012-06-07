package dk.aau.sw802f12.splurge.lastfm;

import org.w3c.dom.Document;

/**
 * Package protected wrapper class.
 * Initiates http request, and passes result on to responseHandler;
 * @author sw802f12
 */
class HttpRequest {
	
	private String mRequest;
	private XmlResponseHandler mHandler;
	
	HttpRequest(String uri, XmlResponseHandler xmlResponseHandler) {
		mRequest = uri;
		mHandler = xmlResponseHandler;
		HttpClient hc = HttpClient.getInstance();
		hc.request(this);
	}
	
	String getRequest(){
		return mRequest;
	}
	
	void setResponse(Document xmlDocument) {
		mHandler.execute(xmlDocument);
	}
}
