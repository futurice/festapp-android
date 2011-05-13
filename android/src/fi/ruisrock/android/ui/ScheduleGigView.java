package fi.ruisrock.android.ui;

import java.util.Date;

import fi.ruisrock.android.domain.to.DaySchedule;
import fi.ruisrock.android.util.CalendarUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;


public class ScheduleGigView extends View implements View.OnClickListener {
	
	private static final float MINUTE_IN_PIXELS = 5f;
	
	private DaySchedule daySchedule;
	private Context context;

	public ScheduleGigView(Context context) {
		super(context);
	}

	public ScheduleGigView(Context context, DaySchedule daySchedule) {
		super(context);
		this.context = context;
		this.daySchedule = daySchedule;
		setFocusable(true); //not yet necessary, but you never know what the future brings
		setOnClickListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//canvas.drawColor(0xFFCCCCCC);     //if you want another background color       

		//draw the balls on the canvas
		//canvas.drawBitmap(colorball1.getBitmap(), colorball1.getX(), colorball1.getY(), null);

		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(3f);
		canvas.drawLine(0f, 0f, 100f, 20f, paint);
		
		
		drawTimeline(canvas);

		// refresh the canvas
		//invalidate();
	}
	
	private void drawTimeline(Canvas canvas) {
		Date startTime = daySchedule.getEarliestTime();
		Date endTime = daySchedule.getLatestTime();
		int startMinutes = startTime.getMinutes();
		int minutes = CalendarUtil.getMinutesBetweenTwoDates(startTime, endTime);
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(1f);
		canvas.drawLine(0f, 0f, minutes * MINUTE_IN_PIXELS, -60f, paint);
		minutes += startMinutes;
		for (int i = startMinutes; i <= minutes; i++) {
			if (i % 60 == 0) {
				canvas.drawText("foo", i*MINUTE_IN_PIXELS, -30f,paint);
			}
		}
		
	}

	@Override
	public void onClick(View v) {
		
	}



}