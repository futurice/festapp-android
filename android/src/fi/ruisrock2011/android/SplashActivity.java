package fi.ruisrock2011.android;

import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.dao.AnalyticsHelper;
import fi.ruisrock2011.android.domain.to.HTTPBackendResponse;
import fi.ruisrock2011.android.util.HTTPUtil;
import fi.ruisrock2011.android.util.RuisrockConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Initial Splash-screen.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class SplashActivity extends Activity {

	private static boolean active = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		// TODO: Added for Ruisrock2012 for a cleaner start if no splash
		if (!active) {
			startActivity(new Intent(getBaseContext(), RuisrockMainActivity.class));
			finish();
		} else {
			// thread for displaying the SplashScreen
			Thread splashTread = new Thread() {
				@Override
				public void run() {
					try {
						int waited = 0;
						while(active && (waited < RuisrockConstants.SPLASH_SCREEN_TIMEOUT)) {
							sleep(100);
							if(active) {
								waited += 100;
							}
						}
					} catch(InterruptedException e) {
						// do nothing
					} finally {
						active = false;
						// TODO: Added transitions for Ruisrock2012 for smoother activity switch
						SplashActivity.this.startActivity(new Intent(getBaseContext(), RuisrockMainActivity.class));
						SplashActivity.this.finish();
					}
				}
			};
			splashTread.start();
			
			// TODO: Analytics for Ruisrock2012
			AnalyticsHelper.sendAnalytics(this, AnalyticsHelper.EVENT_START);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			active = false;
		}
		return true;
	}

}
