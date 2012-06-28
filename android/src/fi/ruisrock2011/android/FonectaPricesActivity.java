package fi.ruisrock2011.android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * View for Fonecta prices.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class FonectaPricesActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fonecta_02_prices);
		
		WebView view = (WebView)findViewById(R.id.fonecta_02_prices_webview);
		view.getSettings().setLoadWithOverviewMode(true);
		view.getSettings().setUseWideViewPort(true);
		view.setWebViewClient(new MyWebView());
		
		view.loadUrl("http://www.fonecta.com/kuluttajapalvelut/020202/fi_FI/hinnat/");
	}
	
	private class MyWebView extends WebViewClient { 
        @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
            view.loadUrl(url); 
            return true;
        } 
    }
}
