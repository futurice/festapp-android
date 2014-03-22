package com.futurice.festapp;

import java.util.List;

import com.flurry.android.FlurryAgent;
import com.futurice.festapp.dao.GigDAO;
import com.futurice.festapp.domain.Gig;
import com.futurice.festapp.ui.ArtistAdapter;
import com.futurice.festapp.util.StringUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.futurice.festapp.R;

/**
 * View for listing Artists.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class ArtistListActivity extends BaseActivity {
	
	private ListView artistList;
	private List<Gig> gigs;
	private OnItemClickListener artistClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int index, long arg) {
			Object o = av.getItemAtPosition(index);
			if (o instanceof Gig) {
				Gig gig = (Gig) o;
				if (StringUtil.isNotEmpty(gig.getId())) {
				    Intent artistInfo = new Intent(getBaseContext(), ArtistInfoActivity.class);
				    artistInfo.putExtra("gig.id", gig.getId());
				    startActivity(artistInfo);
					return;
				}
			}
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artists);
		createArtistList();
		FlurryAgent.logEvent("Artistit");
	}
	
	private void createArtistList() {
		artistList = (ListView) findViewById(R.id.artistList);
		gigs = GigDAO.findAllActive(this);
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View header = inflater.inflate(R.layout.list_header, null, false);

		((TextView)header.findViewById(R.id.listTitle)).setText(getResources().getString(R.string.Artists));

		artistList.addHeaderView(header);
		
	    artistList.setAdapter(new ArtistAdapter(this, gigs));
	    artistList.setOnItemClickListener(artistClickListener);
	}
	
	
	

}
