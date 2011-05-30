package fi.ruisrock2011.android;

import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.util.RuisrockConstants;
import fi.ruisrock2011.android.util.StringUtil;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

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
