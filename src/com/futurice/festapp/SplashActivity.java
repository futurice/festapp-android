package com.futurice.festapp;

import com.futurice.festapp.analytics.TagManagerUtils;
import com.futurice.festapp.util.FestAppConstants;

import com.futurice.festapp.R;
import com.google.tagmanager.Container;
import com.google.tagmanager.ContainerOpener;
import com.google.tagmanager.ContainerOpener.OpenType;
import com.google.tagmanager.Logger.LogLevel;
import com.google.tagmanager.TagManager;

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

		TagManagerUtils.initTagManager(this, new Runnable() {
			@Override
			public void run() {
				showSplash();
			}
		});
	}

	public void showSplash() {
		// Added for a cleaner start if no splash
		if (!active) {
			startActivity(new Intent(getBaseContext(), FestAppMainActivity.class));
			finish();
		} else {
			// thread for displaying the SplashScreen
			Thread splashTread = new Thread() {
				@Override
				public void run() {
					try {
						int waited = 0;
						while(active && (waited < FestAppConstants.SPLASH_SCREEN_TIMEOUT)) {
							sleep(100);
							if(active) {
								waited += 100;
							}
						}
					} catch(InterruptedException e) {
						// do nothing
					} finally {
						active = false;

						// Added transitions for smoother activity switch
						SplashActivity.this.startActivity(new Intent(getBaseContext(), FestAppMainActivity.class));
						SplashActivity.this.finish();
					}
				}
			};
			splashTread.start();
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
