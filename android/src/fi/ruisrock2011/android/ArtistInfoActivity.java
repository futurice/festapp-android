package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.domain.GigLocation;
import fi.ruisrock2011.android.util.RuisrockConstants;
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
		showInitialInfoOnFirstVisit(this);
	}
	
	private void showInitialInfoOnFirstVisit(Context context) {
		SharedPreferences pref = context.getSharedPreferences(RuisrockConstants.PREFERENCE_GLOBAL, Context.MODE_PRIVATE);
		final String key = RuisrockConstants.PREFERENCE_SHOW_FAVORITE_INFO;
		if (pref.getBoolean(key, true)) {
			Editor editor = pref.edit();
			editor.putBoolean(key, false);
			editor.commit();
			UIUtil.showDialog(context.getString(R.string.timelineActivity_initialInfo_title), context.getString(R.string.timelineActivity_initialInfo_msg), context);
		}
	}
	
	
	private void populateViewValues() {
		if (gig == null) {
			artistInfoView.setVisibility(View.GONE);
			UIUtil.showDialog(getString(R.string.Error), getString(R.string.artistInfoActivity_invalidId), this);
		} else {
			artistInfoView.setVisibility(View.VISIBLE);
			TextView artistName = (TextView) findViewById(R.id.artistName);
			artistName.setText(gig.getArtist());
			
			LinearLayout infoTable = (LinearLayout) findViewById(R.id.artistInfoTable);
			infoTable.setVisibility(View.VISIBLE);
			StringBuilder stageText = new StringBuilder("");
			StringBuilder timeText = new StringBuilder("");
			for (GigLocation location : gig.getLocations()) {
				String nl = (stageText.length() != 0) ? "\n" : "";
				String stage = (location.getStage() != null) ? location.getStage() : "";
				stageText.append(nl + stage);
				timeText.append(nl + location.getDayAndTime());
			}
			((TextView) findViewById(R.id.artistInfoStage)).setText(stageText.toString());
			((TextView) findViewById(R.id.artistInfoLiveTime)).setText(timeText.toString());
			ToggleButton favoriteButton = (ToggleButton) findViewById(R.id.artistInfoFavorite);
			favoriteButton.setChecked(gig.isFavorite());
			favoriteButton.setOnClickListener(favoriteListener);
			infoTable.bringToFront();
			
			ImageView artistImage = (ImageView) findViewById(R.id.artistImage);
			LinearLayout artistImageContainer = (LinearLayout) findViewById(R.id.artistImageContainer);
			int imageId = getResources().getIdentifier("artist_" + gig.getId(), "drawable", getPackageName());
			if (imageId == 0) {
				artistImageContainer.setVisibility(View.GONE);
			} else {
				try {
					DisplayMetrics metrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = metrics.widthPixels;
					
					int height = (int) (400 * getResources().getDisplayMetrics().density);
					artistImage.setImageBitmap(UIUtil.decodeSampledBitmapFromResource(getResources(), imageId, width, height));
					artistImageContainer.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					artistImageContainer.setVisibility(View.GONE);
				}
			}
			
			TextView artistDescription = (TextView) findViewById(R.id.artistDescription);
			artistDescription.setMovementMethod(LinkMovementMethod.getInstance());
			artistDescription.setText(Html.fromHtml(gig.getDescription()));
			findViewById(R.id.spotify).setVisibility(gig.getSpotify() != null ? View.VISIBLE : View.GONE);
			findViewById(R.id.youtube).setVisibility(gig.getYoutube() != null ? View.VISIBLE : View.GONE);
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
		
	public void openSpotify(View v) {
		try {
			Intent launcher = new Intent( Intent.ACTION_VIEW, Uri.parse(gig.getSpotify()) );
			startActivity(launcher);
		} catch(ActivityNotFoundException anfe) {
			Toast.makeText(this, R.string.artistInfoActivity_no_spotify, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void openYoutube(View v) {
		Intent launcher = new Intent( Intent.ACTION_VIEW, Uri.parse(gig.getYoutube()) );
		startActivity(launcher);
	}
}
