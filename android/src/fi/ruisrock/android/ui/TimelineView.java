package fi.ruisrock.android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TimelineView extends View {
	
	public TimelineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setWillNotDraw(false);
	}




	private Canvas canvas;

	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas = canvas;
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(3f);
		
		canvas.drawLine(0f, 0f, 400f, 20f, paint);
		canvas.drawLine(0f, 0f, -400f, 200f, paint);
		canvas.drawLine(0f, 0f, -400f, -400f, paint);
		
		//throw new RuntimeException("YEEEAAAH!");
	}
	
	

}
