package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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
					intent.setData(Uri.parse("tel:+35820202"));
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
	
	private ImageView callGlow;
	private Animation animation1;
	private Animation animation2;
	/*
	private Runnable currentPositionRunnable = new Runnable() {
		@Override
		public void run() {
			
			//currentPositionHandler.postDelayed(this, CURRENT_POSITION_ANIM_FREQ);
		}
	};
	private Handler currentPositionHandler = new Handler();
	*/
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fonecta_02);
		
		callButton = (ImageView) findViewById(R.id.call02Button);
		callButton.setOnClickListener(callClickListener);
		
		showPricesText = (TextView) findViewById(R.id.showPricesText);
		showPricesText.setOnClickListener(showPricesClickListener);
		
		callGlow = (ImageView) findViewById(R.id.call02Glow);
		
		animation1 = AnimationUtils.loadAnimation(this, R.anim.glow1);
		animation1.setAnimationListener(new LocalAnimationListener());
		//animation1.setRepeatMode(Animation.INFINITE);
		animation2 = AnimationUtils.loadAnimation(this, R.anim.glow2);
		animation2.setAnimationListener(new LocalAnimationListener2());
		
		callGlow.startAnimation(animation1);
	}
	
	class LocalAnimationListener implements AnimationListener {
		/*
		private Animation startAnimation;
		public LocalAnimationListener(Animation startAnimation) {
			this.startAnimation = startAnimation;
		}
		*/
		
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
	
	class LocalAnimationListener2 implements AnimationListener {
		/*
		private Animation startAnimation;
		public LocalAnimationListener(Animation startAnimation) {
			this.startAnimation = startAnimation;
		}
		*/
		
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
