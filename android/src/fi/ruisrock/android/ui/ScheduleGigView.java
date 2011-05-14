package fi.ruisrock.android.ui;

import java.util.Date;

import fi.ruisrock.android.domain.to.DaySchedule;
import fi.ruisrock.android.util.CalendarUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class ScheduleGigView extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final float MINUTE_IN_PIXELS = 5f;
	
	private DaySchedule daySchedule;
	private Context context;
	
	private TutorialThread _thread;

	public ScheduleGigView(Context context) {
		super(context);
	}
	
	public ScheduleGigView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScheduleGigView(Context context, DaySchedule daySchedule) {
		super(context);
		getHolder().addCallback(this);
		this.context = context;
		this.daySchedule = daySchedule;
		setFocusable(true); //not yet necessary, but you never know what the future brings
		
		_thread = new TutorialThread(getHolder(), this);
		
	}
	
	public void setDaySchedule(DaySchedule daySchedule) {
		this.daySchedule = daySchedule;
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
		canvas.drawLine(0f, 0f, 400f, 20f, paint);
		
		
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
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		System.out.println("why?");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	class TutorialThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private ScheduleGigView _panel;
        private boolean _run = false;
 
        public TutorialThread(SurfaceHolder surfaceHolder, ScheduleGigView panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
        }
 
        public void setRunning(boolean run) {
            _run = run;
        }
 
        @Override
        public void run() {
            Canvas c;
            while (_run) {
                c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        _panel.onDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }


}