package fi.ruisrock.android.ui;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import fi.ruisrock.android.ArtistInfoActivity;
import fi.ruisrock.android.R;
import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.domain.Gig;
import fi.ruisrock.android.util.CalendarUtil;

public class GigTimelineWidget extends RelativeLayout {
	
	public static final int PIXELS_PER_MINUTE = 5;
	private Gig gig;
	private ToggleButton starIcon;
	private OnCheckedChangeListener favoriteListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			GigDAO.setFavorite(getContext(), gig.getId(), isChecked);
			setFavorite(isChecked);
			if (isChecked) {
				Toast.makeText(getContext(), getResources().getString(R.string.artistInfoActivity_favoriteOn), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getContext(), getResources().getString(R.string.artistInfoActivity_favoriteOff), Toast.LENGTH_SHORT).show();
			}
		}
		/*
		@Override
		public void onClick(View v) {
			if (v.equals(starIcon)) {
				boolean isFavorite = starIcon.isChecked();
				GigDAO.setFavorite(getContext(), gig.getId(), isFavorite);
				gig.setFavorite(isFavorite);
				if (isFavorite) {
					Toast.makeText(getContext(), getResources().getString(R.string.artistInfoActivity_favoriteOn), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), getResources().getString(R.string.artistInfoActivity_favoriteOff), Toast.LENGTH_SHORT).show();
				}
			}
		}
		*/
		
		
	};

	public GigTimelineWidget(Context context, AttributeSet attrs, Gig gig, Date previousTime) {
		super(context, attrs);
		this.gig = gig;
		LayoutInflater.from(context).inflate(R.layout.gig_timeline_box, this, true);
		TextView label = (TextView) findViewById(R.id.artistName);
		label.setText(gig.getArtist());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		/*
		if (previousTime.before(gig.getStartTime())) {
			int margin = PIXELS_PER_MINUTE * CalendarUtil.getMinutesBetweenTwoDates(previousTime, gig.getStartTime());
			params.leftMargin = margin;
			params.setMargins(400, 0, 0, 0);
		}
		*/
		
		starIcon = (ToggleButton) findViewById(R.id.starIcon);
		starIcon.setChecked(gig.isFavorite());
		starIcon.setOnCheckedChangeListener(favoriteListener);
		setFavorite(gig.isFavorite());
		
		int width = PIXELS_PER_MINUTE * gig.getDuration();
		params.width = width;
		setLayoutParams(params);
	}
	
	public void setFavorite(boolean fav) {
		gig.setFavorite(fav);
		starIcon.setChecked(fav);
		if (fav) {
			setBackgroundResource(R.drawable.schedule_gig_favorite);
		} else {
			setBackgroundResource(R.drawable.schedule_gig);
		}
	}
	
	/*
	public ToggleButton getStarIcon() {
		return starIcon;
	}
	*/
	
	public Gig getGig() {
		return gig;
	}

}
