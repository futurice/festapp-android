package fi.ruisrock2011.android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import fi.ruisrock2011.android.util.RuisrockConstants;

/**
 * View for showing content of a News-article. 
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class NewsContentActivity extends Activity {
	
	private static final String TAG = "NewsDescriptionActivity";
	
	private WebView contentView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_content);
		
		
		TextView titleView = (TextView) findViewById(R.id.newsTitle);
		titleView.setText("");
		
		TextView dateView = (TextView) findViewById(R.id.newsDate);
		dateView.setText("");
		
		contentView = (WebView) findViewById(R.id.newsContent);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			titleView.setText((String) extras.get("news.title"));
			dateView.setText((String) extras.get("news.date"));
			contentView.loadDataWithBaseURL(RuisrockConstants.RUISROCK_BASE_URL, (String) extras.get("news.content"), "text/html", "utf-8", null);
		}
	}

}
