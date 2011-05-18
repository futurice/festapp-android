package fi.ruisrock.android.ui;

import fi.ruisrock.android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ScrollingImageView extends AbsoluteLayout implements OnGestureListener {

	private GestureDetector mGestureDetector;
	private ImageView mImageView;

	public ScrollingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mImageView = new ImageView(context);
		mImageView.setImageResource(R.drawable.map);
		this.addView(mImageView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setIsLongpressEnabled(false);

	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		int scrollWidth = mImageView.getWidth() - this.getWidth();
		if ((this.getScrollX() >= 0) && (this.getScrollX() <= scrollWidth) && (scrollWidth > 0)) {
			int moveX = (int) distanceX;
			if (((moveX + this.getScrollX()) >= 0) && ((Math.abs(moveX) + Math.abs(this.getScrollX())) <= scrollWidth)) {
				this.scrollBy(moveX, 0);
			} else {
				if (distanceX >= 0) {
					this.scrollBy(scrollWidth - Math.max(Math.abs(moveX), Math.abs(this.getScrollX())), 0);
				} else {
					this.scrollBy(-Math.min(Math.abs(moveX), Math.abs(this.getScrollX())), 0);
				}
			}
		}

		int scrollHeight = mImageView.getHeight() - this.getHeight();
		if ((this.getScrollY() >= 0) && (this.getScrollY() <= scrollHeight) && (scrollHeight > 0)) {
			int moveY = (int) distanceY;
			if (((moveY + this.getScrollY()) >= 0) && ((Math.abs(moveY) + Math.abs(this.getScrollY())) <= scrollHeight)) {
				this.scrollBy(0, moveY);
			} else {
				if (distanceY >= 0) {
					this.scrollBy(0, scrollHeight - Math.max(Math.abs(moveY), Math.abs(this.getScrollY())));
				} else {
					this.scrollBy(0, -Math.min(Math.abs(moveY), Math.abs(this.getScrollY())));
				}
			}
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		return true;
	}

	public void zoomIn() {
		int h = mImageView.getHeight();
		int w = mImageView.getWidth();
		h *= 2;
		w *= 2;
		this.removeAllViews();
		this.addView(mImageView, new LinearLayout.LayoutParams(w, h));
	}

	public void zoomOut() {
		this.scrollTo(0, 0);
		int h = mImageView.getHeight();
		int w = mImageView.getWidth();
		h /= 2;
		w /= 2;
		this.removeAllViews();
		this.addView(mImageView, new LinearLayout.LayoutParams(w, h));
	}
}
