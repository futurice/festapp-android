package com.futurice.festapp.ui;

import java.util.List;

import com.futurice.festapp.domain.Gig;
import com.futurice.festapp.domain.GigLocation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futurice.festapp.R;

public class ArtistAdapter extends ListAdapter<Gig> {

	public ArtistAdapter(Context context, List<Gig> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.artist_item, null);
		}
		TextView artist = (TextView) view.findViewById(R.id.gig_artist);
		TextView timeAndStage = (TextView) view.findViewById(R.id.gig_timeAndStage);

		Gig gig = (Gig) getItem(position);
		artist.setText(gig.getArtist());
		StringBuilder stageAndTimeText = new StringBuilder();
		for (GigLocation location : gig.getLocations()) {
			stageAndTimeText.append((stageAndTimeText.length() == 0) ? "" : "\n");
			stageAndTimeText.append(location.getStageAndTime());
		}
		timeAndStage.setText(stageAndTimeText);
		return view;
	}

}
