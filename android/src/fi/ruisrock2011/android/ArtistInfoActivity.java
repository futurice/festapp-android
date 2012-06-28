package fi.ruisrock2011.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import fi.ruisrock2011.android.domain.GigLocation;
import fi.ruisrock2011.android.util.RuisrockConstants;
import fi.ruisrock2011.android.util.StringUtil;
import fi.ruisrock2011.android.util.UIUtil;

/**
 * View for Artist-info.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class ArtistInfoActivity extends Activity {
	
	private RelativeLayout artistInfoView;
	private Drawable spotifyIcon;
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
		//final String key = "showFavoriteAndSpotifyInfo";
		final String key = RuisrockConstants.PREFERENCE_SHOW_FAVORITE_INFO;
		
		// TODO: Start DEBUG
		/*
		Editor ed = pref.edit();
		ed.putBoolean(key, true);
		ed.commit();
		*/
		// END DEBUG
		
		if (pref.getBoolean(key, true)) {
			/*
			if (!isSpotifyInstalled() && pref.getBoolean(RuisrockConstants.PREFERENCE_SHOW_FAVORITE_INFO, true)) {
				
			}
			*/
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
			
			TableLayout infoTable = (TableLayout) findViewById(R.id.artistInfoTable);
//			infoTable.setBackgroundResource(R.drawable.artist_info_table_bg);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isSpotifyInstalled()) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.artist_info_menu, menu);
			if (spotifyIcon != null) {
				MenuItem item = menu.findItem(R.id.openSpotify);
				item.setIcon(spotifyIcon);
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		switch (itemId) {
		case R.id.openSpotify:
			openSpotifyIntent(gig.getArtist());
			break;
		}
		return false;
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
	
	private void openSpotifyIntent(String artist) {
		String spotifyUri = createSpotifyUri(artist);
		if (spotifyUri != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUri));
			startActivity(intent);
		}
	}
	
	private boolean isSpotifyInstalled() {
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo("com.spotify.mobile.android.ui", PackageManager.GET_ACTIVITIES);
			if (pi.applicationInfo.icon != 0) {
				spotifyIcon = pi.applicationInfo.loadIcon(pm);
			} else {
				spotifyIcon = null;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private String createSpotifyUri(String artist) {
		if (StringUtil.isEmpty(artist)) {
			return null;
		}
		
		artist = artist.toLowerCase().trim();
		artist = artist.replaceAll("\\([a-z]+\\)$", "");
		artist = artist.trim();
		if (artist.startsWith("apocalyptica")) {
			return "spotify:artist:4Lm0pUvmisUHMdoky5ch2I";
		}
		if (artist.startsWith("apulanta")) {
			return "spotify:artist:5kwthnxNdfnqGk0nL35wDC";
		}
		if (artist.startsWith("black twig")) {
			return "spotify:artist:79AcJfuSU5H7rkKOuPG4y1";
		}
		if (artist.startsWith("bloc party")) {
			return "spotify:artist:3MM8mtgFzaEJsqbjZBSsHJ";
		}
		if (artist.startsWith("burning hearts")) {
			return "spotify:artist:07RLOidpB4QSAETF8ZlR5W";
		}
		if (artist.startsWith("children of bodom")) {
			return "spotify:artist:1xUhNgw4eJDZfvumIpcz1B";
		}
		if (artist.startsWith("chisu")) {
			return "spotify:artist:0Wo2rgrMZVYn0u0uxlDPJP";
		}
		if (artist.startsWith("disco ensemble")) {
			return "spotify:artist:0027wHZDQXpRll4ckwDGad";
		}
		if (artist.startsWith("elokuu")) {
			return "spotify:artist:0LqzBv7bOc73BPn8du1K2o";
		}
		if (artist.startsWith("eppu normaali")) {
			return "spotify:artist:6KbCFHgM4basg1TvK3oEzv";
		}
		if (artist.startsWith("ewert and the two dragons")) {
			return "spotify:artist:1D34O3ftIxPfbMN7bVVSPS";
		}
		if (artist.startsWith("fintelligens")) {
			return "spotify:artist:17LOJRoDtwnUqJzspDoFSy";
		}
		if (artist.startsWith("flogging molly")) {
			return "spotify:artist:5kQGFREO5FzMBMsAO3cEtj";
		}
		if (artist.startsWith("french films")) {
			return "spotify:artist:4AOMGVao7fbfxQE7ON6Qml";
		}
		if (artist.startsWith("gg caravan")) {
			return "spotify:artist:2nbvM2nsgPcAeUxiDPC6FX";
		}
		if (artist.startsWith("gracias")) {
			return "spotify:artist:2GRNS6DnefxBzYSZXDc6ij";
		}
		if (artist.startsWith("herra ylpp")) {
			return "spotify:artist:4St5yIsy2GgMO3dRxxtxAo";
		}
		if (artist.startsWith("huoratron")) {
			return "spotify:artist:2SRSJhAtFtJRhizJMdf0Gt";
		}
		if (artist.contains("jimmy cliff")) {
			return "spotify:artist:3rJ3m1tM6vUgiWLjfV8sRf";
		}
		if (artist.startsWith("jukka poika")) {
			return "spotify:artist:57ZjZU8vSOeP0Q2hbwh8wn";
		}
		if (artist.startsWith("kauko r")) {
			return "spotify:artist:1gdEQi5Qd5hYKMYeSCwnLK";
		}
		if (artist.startsWith("kuningasidea")) {
			return "spotify:artist:7EuXVmTcFfpvmFbi1CTctP";
		}
		if (artist.startsWith("lapko")) {
			return "spotify:artist:7c3qr8krIQX4LbiU0KHXCX";
		}
		if (artist.startsWith("metronomy")) {
			return "spotify:artist:54QMjE4toDfiCryzYWCpXX";
		}
		if (artist.startsWith("michael monroe")) {
			return "spotify:artist:5Ul6r5lUSOraWUidNnsILZ";
		}
		if (artist.startsWith("moonface")) {
			return "spotify:artist:0h1JqR0KD88ru6f5yUd7Lh";
		}
		if (artist.startsWith("mustasch")) {
			return "spotify:artist:7ig8pUnno95YNA9MclOveH";
		}
		if (artist.startsWith("nightwish")) {
			return "spotify:artist:2NPduAUeLVsfIauhRwuft1";
		}
		if (artist.startsWith("notkea rotta")) {
			return "spotify:artist:2FrQM3hYDld2cLxy5vXuU7";
		}
		if (artist.startsWith("olavi uusivirta")) {
			return "spotify:artist:5LbUBFEG2qciScT9kwFqmV";
		}
		if (artist.startsWith("paleface")) {
			return "spotify:artist:5CNgBVZXUEEGqyzgjUAgDj";
		}
		if (artist.startsWith("pariisin kev")) {
			return "spotify:artist:2Bj3YsSKo7O5bj3Ku6z1Ny";
		}
		if (artist.startsWith("pasa")) {
			return "spotify:artist:09W3yLwRPQ77POxGCNfLc7";
		}
		if (artist.startsWith("pmmp")) {
			return "spotify:artist:6LUnsRyqOZdHGTZqMlWVV2";
		}
		if (artist.startsWith("pulp")) {
			return "spotify:artist:36E7oYfz3LLRto6l2WmDcD";
		}
		if (artist.startsWith("refused")) {
			return "spotify:artist:5sdxGvwxI1TkTA6Pu2jnTb";
		}
		if (artist.startsWith("regina")) {
			return "spotify:artist:4COCsob5jcnZPAe3QjARrc";
		}
		if (artist.startsWith("rival sons")) {
			return "spotify:artist:356c8AN5YWKvz86B4Sb1yf";
		}
		if (artist.startsWith("robin")) {
			return "spotify:artist:4Q4b4S784htx6DtxcMUfMO";
		}
		if (artist.startsWith("ruudolf")) {
			return "spotify:artist:2AHLeXCzAB77q0680DNtr5";
		}
		if (artist.startsWith("santigold")) {
			return "spotify:artist:6Jrxnp0JgqmeUX1veU591p";
		}
		if (artist.startsWith("scandinavian music group")) {
			return "spotify:artist:773p3GT2SYlZUbqTo1nXKI";
		}
		if (artist.startsWith("snoop dogg")) {
			return "spotify:artist:7hJcb9fa4alzcOq3EaNPoG";
		}
		if (artist.startsWith("stam1na")) {
			return "spotify:artist:41nB823nb3wxEI25UeGHqG";
		}
		if (artist.startsWith("stig")) {
			return "spotify:artist:6TKvvwslcx2bKwiX2aBxbd";
		}
		if (artist.startsWith("stockers")) {
			return "spotify:artist:6hfIf033ub2boheR8K6Aez";
		}
		if (artist.startsWith("suicidal tendencies")) {
			return "spotify:artist:3WPKDlucMsXH6FC1XaclZC";
		}
		if (artist.startsWith("the cardigans")) {
			return "spotify:artist:1tqZaCwM57UFKjWoYwMLrw";
		}
		if (artist.startsWith("the mars volta")) {
			return "spotify:artist:75U40yZLLPglFgXbDVnmVs";
		}
		if (artist.startsWith("the rasmus")) {
			return "spotify:artist:76ptJV8617638xrpeoUtzl";
		}
		if (artist.startsWith("two door cinema club")) {
			return "spotify:artist:536BYVgOnRky0xjsPT96zl";
		}
		if (artist.startsWith("veronica maggio")) {
			return "spotify:artist:2OIWxN9xUhgUHkeUCWCaNs";
		}
		if (artist.startsWith("von hertzen brothers")) {
			return "spotify:artist:5QA702pGd9qa2oWvp21ofG";
		}
		if (artist.startsWith("yeasayer")) {
			return "spotify:artist:04HvbIwBccFmRie5ATX4ft";
		}
		if (artist.startsWith("zebra and snake")) {
			return "spotify:artist:6rhdTr9TbMSfQ1CzkVY8V1";
		}
		
		// Search for artist
		return "spotify:search:artist:"+artist;
	}
	

}
