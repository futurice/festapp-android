package com.futurice.festapp;

import com.futurice.festapp.service.FestAppService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DebugActivity extends Activity {

	public final static boolean F_DEBUG = false;
	public volatile static boolean F_IGNORE_ETAG = false;

	private View.OnClickListener clicklistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.debug_menu_clear_db:
				//@TODO do stuff
				break;
			case R.id.debug_menu_fetch_all:
				Intent i = new Intent(DebugActivity.this, FestAppService.class);
				i.putExtra("com.futurice.festapp.service.FORCE", true);
				startService(i);
				
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug);
		createMenuItems();
	}

	private void createMenuItems() {
		findViewById(R.id.debug_menu_clear_db)
				.setOnClickListener(clicklistener);
		findViewById(R.id.debug_menu_fetch_all).setOnClickListener(
				clicklistener);
	}

}
