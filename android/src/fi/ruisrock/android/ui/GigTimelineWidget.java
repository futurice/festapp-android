package fi.ruisrock.android.ui;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.ruisrock.android.R;
import fi.ruisrock.android.domain.Gig;
import fi.ruisrock.android.util.CalendarUtil;

public class GigTimelineWidget extends RelativeLayout {
	
	public static final int PIXELS_PER_MINUTE = 5;
	private Gig gig;
	private ImageView starIcon;

	public GigTimelineWidget(Context context, AttributeSet attrs, Gig gig, Date previousTime) {
		super(context, attrs);
		this.gig = gig;
		LayoutInflater.from(context).inflate(R.layout.gig_timeline_box, this, true);
		setBackgroundResource(R.drawable.schedule_gig);
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
		
		starIcon = (ImageView) findViewById(R.id.starIcon);
		
		int width = PIXELS_PER_MINUTE * gig.getDuration();
		params.width = width;
		setLayoutParams(params);
	}
	
	public ImageView getStarIcon() {
		return starIcon;
	}
	
	public Gig getGig() {
		return gig;
	}

}
