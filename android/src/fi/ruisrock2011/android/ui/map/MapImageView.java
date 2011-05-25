package fi.ruisrock2011.android.ui.map;

import fi.ruisrock2011.android.R;
import fi.ruisrock2011.android.util.RuisrockConstants;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.location.Location;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MapImageView extends ImageView {
	private SizeCallback callBack;
	private Handler handle;
	private Runnable cbkAction;
	private int width;
	private int height;
	private Context context;
	private int currentPositionId;
	private Location currentPosition;
	RectF rect;
	private float current_scale;

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
		canvas.drawBitmap(getCurrentPosition(), 1512, 1118, null);
	}
	*/
	
	/*
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// http://stackoverflow.com/questions/2738834/combining-two-png-files-in-android
		
		float x = 1512;
		float y = 1118;
		
		float left = (rect.left > 0) ? rect.left : 0;
		float right = (rect.right > 0) ? rect.right : width;
		float top = (rect.top > 0) ? rect.top : 0;
		float bottom = (rect.bottom > 0) ? rect.bottom : height;
		
		boolean tooLeft = x < left;
		boolean tooRight = x > right;
		boolean tooTop = y < top;
		boolean tooBottom = y > bottom;
		
		if (tooLeft || tooRight || tooTop || tooBottom) {
			//
		} else {
			
			float xFactor = getResources().getDisplayMetrics().xdpi / 160;
			float yFactor = getResources().getDisplayMetrics().ydpi / 160;
			
			float currentX = ((x - left) * xFactor)*current_scale;
			float currentY = ((y - top) * yFactor)*current_scale;
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.leftMargin = (int) (currentX - left);
			lp.topMargin = (int) (currentY - top);
			//lp.width = 40;
			//lp.height = 40;
			
			canvas.drawBitmap(getCurrentPosition(), currentY, currentX, null);
		}
	}
	*/
	
	private Bitmap getCurrentPosition() {
		switch (currentPositionId) {
		case 1:
			currentPositionId = 2;
			break;
		case 2:
			currentPositionId = 3;
			break;
		default:
			currentPositionId = 1;
			break;
		}
		
		int imageId = getResources().getIdentifier("ic_maps_indicator_current_position_anim" + currentPositionId, "drawable", context.getPackageName());
		
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inScaled = false;
		return BitmapFactory.decodeResource(getResources(), imageId, opts);
	}
	
	

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
	
	public void setCurrentPosition(Location location, RectF rect, float scale) {
		this.currentPosition = location;
		this.rect = rect;
		this.current_scale = scale;
		invalidate();
	}
}
