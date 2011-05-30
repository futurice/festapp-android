package fi.ruisrock2011.android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import fi.ruisrock2011.android.InfoSubPageActivity.InfoSubPageType;

public class FonectaActivity extends Activity {
	
	private static final String TAG = "FonectaActivity";
	private TextView textView;
	private WebView contentView;
	private InfoSubPageType pageType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fonecta_02);
	}

}
