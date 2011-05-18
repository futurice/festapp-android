package fi.ruisrock.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		/*
		final ImageView switcherView = (ImageView) this.findViewById(R.id.img);

		int viewWidth = switcherView.getWidth();
		int viewHeight = switcherView.getHeight();
		int mapWidth = getResources().getDrawable(R.drawable.map).getIntrinsicWidth();
		int mapHeight = getResources().getDrawable(R.drawable.map).getIntrinsicHeight();
		
		
	    // set maximum scroll amount (based on center of image)
		int maxX = (int)((mapWidth / 2) - (viewWidth / 2));
		int maxY = (int)((mapHeight / 2) - (viewHeight / 2));
	    //int maxX = (int)((bitmapWidth / 2) - (screenWidth / 2));
	    //int maxY = (int)((bitmapHeight / 2) - (screenHeight / 2));

	    // set scroll limits
	    final int maxLeft = (maxX * -1);
	    final int maxRight = maxX;
	    final int maxTop = (maxY * -1);
	    final int maxBottom = maxY;

	    switcherView.setOnTouchListener(new View.OnTouchListener()
	    {
	        float downX, downY;
	        int totalX, totalY;
	        int scrollByX, scrollByY;
	        public boolean onTouch(View view, MotionEvent event)
	        {
	            float currentX, currentY;
	            switch (event.getAction())
	            {
	                case MotionEvent.ACTION_DOWN:
	                    downX = event.getX();
	                    downY = event.getY();
	                    break;

	                case MotionEvent.ACTION_MOVE:
	                    currentX = event.getX();
	                    currentY = event.getY();
	                    scrollByX = (int)(downX - currentX);
	                    scrollByY = (int)(downY - currentY);

	                    // scrolling to left side of image (pic moving to the right)
	                    if (currentX > downX)
	                    {
	                        if (totalX == maxLeft)
	                        {
	                            scrollByX = 0;
	                        }
	                        if (totalX > maxLeft)
	                        {
	                            totalX = totalX + scrollByX;
	                        }
	                        if (totalX < maxLeft)
	                        {
	                            scrollByX = maxLeft - (totalX - scrollByX);
	                            totalX = maxLeft;
	                        }
	                    }

	                    // scrolling to right side of image (pic moving to the left)
	                    if (currentX < downX)
	                    {
	                        if (totalX == maxRight)
	                        {
	                            scrollByX = 0;
	                        }
	                        if (totalX < maxRight)
	                        {
	                            totalX = totalX + scrollByX;
	                        }
	                        if (totalX > maxRight)
	                        {
	                            scrollByX = maxRight - (totalX - scrollByX);
	                            totalX = maxRight;
	                        }
	                    }

	                    // scrolling to top of image (pic moving to the bottom)
	                    if (currentY > downY)
	                    {
	                        if (totalY == maxTop)
	                        {
	                            scrollByY = 0;
	                        }
	                        if (totalY > maxTop)
	                        {
	                            totalY = totalY + scrollByY;
	                        }
	                        if (totalY < maxTop)
	                        {
	                            scrollByY = maxTop - (totalY - scrollByY);
	                            totalY = maxTop;
	                        }
	                    }

	                    // scrolling to bottom of image (pic moving to the top)
	                    if (currentY < downY)
	                    {
	                        if (totalY == maxBottom)
	                        {
	                            scrollByY = 0;
	                        }
	                        if (totalY < maxBottom)
	                        {
	                            totalY = totalY + scrollByY;
	                        }
	                        if (totalY > maxBottom)
	                        {
	                            scrollByY = maxBottom - (totalY - scrollByY);
	                            totalY = maxBottom;
	                        }
	                    }

	                    switcherView.scrollBy(scrollByX, scrollByY);
	                    downX = currentX;
	                    downY = currentY;
	                    break;

	            }

	            return true;
	        }
	    });
	    */

	}

}
