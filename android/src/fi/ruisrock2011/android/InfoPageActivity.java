package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import fi.ruisrock2011.android.InfoSubPageActivity.InfoSubPageType;

/**
 * InfoPage Activity.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class InfoPageActivity extends Activity {
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getBaseContext(), InfoSubPageActivity.class);
			switch (v.getId()) {
			case R.id.infoPage_btnGeneralInfo:
			    intent.putExtra("subPage", InfoSubPageType.GENERAL_INFO);
				break;
			case R.id.infoPage_btnServices:
				intent.putExtra("subPage", InfoSubPageType.SERVICES);
				break;
			case R.id.infoPage_btnFoodAndDrink:
			    intent.putExtra("subPage", InfoSubPageType.FOOD_AND_DRINK);
				break;
			case R.id.infoPage_btnTransportation:
				intent.putExtra("subPage", InfoSubPageType.TRANSPORTATION);
				break;
			}
			if (intent.hasExtra("subPage")) {
				startActivity(intent);
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
		findViewById(R.id.infoPage_btnTransportation).setOnClickListener(clickListener);
	}

}
