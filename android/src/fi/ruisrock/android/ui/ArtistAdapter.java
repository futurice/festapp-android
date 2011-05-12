package fi.ruisrock.android.ui;

import java.util.ArrayList;
import java.util.List;

import fi.ruisrock.android.R;
import fi.ruisrock.android.domain.Gig;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
		timeAndStage.setText(items.get(position).getStageAndTime());
		return view;
	}

}
