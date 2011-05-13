package fi.ruisrock.android.ui;

import fi.ruisrock.android.R;
import fi.ruisrock.android.domain.Gig;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class GigRelativeLayout extends RelativeLayout implements View.OnClickListener {
	
	private static final int PIXELS_PER_MINUTE = 3;
	private Context context;
	private Gig gig;

	public GigRelativeLayout(Context context, AttributeSet attrs, Gig gig) {
		super(context, attrs);
		this.context = context;
		this.gig = gig;
		LayoutInflater.from(context).inflate(R.layout.test, this, true);
		setBackgroundResource(R.drawable.schedule_gig);
		TextView label = (TextView) findViewById(R.id.artistName);
		label.setText(gig.getArtist());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		int width = PIXELS_PER_MINUTE * gig.getDuration();
		params.width = width;
		setLayoutParams(params);
		setMinimumWidth(width);
	}
	
	public Gig getGig() {
		return gig;
	}

	@Override
	public void onClick(View v) {
		if (v instanceof GigRelativeLayout) {
			GigRelativeLayout gl = (GigRelativeLayout) v;
			Toast.makeText(context, "GRL " + gig.getArtist(), Toast.LENGTH_SHORT).show();
		}
	}
	
	
	

}
