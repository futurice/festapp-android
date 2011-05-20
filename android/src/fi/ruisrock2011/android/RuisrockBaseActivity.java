package fi.ruisrock2011.android;

import fi.ruisrock2011.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class RuisrockBaseActivity extends Activity {
	
	private ImageView mainMenuButton;
	private ImageView artistsMenuButton;
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.bottomMenu_artists) {
				startActivity(new Intent(getBaseContext(), ScheduleTabActivity.class));
			} else if (v.getId() == R.id.bottomMenu_artists) {
				startActivity(new Intent(getBaseContext(), ArtistInfoActivity.class));
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	protected void foo() {
		mainMenuButton = (ImageView) findViewById(R.id.bottomMenu_main);
		artistsMenuButton = (ImageView) findViewById(R.id.bottomMenu_artists);
		
		mainMenuButton.setOnClickListener(clickListener);
		artistsMenuButton.setOnClickListener(clickListener);
	}
	
}
