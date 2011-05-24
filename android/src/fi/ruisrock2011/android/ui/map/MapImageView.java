package fi.ruisrock2011.android.ui.map;

import fi.ruisrock2011.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MapImageView extends ImageView {
	private SizeCallback callBack;
	private Handler handle;
	private Runnable cbkAction;
	private int width;
	private int height;
	private Context context;

	public MapImageView(Context context) {
		super(context);
		this.context = context;
		cbkAction = new Runnable() {
			public void run() {
				if (callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}

	public MapImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		cbkAction = new Runnable() {
			public void run() {
				if (callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}
	
	/*
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// http://stackoverflow.com/questions/2738834/combining-two-png-files-in-android
		
		Bitmap b = BitmapFactory.decodeResource( getResources(), R.drawable.ic_maps_indicator_current_position );
        canvas.drawBitmap(b, 100.0f, 100.0f, null);
	}
	*/

	public MapImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		cbkAction = new Runnable() {
			public void run() {
				if (callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		if (handle != null)
			handle.post(cbkAction);
	}

	public void setCallBack(SizeCallback cbk) {
		callBack = cbk;
	}

	public void setHandle(Handler h) {
		handle = h;
	}
}
