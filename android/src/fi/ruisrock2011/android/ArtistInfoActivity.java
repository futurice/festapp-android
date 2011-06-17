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
			infoTable.setBackgroundResource(R.drawable.artist_info_table_bg);
			infoTable.setVisibility(View.VISIBLE);
			StringBuilder stageText = new StringBuilder("");
			StringBuilder timeText = new StringBuilder("");
			for (GigLocation location : gig.getLocations()) {
				String nl = (stageText.length() != 0) ? "\n" : "";
				stageText.append(nl + location.getStage());
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
		if (artist.startsWith("amorphis")) {
			return "spotify:artist:2UOVgpgiNTC6KK0vSC77aD";
		}
		if (artist.startsWith("anna calvi")) {
			return "spotify:artist:50sSN9E5i4DJzYDclAXlSo";
		}
		if (artist.startsWith("anssi 8000")) {
			return "spotify:artist:1qiFMp3603jO86EYOjDIj9";
		}
		if (artist.startsWith("apulanta")) {
			return "spotify:artist:5kwthnxNdfnqGk0nL35wDC";
		}
		if (artist.startsWith("bob hund")) {
			return "spotify:artist:6OZxE19iim1JKvCA3GmCVx";
		}
		if (artist.startsWith("bring me the horizon")) {
			return "spotify:artist:1Ffb6ejR6Fe5IamqA5oRUF";
		}
		if (artist.startsWith("bullet for my valentine")) {
			return "spotify:artist:7iWiAD5LLKyiox2grgfmUT";
		}
		if (artist.startsWith("carpark north")) {
			return "spotify:artist:6v8pFbihIDnlV6freVYMmZ";
		}
		if (artist.startsWith("circle")) {
			return "spotify:artist:0VFRNaQs3xWnI8zSB66MDR";
		}
		if (artist.startsWith("cmx")) {
			return "spotify:artist:7Ip1eXTDPgmMib2tTiJCyG";
		}
		if (artist.startsWith("graveyard")) {
			return "spotify:artist:0hU5urLse5h1Z0b4zQkovL";
		}
		if (artist.startsWith("elbow")) {
			return "spotify:artist:0TJB3EE2efClsYIDQ8V2Jk";
		}
		if (artist.startsWith("felix zenger")) {
			return "spotify:artist:13eYLbVUzgd8RytNSjssVO";
		}
		if (artist.startsWith("fleet foxes")) {
			return "spotify:artist:4EVpmkEwrLYEg6jIsiPMIb";
		}
		if (artist.startsWith("happoradio")) {
			return "spotify:artist:088hJWSolrJNzoNBo2cyOd";
		}
		if (artist.startsWith("hurts")) {
			return "spotify:artist:3w4VAlllkAWI6m0AV0Gn6a";
		}
		if (artist.startsWith("isobel campbell")) {
			return "spotify:artist:6O3XESr2bixxsS9dTY99Rf";
		}
		if (artist.contains("villegalle")) {
			return "spotify:artist:4ASPsiINepImjhptGTwnbH";
		}
		if (artist.startsWith("jarkko martikainen")) {
			return "spotify:artist:466GWVoauVVD15fGVapVKv";
		}
		/*
		if (artist.startsWith("jätkäjätkät")) {
			return null;
		}
		*/
		if (artist.startsWith("jenni vartiainen")) {
			return "spotify:artist:6PP1ZiMzBbTeRqGvhr3pV9";
		}
		if (artist.startsWith("jukka poika")) {
			return "spotify:artist:57ZjZU8vSOeP0Q2hbwh8wn";
		}
		if (artist.startsWith("kaija koo")) {
			return "spotify:artist:60UkyJpgRSEUtcVTasRFEq";
		}
		if (artist.startsWith("kotiteollisuus")) {
			return "spotify:artist:0r9Q7acXxkDPoqfRfAb9Aw";
		}
		if (artist.equals("lama")) {
			return "spotify:artist:4fePAqrqNX0ezZxNTDgdov";
		}
		if (artist.startsWith("magenta skycode")) {
			return "spotify:artist:50ExyXXZnoYUrtmAJguRMk";
		}
		if (artist.startsWith("manu chao")) {
			return "spotify:artist:6wH6iStAh4KIaWfuhf0NYM";
		}
		/*
		if (artist.startsWith("michael monroe")) {
			return R.drawable.artistimg_michael_monroe;
		}
		*/
		if (artist.startsWith("mirel wagner")) {
			return "spotify:artist:599W9qxs3LzkiiEmkoK6Kx";
		}
		if (artist.startsWith("olavi uusivirta")) {
			return "spotify:artist:5LbUBFEG2qciScT9kwFqmV";
		}
		if (artist.startsWith("paleface")) {
			return "spotify:artist:5CNgBVZXUEEGqyzgjUAgDj";
		}
		if (artist.startsWith("paradise oskar")) {
			return "spotify:artist:15ksAhjnYhuK2wOZCYg9g3";
		}
		if (artist.startsWith("paramore")) {
			return "spotify:artist:74XFHRwlV6OrjEM0A2NCMF";
		}
		if (artist.startsWith("pariisin kev")) {
			return "spotify:artist:2Bj3YsSKo7O5bj3Ku6z1Ny";
		}
		if (artist.startsWith("pelle miljoona")) {
			return "spotify:artist:3b9AnZICKvmVlgG2nKEplU";
		}
		/*
		if (artist.startsWith("pertti kurikan nimi")) {
			return R.drawable.artistimg_pertti_kurikan_np;
		}
		*/
		if (artist.startsWith("pmmp")) {
			return "spotify:artist:6LUnsRyqOZdHGTZqMlWVV2";
		}
		if (artist.startsWith("primus")) {
			return "spotify:artist:64mPnRMMeudAet0E62ypkx";
		}
		if (artist.startsWith("raappana")) {
			return "spotify:artist:7m7tqSW9C7alkliDAZYvF0";
		}
		if (artist.startsWith("robyn")) {
			return "spotify:artist:6UE7nl9mha6s8z0wFQFIZ2";
		}
		if (artist.startsWith("rubik")) {
			return "spotify:artist:0xE2i2PABOl0mrd4cNT40j";
		}
		if (artist.startsWith("sabaton")) {
			return "spotify:artist:3o2dn2O0FCVsWDFSh8qxgG";
		}
		if (artist.startsWith("scandinavian music group")) {
			return "spotify:artist:773p3GT2SYlZUbqTo1nXKI";
		}
		if (artist.startsWith("stam1na")) {
			return "spotify:artist:41nB823nb3wxEI25UeGHqG";
		}
		if (artist.startsWith("sweatmaster")) {
			return "spotify:artist:2GhrB6KvD9FsgG81kMsD89";
		}
		if (artist.startsWith("the capital beat")) {
			return "spotify:artist:7kQ4odJphDeYdON7v0H5Yr";
		}
		if (artist.startsWith("the freza")) {
			return "spotify:artist:30ubXnWx23pTD0EQOm5I1D";
		}
		if (artist.startsWith("the national")) {
			return "spotify:artist:2cCUtGK9sDU2EoElnk0GNB";
		}
		if (artist.startsWith("the prodigy") || artist.equals("prodigy")) {
			return "spotify:artist:1GwxXgEc6oxCKQ5wykWXFs";
		}
		if (artist.startsWith("tuomari nurmio")) {
			return "spotify:artist:7zBD3u7aYHxNPlLWc5CAdh";
		}
		if (artist.startsWith("uusi fantasia")) {
			return "spotify:artist:7K1JNuo3rYr7MNgIXAyysC";
		}
		if (artist.startsWith("von hertzen brothers")) {
			return "spotify:artist:5QA702pGd9qa2oWvp21ofG";
		}
		
		// Search for artist
		return "spotify:search:artist:"+artist;
	}
	

}
