package fi.ruisrock2011.android;

import java.util.List;

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
import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.ui.ArtistAdapter;
import fi.ruisrock2011.android.util.StringUtil;

/**
 * View for listing Artists.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class ArtistListActivity extends Activity {
	
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
