package fi.ruisrock2011.android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import fi.ruisrock2011.android.util.RuisrockConstants;

/**
 * View for a Info-sub-page.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class InfoSubPageActivity extends Activity {

	private String pageTitle;
	private String pageContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_sub_page);
		setExtras();

		TextView titleView = (TextView) findViewById(R.id.infoSubPageTitle);
		titleView.setText(pageTitle);
		if (getString(R.string.service_BikePark).equals(pageTitle)) {
			titleView.setText("Polkupyörä-parkki");
		}

		WebView contentView = (WebView) findViewById(R.id.infoPageWebView);
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

	@Override
	public void onBackPressed() {
		if (getIntent().hasExtra("slideAnim")) {
			finish();
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		} else {
			super.onBackPressed();
		}
	}
}
