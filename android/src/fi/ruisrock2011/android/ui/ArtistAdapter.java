package fi.ruisrock2011.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.domain.Gig;
import fi.ruisrock2011.android.domain.GigLocation;

public class ArtistAdapter extends BaseAdapter {
	
	private Context context;
	private List<Gig> items = new ArrayList<Gig>();
	private LayoutInflater inflater = null;

	public ArtistAdapter(Context context, List<Gig> items) {
		this.context = context;
		this.items = items;
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position; // Use the array index as a unique id.
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.artist_item, null);
		}
		TextView artist = (TextView) view.findViewById(R.id.gig_artist);
		TextView timeAndStage = (TextView) view.findViewById(R.id.gig_timeAndStage);

		artist.setText(items.get(position).getArtist());
		StringBuilder stageAndTimeText = new StringBuilder("");
		for (GigLocation location : items.get(position).getLocations()) {
			String nl = (stageAndTimeText.length() == 0) ? "" : "\n";
			stageAndTimeText.append(nl + location.getStageAndTime());
		}
		timeAndStage.setText(stageAndTimeText);
		return view;
	}

}
