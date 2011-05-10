package fi.ruisrock.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import fi.ruisrock.android.dao.NewsDAO;
import fi.ruisrock.android.domain.NewsArticle;
import fi.ruisrock.android.rss.RSSItem;
import fi.ruisrock.android.rss.RSSReader;
import fi.ruisrock.android.ui.NewsArticleAdapter;
import fi.ruisrock.android.util.RuisrockConstants;

public class NewsListActivity extends Activity {
	
	private ListView newsList;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newslist);
		createNewsList();
	}
	
	private void createNewsList() {
		newsList = (ListView) findViewById(R.id.newsList);
		
		List<NewsArticle> articles = NewsDAO.findAll(this);
		if (articles.size() == 0) {
			RSSReader rssReader = new RSSReader();
			List<RSSItem> feed = rssReader.loadRSSFeed(RuisrockConstants.NEWS_RSS_URL);
			if (feed != null && feed.size() > 0) {
				articles = new ArrayList<NewsArticle>();
				for (RSSItem rssItem : feed) {
					articles.add(new NewsArticle(rssItem));
				}
				NewsDAO.replaceAll(this, articles);
			}
		}
	    newsList.setAdapter(new NewsArticleAdapter(this, articles));
	}
	
	
}
