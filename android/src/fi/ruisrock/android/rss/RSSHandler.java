package fi.ruisrock.android.rss;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * RSSHandler.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class RSSHandler extends DefaultHandler {

	private List<RSSItem> feedItems;
	private RSSItem item;

	//private int depth = 0;
	private RssElementType currentRssElement = RssElementType.CHANNEL;

	public List<RSSItem> getFeedItems() {
		return feedItems;
	}

	public void startDocument() throws SAXException {
		feedItems = new ArrayList<RSSItem>();
		item = new RSSItem();
	}

	public void endDocument() throws SAXException {
	}
	
	private enum RssElementType {
		CHANNEL,
		TITLE,
		LINK,
		DESCRIPTION,
		CATEGORY,
		PUB_DATE
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		//depth++;
		if (localName.equals("channel")) {
			currentRssElement = RssElementType.CHANNEL;
			return;
		}
		if (localName.equals("item")) {
			// create a new item
			item = new RSSItem();
			return;
		}
		if (localName.equals("title")) {
			currentRssElement = RssElementType.TITLE;
			return;
		}
		if (localName.equals("description")) {
			currentRssElement = RssElementType.DESCRIPTION;
			return;
		}
		if (localName.equals("link")) {
			currentRssElement = RssElementType.LINK;
			return;
		}
		if (localName.equals("category")) {
			currentRssElement = RssElementType.CATEGORY;
			return;
		}
		if (localName.equals("pubDate")) {
			currentRssElement = RssElementType.PUB_DATE;
			return;
		}
		currentRssElement = RssElementType.CHANNEL;
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		//depth--;
		if (localName.equals("item")) {
			// add our item to the list!
			feedItems.add(item);
			return;
		}
	}

	public void characters(char ch[], int start, int length) {
		String value = new String(ch, start, length);
		Log.i("RSSReader", "characters[" + value + "]");

		switch (currentRssElement) {
		case TITLE:
			item.setTitle(value);
			break;
		case LINK:
			item.setLink(value);
			break;
		case DESCRIPTION:
			item.setDescription(value);
			break;
		case CATEGORY:
			item.setCategory(value);
			break;
		case PUB_DATE:
			item.setPubDate(value);
			break;
		default:
			break;
		}
		currentRssElement = RssElementType.CHANNEL;
	}

}
