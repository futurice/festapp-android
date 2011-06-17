package fi.ruisrock2011.android.ui;

import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.dao.GigDAO;
import fi.ruisrock2011.android.domain.Gig;

/**
 * UI-widget for representing a Gig on the Timeline-view.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class GigTimelineWidget extends RelativeLayout {
	
	public static final int PIXELS_PER_MINUTE = 5;
	private Gig gig;
	private ToggleButton starIcon;
	private TextView artistLabel;
	private OnCheckedChangeListener favoriteListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			GigDAO.setFavorite(getContext(), gig.getId(), isChecked);
			setFavorite(isChecked);
			
		}
	};

	public GigTimelineWidget(Context context, AttributeSet attrs, Gig gig, Date previousTime) {
		super(context, attrs);
		this.gig = gig;
		LayoutInflater.from(context).inflate(R.layout.gig_timeline_box, this, true);
		artistLabel = (TextView) findViewById(R.id.artistName);
		artistLabel.setText(gig.getArtist());
		
		starIcon = (ToggleButton) findViewById(R.id.starIcon);
		starIcon.setChecked(gig.isFavorite());
		starIcon.setOnCheckedChangeListener(favoriteListener);
		setFavorite(gig.isFavorite());
		if (gig.getOnlyDuration() < 20) {
			starIcon.setVisibility(View.GONE);
		} else {
			starIcon.setVisibility(View.VISIBLE);
		}
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		int width = PIXELS_PER_MINUTE * gig.getOnlyDuration();
		params.width = width;
		int height = (int) getResources().getDimension(R.dimen.timeline_gigHeight);
		params.height = height;
		setLayoutParams(params);
	}
	
	public void setFavorite(boolean fav) {
		gig.setFavorite(fav);
		starIcon.setChecked(fav);
		if (fav) {
			setBackgroundResource(R.drawable.schedule_gig_favorite);
			artistLabel.setTextColor(R.color.timeline_brown);
		} else {
			setBackgroundResource(R.drawable.schedule_gig);
			artistLabel.setTextColor(Color.WHITE);
		}
	}
	
	public Gig getGig() {
		return gig;
	}

}
