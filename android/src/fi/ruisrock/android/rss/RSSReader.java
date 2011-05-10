package fi.ruisrock.android.rss;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

public class RSSReader {
	
	public static DateFormat RSS_DATE_FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

	public List<RSSItem> loadRSSFeed(String rssFeedUrl) {
		try {
			// setup the url
			URL url = new URL(rssFeedUrl);

			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// create a parser
			SAXParser parser = factory.newSAXParser();

			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();
			// instantiate our handler
			RSSHandler rssHandler = new RSSHandler();
			// assign our handler
			xmlreader.setContentHandler(rssHandler);
			// get our data via the url class
			InputSource is = new InputSource(url.openStream());
			// perform the synchronous parse
			xmlreader.parse(is);
			// get the results - should be a fully populated RSSFeed instance,
			// or null on error
			return rssHandler.getFeedItems();
		} catch (Exception e) {
			Log.e("RSSReader", "Error reading RSS feed", e);
			return null;
		}
	}

}
