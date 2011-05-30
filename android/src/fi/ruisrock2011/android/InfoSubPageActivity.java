package fi.ruisrock2011.android;

import fi.ruisrock2011.android.dao.ConfigDAO;
import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.util.RuisrockConstants;
import fi.ruisrock2011.android.util.StringUtil;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

public class InfoSubPageActivity extends Activity {
	
	public enum InfoSubPageType {
		GENERAL_INFO,
		FOOD_AND_DRINK,
		SERVICES,
		TRANSPORTATION
	}
	
	private static final String TAG = "GeneralInfoActivity";
	private TextView textView;
	private WebView contentView;
	private InfoSubPageType pageType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_sub_page);
		setPageType();
		
		textView = (TextView) findViewById(R.id.infoSubPageTitle);
		contentView = (WebView) findViewById(R.id.infoPageWebView);
		
		String data = "";
		switch (pageType) {
		case GENERAL_INFO:
			textView.setText(R.string.GeneralInfo);
			try {
				data = StringUtil.convertStreamToString(getResources().openRawResource(R.raw.page_general_info));
			} catch (Exception e) {
				Log.e(TAG, "Cannot read HTML resource", e);
			}
			break;
		case FOOD_AND_DRINK:
			textView.setText(R.string.FoodAndDrink);
			data = ConfigDAO.getPageFoodAndDrink(getBaseContext());
			break;
		case SERVICES:
			textView.setText(R.string.Services);
			break;
		case TRANSPORTATION:
			textView.setText(R.string.Transportation);
			data = ConfigDAO.getPageTransportation(getBaseContext());
			break;
		}
		
		contentView.loadDataWithBaseURL(RuisrockConstants.RUISROCK_BASE_URL, data, "text/html", "utf-8", null);
	}
	
	private void setPageType() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			pageType = (InfoSubPageType) extras.get("subPage");
		}
	}
	

}
