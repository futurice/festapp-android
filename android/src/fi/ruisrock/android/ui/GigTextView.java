package fi.ruisrock.android.ui;

import fi.ruisrock.android.R;
import fi.ruisrock.android.domain.Gig;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GigTextView extends TextView {
	
	private static final int PIXELS_PER_MINUTE = 3;
	private Context context;
	private Gig gig;

	public GigTextView(Context context, AttributeSet attrs, Gig gig) {
		super(context, attrs);
		this.context = context;
		this.gig = gig;
		LayoutInflater.from(context).inflate(R.layout.test2, null);
		setText(gig.getArtist());
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int width = PIXELS_PER_MINUTE * gig.getDuration();
		params.width = width;
		setLayoutParams(params);
		setMinimumWidth(width);
	}
	
	public Gig getGig() {
		return gig;
	}
	
	
}
