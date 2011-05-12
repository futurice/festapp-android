package fi.ruisrock.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import fi.ruisrock.android.dao.NewsDAO;

/**
 * Debug Activity-view.
 * 
 * This activity *MUST NOT* be visible to end-users.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class DebugActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug);
		
		Button addButton = (Button) findViewById(R.id.clearTablesButton);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int articles = NewsDAO.deleteAll(getBaseContext());
				String message = String.format("Deleted following resources from DB:\n" +
						"- News articles: %d", articles);
				Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
			}
		});
	}

}
