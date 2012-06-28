package fi.ruisrock2011.android;

import fi.ruisrock2011.android.dao.AnalyticsHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
				showCall02Dialog();
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

	private ImageView callGlow;
	private Animation animation1;
	private Animation animation2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fonecta_02);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int paddingTop = (int)(0.4 * metrics.heightPixels);
		
		LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayout1);
		linearLayout.setPadding(0, paddingTop, 0, 0);
		
		callButton = (ImageView) findViewById(R.id.call02Button);
		callButton.setOnClickListener(callClickListener);

		showPricesText = (TextView) findViewById(R.id.showPricesText);
		showPricesText.setOnClickListener(showPricesClickListener);

		callGlow = (ImageView) findViewById(R.id.call02Glow);

		animation1 = AnimationUtils.loadAnimation(this, R.anim.glow1);
		animation1.setAnimationListener(new GlowAnimationListener());
		animation2 = AnimationUtils.loadAnimation(this, R.anim.glow2);
		animation2.setAnimationListener(new GlowAnimationListener2());

		callGlow.startAnimation(animation1);
	}

	protected void showCall02Dialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.fonectaActivity_confirmation)).setCancelable(false)
			.setPositiveButton(getString(R.string.Call), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					try {
						Intent intent = new Intent(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:020202"));
						startActivity(intent);
					} catch (Exception e) {
						Log.e(TAG, "Failed to invoke call", e);
						Toast.makeText(getBaseContext(), getString(R.string.fonectaActivity_callError), Toast.LENGTH_LONG);
					}
					AnalyticsHelper.sendAnalytics(FonectaActivity.this, AnalyticsHelper.EVENT_02_CALL);
				}
			}).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					AnalyticsHelper.sendAnalytics(FonectaActivity.this, AnalyticsHelper.EVENT_02_CANCEL);
				}
			});
		final AlertDialog alert = builder.create();
		alert.show();		
}

class GlowAnimationListener implements AnimationListener {
	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		callGlow.startAnimation(animation2);
	}
}

class GlowAnimationListener2 implements AnimationListener {
	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		callGlow.startAnimation(animation1);
	}
}

}
