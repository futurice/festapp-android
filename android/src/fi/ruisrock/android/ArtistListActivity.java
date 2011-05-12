package fi.ruisrock.android;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.domain.Gig;
import fi.ruisrock.android.ui.ArtistAdapter;

public class ArtistListActivity extends Activity {
	
	private ListView artistList;
	private List<Gig> gigs;
	
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
		
	    artistList.setAdapter(new ArtistAdapter(this, gigs));
	    //artistList.setOnItemClickListener(newsArticleClickListener);
	}
	
	
	

}
