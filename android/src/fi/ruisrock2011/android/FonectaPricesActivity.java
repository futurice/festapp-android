package fi.ruisrock2011.android;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import fi.ruisrock2011.android.util.StringUtil;

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
		
		TextView textView = (TextView) findViewById(R.id.text);
		textView.setText(Html.fromHtml(getText()));
	}
	
	private String getText() {
		InputStream is = getResources().openRawResource(R.raw.page_02_prices);
		try {
			return StringUtil.convertStreamToString(is);
		} catch (Exception e) {
			return "";
		}
	}

}
