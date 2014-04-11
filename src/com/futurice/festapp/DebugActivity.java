package com.futurice.festapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class DebugActivity extends Activity {

	public final static boolean F_DEBUG = false;
	public static boolean F_IGNORE_ETAG = false;

	private View.OnClickListener clicklistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.debug_menu_clear_db:
				//@TODO do stuff
				break;
			case R.id.debug_menu_fetch_all:
				//@TODO do stuff
				break;
			}
		}
	};

	private CompoundButton.OnCheckedChangeListener checkboxlistener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton v, boolean isChecked) {
			switch (v.getId()) {
			case R.id.debug_menu_ignore_etag:
				F_IGNORE_ETAG = isChecked;
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
		((CheckBox) findViewById(R.id.debug_menu_ignore_etag))
				.setOnCheckedChangeListener(checkboxlistener);
		findViewById(R.id.debug_menu_clear_db)
				.setOnClickListener(clicklistener);
		findViewById(R.id.debug_menu_fetch_all).setOnClickListener(
				clicklistener);
	}

}
