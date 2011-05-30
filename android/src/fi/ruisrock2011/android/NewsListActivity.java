package fi.ruisrock2011.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.dao.NewsDAO;
import fi.ruisrock2011.android.domain.NewsArticle;
import fi.ruisrock2011.android.rss.RSSItem;
import fi.ruisrock2011.android.rss.RSSReader;
import fi.ruisrock2011.android.ui.NewsArticleAdapter;
import fi.ruisrock2011.android.util.RuisrockConstants;
import fi.ruisrock2011.android.util.StringUtil;
import fi.ruisrock2011.android.util.UIUtil;

/**
 * View for showing a list of News-articles (gotten via RSS).
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class NewsListActivity extends Activity {
	
	private ListView newsList;
	private boolean rssLoadingSuccess;
	private List<NewsArticle> articles;
	
	private ProgressDialog progressDialog;
	
	private OnItemClickListener newsArticleClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int index, long arg) {
			Object o = av.getItemAtPosition(index);
			if (o instanceof NewsArticle) {
				NewsArticle article = (NewsArticle) o;
				if (StringUtil.isNotEmpty(article.getUrl())) {
					//Toast.makeText(getBaseContext(), "TODO: implement", Toast.LENGTH_SHORT).show();
					Intent i = new Intent(getBaseContext(), NewsContentActivity.class);
					i.putExtra("news.title", article.getTitle());
					i.putExtra("news.date", article.getDateString());
					i.putExtra("news.content", article.getContent());
					startActivity(i);
					return;
				}
			}
			Toast.makeText(v.getContext(), getString(R.string.newsActivity_invalidUrl), Toast.LENGTH_LONG).show();
		}
	};
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		createNewsList();
	}
	
	private void showErrorDialog(String title, String message) {
		UIUtil.showErrorDialog(title, message, this);
	}
	
	/*
	private boolean updateNewsArticlesViaRSS() {
		RSSReader rssReader = new RSSReader();
		List<RSSItem> feed = rssReader.loadRSSFeed(RuisrockConstants.NEWS_RSS_URL);
		if (feed != null && feed.size() > 0) {
			articles = new ArrayList<NewsArticle>();
			for (RSSItem rssItem : feed) {
				articles.add(new NewsArticle(rssItem));
			}
			NewsDAO.replaceAll(this, articles);
			return true;
		}
		return false;
	}
	*/
	
	private void createNewsList() {
		newsList = (ListView) findViewById(R.id.newsList);
		
		articles = NewsDAO.findAll(this);
		/*
		if (articles.size() == 0) {
			progressDialog = ProgressDialog.show(this, "", getString(R.string.newsActivity_loadingArticles));
			ActivityThread rssThread = new ActivityThread();
			rssThread.start();
		}
		*/
		
	    newsList.setAdapter(new NewsArticleAdapter(this, articles));
	    newsList.setOnItemClickListener(newsArticleClickListener);
	}
	
	/*
	private class ActivityThread extends Thread {
		@Override
		public void run() {
			rssLoadingSuccess = updateNewsArticlesViaRSS();
			handler.sendEmptyMessage(0);
		}

		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
				if (rssLoadingSuccess) {
					newsList.setAdapter(new NewsArticleAdapter(getBaseContext(), articles));
				} else {
					showErrorDialog(getString(R.string.Error), getString(R.string.newsActivity_httpError));
				}
			}
		};
	}
	*/
	
}
