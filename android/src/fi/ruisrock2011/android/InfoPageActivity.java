package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
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
			    Toast.makeText(getBaseContext(), "TODO: Implement", Toast.LENGTH_LONG).show();
				break;
			case R.id.infoPage_btnServices:
				intent = new Intent(getBaseContext(), ServicesListActivity.class);
				break;
			case R.id.infoPage_btnFoodAndDrink:
			    intent.putExtra("subPageTitle", getString(R.string.FoodAndDrink));
			    intent.putExtra("subPageContent", ConfigDAO.getPageFoodAndDrink(getBaseContext()));
				break;
			case R.id.infoPage_btnTransportation:
				intent.putExtra("subPageTitle", getString(R.string.Transportation));
			    intent.putExtra("subPageContent", ConfigDAO.getPageTransportation(getBaseContext()));
				break;
			}
			startActivity(intent);
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
