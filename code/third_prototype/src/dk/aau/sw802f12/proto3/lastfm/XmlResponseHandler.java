package dk.aau.sw802f12.proto3.lastfm;

import org.w3c.dom.Document;

interface XmlResponseHandler {
	void execute(Document xmlDocument);
}
