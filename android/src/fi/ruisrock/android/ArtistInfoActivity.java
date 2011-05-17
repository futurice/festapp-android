package fi.ruisrock.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.domain.Gig;
import fi.ruisrock.android.util.RuisrockConstants;
import fi.ruisrock.android.util.UIUtil;

public class ArtistInfoActivity extends Activity {
	
	private LinearLayout artistInfoView;
	private Gig gig;
	private OnClickListener favoriteListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.artistInfoFavorite) {
				ToggleButton favoriteButton = (ToggleButton) v;
				boolean isFavorite = favoriteButton.isChecked();
				GigDAO.setFavorite(ArtistInfoActivity.this, gig.getId(), isFavorite);
				gig.setFavorite(isFavorite);
				if (isFavorite) {
					Toast.makeText(getApplicationContext(), getString(R.string.artistInfoActivity_favoriteOn), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.artistInfoActivity_favoriteOff), Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
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
				((TextView) findViewById(R.id.artistInfoStage)).setText(gig.getStage());
				((TextView) findViewById(R.id.artistInfoLiveTime)).setText(gig.getTime());
				ToggleButton favoriteButton = (ToggleButton) findViewById(R.id.artistInfoFavorite);
				favoriteButton.setChecked(gig.isFavorite());
				favoriteButton.setOnClickListener(favoriteListener);
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
