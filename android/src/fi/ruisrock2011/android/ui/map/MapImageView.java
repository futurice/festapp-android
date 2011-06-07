package fi.ruisrock2011.android.ui.map;

import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MapImageView extends ImageView {
	private SizeCallback callBack;
	private Handler handle;
	private Runnable cbkAction;
	private int width;
	private int height;
	RectF rect;

	public MapImageView(Context context) {
		super(context);
		init();
	}
	
	public MapImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MapImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
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
