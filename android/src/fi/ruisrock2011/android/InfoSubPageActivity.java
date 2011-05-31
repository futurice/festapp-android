package fi.ruisrock2011.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import fi.ruisrock2011.android.dao.ConfigDAO;
import fi.ruisrock2011.android.util.RuisrockConstants;
import fi.ruisrock2011.android.util.StringUtil;

/**
 * View for a Info-sub-page.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class InfoSubPageActivity extends Activity {
	
	private static final String TAG = "InfoSubPageActivity";
	private String pageTitle;
	private String pageContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_sub_page);
		setExtras();
		
		TextView textView = (TextView) findViewById(R.id.infoSubPageTitle);
		WebView contentView = (WebView) findViewById(R.id.infoPageWebView);
		textView.setText(pageTitle);
		contentView.loadDataWithBaseURL(RuisrockConstants.RUISROCK_BASE_URL, pageContent, "text/html", "utf-8", null);
	}
	
	private void setExtras() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			pageTitle = extras.getString("subPageTitle");
			pageContent = extras.getString("subPageContent");
			if (pageTitle == null) {
				pageTitle = "";
			}
			if (pageContent == null) {
				pageContent = "";
			}
		}
	}
	

}
