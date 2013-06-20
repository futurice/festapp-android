package fi.ruisrock2011.android;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fi.ruisrock2011.android.dao.NewsDAO;
import fi.ruisrock2011.android.domain.NewsArticle;
import fi.ruisrock2011.android.ui.NewsArticleAdapter;
import fi.ruisrock2011.android.util.StringUtil;

/**
 * View for showing a list of News-articles.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class NewsListActivity extends Activity {
	
	private ListView newsList;
	private List<NewsArticle> articles;
	
	private OnItemClickListener newsArticleClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int index, long arg) {
			Object o = av.getItemAtPosition(index);
			if (o instanceof NewsArticle) {
				NewsArticle article = (NewsArticle) o;
				if (StringUtil.isNotEmpty(article.getUrl())) {
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
	
	private void createNewsList() {
		newsList = (ListView) findViewById(R.id.newsList);
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View header = inflater.inflate(R.layout.list_header, null, false);

		((TextView)header.findViewById(R.id.listTitle)).setText(getResources().getString(R.string.News));

		newsList.addHeaderView(header);
		
		articles = NewsDAO.findAll(this);
	    newsList.setAdapter(new NewsArticleAdapter(this, articles));
	    newsList.setOnItemClickListener(newsArticleClickListener);
	}
	
}
