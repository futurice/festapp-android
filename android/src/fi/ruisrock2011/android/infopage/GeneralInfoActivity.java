package fi.ruisrock2011.android.infopage;

import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.util.StringUtil;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;

public class GeneralInfoActivity extends Activity {
	
	private static final String TAG = "GeneralInfoActivity";
	
	private WebView contentView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.info_sub_page);
		ImageView title = (ImageView) findViewById(R.id.infoSubPageTitle);
		title.setImageResource(R.drawable.title_general_info);
		
		contentView = (WebView) findViewById(R.id.infoPageWebView);
		try {
			String data = StringUtil.convertStreamToString(getResources().openRawResource(R.raw.general_info));
			contentView.loadData(data, "text/html", "UTF-8");
		} catch (Exception e) {
			Log.e(TAG, "Cannot read HTML resource", e);
		}
		
	}

}
