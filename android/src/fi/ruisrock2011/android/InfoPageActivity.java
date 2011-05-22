package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import fi.ruisrock2011.android.infopage.GeneralInfoActivity;

/**
 * InfoPage Activity.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class InfoPageActivity extends Activity {
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.infoPage_btnGeneralInfo:
				startActivity(new Intent(getBaseContext(), GeneralInfoActivity.class));
				break;
			case R.id.infoPage_btnServices:
				break;
			case R.id.infoPage_btnFoodAndDrink:
				break;
			case R.id.infoPage_btnMoving:
				break;
			default:
				break;
			}
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_page);
		
		findViewById(R.id.infoPage_btnGeneralInfo).setOnClickListener(clickListener);
		findViewById(R.id.infoPage_btnServices).setOnClickListener(clickListener);
		findViewById(R.id.infoPage_btnFoodAndDrink).setOnClickListener(clickListener);
		findViewById(R.id.infoPage_btnMoving).setOnClickListener(clickListener);
	}

}
