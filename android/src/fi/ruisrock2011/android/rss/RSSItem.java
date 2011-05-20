package fi.ruisrock2011.android.rss;

public class RSSItem {

	private String title = null;
	private String description = null;
	private String link = null;
	private String category = null;
	private String pubdate = null;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setPubDate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getLink() {
		return link;
	}

	public String getCategory() {
		return category;
	}

	public String getPubDate() {
		return pubdate;
	}

}
