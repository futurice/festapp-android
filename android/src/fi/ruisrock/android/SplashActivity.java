package fi.ruisrock.android;

import fi.ruisrock.android.util.RuisrockConstants;
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
	                finish();
	                startActivity(new Intent(getBaseContext(), RuisrockMainActivity.class));
	            }
	        }
	    };
	    splashTread.start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        active = false;
	    }
	    return true;
	}

}
