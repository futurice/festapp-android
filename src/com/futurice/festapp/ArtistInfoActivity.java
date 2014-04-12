package com.futurice.festapp;

import java.util.HashMap;

import com.futurice.festapp.dao.GigDAO;
import com.futurice.festapp.domain.Gig;
import com.futurice.festapp.domain.GigLocation;
import com.futurice.festapp.util.FestAppConstants;
import com.futurice.festapp.util.UIUtil;

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

import com.futurice.festapp.R;

/**
 * View for Artist-info.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class ArtistInfoActivity extends Activity {
	
	private RelativeLayout artistInfoView;
	private Gig gig;
	private final static int HEIGHT = 400;
	
	private OnClickListener favoriteListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.artistInfoFavorite || v.getId() == R.id.artistInfoTable) {
				ToggleButton favoriteButton = (ToggleButton) findViewById(R.id.artistInfoFavorite);
				if(v.getId() == R.id.artistInfoTable) {
					favoriteButton.setChecked(!favoriteButton.isChecked());
				}
				boolean isFavorite = favoriteButton.isChecked();
				GigDAO.setFavorite(ArtistInfoActivity.this, gig.getId(), isFavorite);
				gig.setFavorite(isFavorite);
				if (isFavorite) {
					Toast.makeText(getApplicationContext(), getString(R.string.artistInfoActivity_favoriteOn), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.artistInfoActivity_favoriteOff), Toast.LENGTH_SHORT).show();
				}
				HashMap<String, String> artistMap = new HashMap<String, String>();
				artistMap.put("artist", gig.getArtist());
				artistMap.put("favourite", isFavorite ? "true" : "false");
				artistMap.put("view", "profile");
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
		HashMap<String, String> artistMap = new HashMap<String, String>();
		artistMap.put("artist", gig.getArtist());
	}
	
	private void showInitialInfoOnFirstVisit(Context context) {
		SharedPreferences pref = context.getSharedPreferences(FestAppConstants.PREFERENCE_GLOBAL, Context.MODE_PRIVATE);
		final String key = FestAppConstants.PREFERENCE_SHOW_FAVORITE_INFO;
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
			return;
		}
		
		artistInfoView.setVisibility(View.VISIBLE);
		TextView artistName = (TextView) findViewById(R.id.artistName);
		artistName.setText(gig.getArtist());

		LinearLayout infoTable = (LinearLayout) findViewById(R.id.artistInfoTable);
		infoTable.setVisibility(View.VISIBLE);
		StringBuilder stageText = new StringBuilder("");
		StringBuilder timeText = new StringBuilder("");
		for (GigLocation location : gig.getLocations()) {
			String nl = (stageText.length() != 0) ? "\n" : "";
			String stage = (location.getStage() != null) ? location.getStage()
					: "";
			stageText.append(nl + stage);
			timeText.append(nl + location.getDayAndTime());
		}
		((TextView) findViewById(R.id.artistInfoStage)).setText(stageText
				.toString());
		((TextView) findViewById(R.id.artistInfoLiveTime)).setText(timeText
				.toString());
		ToggleButton favoriteButton = (ToggleButton) findViewById(R.id.artistInfoFavorite);
		favoriteButton.setChecked(gig.isFavorite());
		favoriteButton.setOnClickListener(favoriteListener);
		infoTable.setOnClickListener(favoriteListener);
		infoTable.bringToFront();

		displayArtistImage();
		
		TextView artistDescription = (TextView) findViewById(R.id.artistDescription);
		artistDescription.setMovementMethod(LinkMovementMethod.getInstance());
		artistDescription.setText(Html.fromHtml(gig.getDescription()));
		findViewById(R.id.spotify).setVisibility(
				gig.getSpotify() != null ? View.VISIBLE : View.GONE);
		findViewById(R.id.youtube).setVisibility(
				gig.getYoutube() != null ? View.VISIBLE : View.GONE);
		
	}

	private void displayArtistImage() {
		ImageView artistImage = (ImageView) findViewById(R.id.artistImage);
		LinearLayout artistImageContainer = (LinearLayout) findViewById(R.id.artistImageContainer);
		int imageId = getResources().getIdentifier(gig.getArtistImage(),	"drawable", getPackageName());
		int flag = View.GONE;
		if (imageId != 0) {
			try {
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				int width = metrics.widthPixels;
				int height = (int) (HEIGHT * getResources().getDisplayMetrics().density);
				artistImage.setImageBitmap(UIUtil
						.decodeSampledBitmapFromResource(getResources(),
								imageId, width, height));
				flag = View.VISIBLE;
			} catch (Exception e) {
			}
		}
		artistImageContainer.setVisibility(flag);
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
			HashMap<String, String> artistMap = new HashMap<String, String>();
			artistMap.put("artist", gig.getArtist());
			Intent launcher = new Intent( Intent.ACTION_VIEW, Uri.parse(gig.getSpotify()) );
			startActivity(launcher);
		} catch(ActivityNotFoundException anfe) {
			Toast.makeText(this, R.string.artistInfoActivity_no_spotify, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void openYoutube(View v) {
		HashMap<String, String> artistMap = new HashMap<String, String>();
		artistMap.put("artist", gig.getArtist());
		Intent launcher = new Intent( Intent.ACTION_VIEW, Uri.parse(gig.getYoutube()) );
		startActivity(launcher);
	}
}
