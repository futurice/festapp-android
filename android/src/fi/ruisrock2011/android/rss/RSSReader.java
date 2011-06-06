package fi.ruisrock2011.android.rss;

import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

/**
 * RSSReader.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class RSSReader {

	public List<RSSItem> loadRSSFeed(String rssFeedUrl) {
		try {
			// Setup the url
			URL url = new URL(rssFeedUrl);

			// Create factory & parser
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			// Create the XML-reader
			XMLReader xmlreader = parser.getXMLReader();
			
			// Setup content-handler
			RSSHandler rssHandler = new RSSHandler();
			xmlreader.setContentHandler(rssHandler);
			
			// Parse feed
			InputSource is = new InputSource(url.openStream());
			xmlreader.parse(is);
			return rssHandler.getFeedItems();
		} catch (Exception e) {
			Log.e("RSSReader", "Error reading RSS feed", e);
			return null;
		}
	}

}
