package com.futurice.festapp;

import java.util.HashMap;

import com.flurry.android.FlurryAgent;
import com.futurice.festapp.util.FestAppConstants;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import com.futurice.festapp.R;

/**
 * View for a Info-sub-page.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class InfoSubPageActivity extends BaseActivity {

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
			titleView.setText("Polkupy�r�-parkki");
		}

		WebView contentView = (WebView) findViewById(R.id.infoPageWebView);
		contentView.setBackgroundColor(Color.TRANSPARENT);
		contentView.loadDataWithBaseURL(FestAppConstants.RUISROCK_BASE_URL, pageContent, "text/html", "utf-8", null);
		HashMap<String, String> titleMap = new HashMap<String, String>();
		titleMap.put("otsikko", pageTitle);
		FlurryAgent.logEvent("Infosivu", titleMap);
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
			} else {
				pageContent = pageContent.replaceAll("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", "");
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
