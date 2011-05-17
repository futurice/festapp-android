package fi.ruisrock.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.domain.Gig;
import fi.ruisrock.android.util.RuisrockConstants;
import fi.ruisrock.android.util.UIUtil;

public class ArtistInfoActivity extends Activity {
	
	private LinearLayout artistInfoView;
	private Gig gig;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_info);
		
		artistInfoView = (LinearLayout) findViewById(R.id.artistInfoView);
		gig = getGig();
		populateViewValues();
	}
	
	private void populateViewValues() {
		
		
		if (gig == null) {
			artistInfoView.setVisibility(View.GONE);
			UIUtil.showErrorDialog(getString(R.string.Error), getString(R.string.artistInfoActivity_invalidId), this);
		} else {
			artistInfoView.setVisibility(View.VISIBLE);
			TextView artistName = (TextView) findViewById(R.id.artistName);
			artistName.setText(gig.getArtist());
			
			TableLayout infoTable = (TableLayout) findViewById(R.id.artistInfoTable);
			if (gig.getStage() != null && gig.getStartTime() != null && gig.getEndTime() != null) {
				infoTable.setVisibility(View.VISIBLE);
				
				
				
			} else {
				infoTable.setVisibility(View.GONE);
			}
			
			ImageView artistImage = (ImageView) findViewById(R.id.artistImage);
			RelativeLayout artistImageContainer = (RelativeLayout) findViewById(R.id.artistImageContainer);
			int imageId = getResources().getIdentifier(RuisrockConstants.ARTIST_DRAWABLE_PREFIX + gig.getId(), "drawable", getPackageName());
			if (imageId == 0) {
				artistImageContainer.setVisibility(View.GONE);
			} else {
				artistImage.setImageDrawable(getResources().getDrawable(imageId));
				artistImageContainer.setVisibility(View.VISIBLE);
			}
			
			TextView artistDescription = (TextView) findViewById(R.id.artistDescription);
			artistDescription.setMovementMethod(LinkMovementMethod.getInstance());
			artistDescription.setText(Html.fromHtml(gig.getDescription()));
		}
	}
	
	private Gig getGig() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String id = (String) getIntent().getExtras().get("gig.id");
			if (id!= null) {
				return GigDAO.findGig(this, id);
			}
		}
		return null;
	}

}
