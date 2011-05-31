package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * View for Fonecta 02.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class FonectaActivity extends Activity {
	
	private static final String TAG = "FonectaActivity";
	
	private ImageView callButton;
	private TextView showPricesText;
	private View.OnClickListener callClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.call02Button) {
				try {
					Intent intent = new Intent(Intent.ACTION_CALL);
					// TODO: enable phone-number
					//intent.setData(Uri.parse("tel:+35820202"));
					startActivity(intent);
				} catch (Exception e) {
					Log.e(TAG, "Failed to invoke call", e);
					Toast.makeText(getBaseContext(), getString(R.string.fonectaActivity_callError), Toast.LENGTH_LONG);
				}
			}
		}
	};
	private View.OnClickListener showPricesClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.showPricesText) {
				startActivity(new Intent(getBaseContext(), FonectaPricesActivity.class));
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fonecta_02);
		
		callButton = (ImageView) findViewById(R.id.call02Button);
		callButton.setOnClickListener(callClickListener);
		
		showPricesText = (TextView) findViewById(R.id.showPricesText);
		showPricesText.setOnClickListener(showPricesClickListener);
	}

}
