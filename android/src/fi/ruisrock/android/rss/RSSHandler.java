package fi.ruisrock.android.rss;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fi.ruisrock.android.util.StringUtil;

/**
 * RSSHandler.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class RSSHandler extends DefaultHandler {

	private List<RSSItem> feedItems;
	private RSSItem item;
	private StringBuilder builder;

	public List<RSSItem> getFeedItems() {
		return feedItems;
	}

	public void startDocument() throws SAXException {
		feedItems = new ArrayList<RSSItem>();
		builder = new StringBuilder();
	}

	public void endDocument() throws SAXException {
	}
	

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		String elementName = (StringUtil.isEmpty(localName)) ? qName : localName;
		
		if (elementName.equals("item")) {
			// create a new item
			item = new RSSItem();
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		String elementName = (StringUtil.isEmpty(localName)) ? qName : localName;
		String value = builder.toString();
		builder.setLength(0);
		
		if (item != null) {
			if (elementName.equals("title")) {
				item.setTitle(value);
			} else if (elementName.equals("link")) {
				item.setLink(value);
			} else if (elementName.equals("description")) {
				item.setDescription(value);
			} else if (elementName.equals("category")) {
				item.setCategory(value);
			} else if (elementName.equals("pubDate")) {
				item.setPubDate(value);
			}
		}
		
		if (elementName.equals("item")) {
			feedItems.add(item);
			item = null;
		}
	}

	public void characters(char ch[], int start, int length) {
		builder.append(ch, start, length);
	}

}
