package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import fi.ruisrock2011.android.dao.ConfigDAO;

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
				intent = new Intent(getBaseContext(), GeneralInfoListActivity.class);
				break;
			case R.id.infoPage_btnServices:
				intent = new Intent(getBaseContext(), ServicesListActivity.class);
				break;
			case R.id.infoPage_btnFoodAndDrink:
			    intent.putExtra("subPageTitle", getString(R.string.FoodAndDrink));
			    intent.putExtra("subPageContent", ConfigDAO.getPageFoodAndDrink(getBaseContext()));
			    intent.putExtra("slideAnim", true);
				break;
			case R.id.infoPage_btnTransportation:
				intent.putExtra("subPageTitle", getString(R.string.Transportation));
			    intent.putExtra("subPageContent", ConfigDAO.getPageTransportation(getBaseContext()));
			    intent.putExtra("slideAnim", true);
				break;
			}
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
