package fi.ruisrock2011.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.util.UIUtil;

/**
 * View for Artist-info.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class ArtistInfoActivity extends Activity {
	
	private RelativeLayout artistInfoView;
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
					Toast.makeText(getApplicationContext(), getString(R.string.artistInfoActivity_favoriteOn), Toast.LENGTH_SHORT).show();
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
		
		artistInfoView = (RelativeLayout) findViewById(R.id.artistInfoView);
		gig = getGig();
		populateViewValues();
		UIUtil.showInitialFavoriteInfoOnFirstVisit(this);
	}
	
	
	private void populateViewValues() {
		if (gig == null) {
			artistInfoView.setVisibility(View.GONE);
			UIUtil.showDialog(getString(R.string.Error), getString(R.string.artistInfoActivity_invalidId), this);
		} else {
			artistInfoView.setVisibility(View.VISIBLE);
			TextView artistName = (TextView) findViewById(R.id.artistName);
			artistName.setText(gig.getArtist());
			
			TableLayout infoTable = (TableLayout) findViewById(R.id.artistInfoTable);
			infoTable.setVisibility(View.VISIBLE);
			String stage = (gig.getStage() != null) ? gig.getStage() : "";
			((TextView) findViewById(R.id.artistInfoStage)).setText(stage);
			((TextView) findViewById(R.id.artistInfoLiveTime)).setText(gig.getDayAndTime());
			ToggleButton favoriteButton = (ToggleButton) findViewById(R.id.artistInfoFavorite);
			favoriteButton.setChecked(gig.isFavorite());
			favoriteButton.setOnClickListener(favoriteListener);
			infoTable.bringToFront();
			
			ImageView artistImage = (ImageView) findViewById(R.id.artistImage);
			RelativeLayout artistImageContainer = (RelativeLayout) findViewById(R.id.artistImageContainer);
			Integer imageId = GigDAO.getImageIdForArtist(gig.getArtist());
			if (imageId == null) {
				artistImageContainer.setVisibility(View.GONE);
			} else {
				try {
					artistImage.setImageDrawable(getResources().getDrawable(imageId));
					artistImageContainer.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					artistImageContainer.setVisibility(View.GONE);
				}
			}
			
			TextView artistDescription = (TextView) findViewById(R.id.artistDescription);
			artistDescription.setMovementMethod(LinkMovementMethod.getInstance());
			artistDescription.setText(Html.fromHtml(gig.getDescription()));
		}
	}
	
	private Gig getGig() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String id = (String) extras.get("gig.id");
			if (id != null) {
				return GigDAO.findGig(this, id);
			}
		}
		return null;
	}

}
