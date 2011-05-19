package fi.ruisrock.android.ui.map;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MapImageView extends ImageView {
	private SizeCallback callBack;
	private Handler handle;
	private Runnable cbkAction;
	private int width;
	private int height;

	public MapImageView(Context context) {
		super(context);
		cbkAction = new Runnable() {
			public void run() {
				if (callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}

	public MapImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		cbkAction = new Runnable() {
			public void run() {
				if (callBack != null)
					callBack.onSizeChanged(width, height);
			}
		};
	}

	public MapImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
