package com.futurice.festapp;

import java.util.Timer;
import com.futurice.festapp.dao.GigDAO;
import com.futurice.festapp.domain.to.StageType;
import com.futurice.festapp.ui.map.MapAnimation;
import com.futurice.festapp.ui.map.MapAnimationCallback;
import com.futurice.festapp.ui.map.MapImageView;
import com.futurice.festapp.ui.map.SizeCallback;
import com.futurice.festapp.util.FestAppConstants;
import com.futurice.festapp.util.UIUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;
import com.futurice.festapp.R;

/**
 * View for festival Map.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class MapActivity extends Activity {
	
	private MapImageView mapImageView;
	private ImageButton zoomInButton;
	private ImageButton zoomOutButton;
	private Matrix matrix;
	private RectF sourceRect;
	private RectF destinationRect;
	private Bitmap bitmap;
	private Timer timer;
	private MapAnimation animation;
	private Toast mapToast;
	private Handler handle = new Handler();

	private int imageSizeX = 2953;
	private int imageSizeY = 2126;
	private static final float INITIAL_SCALE = (float) 1;
	private static final float MAGNIFY_SCALE = (float) 1.9;

	private float current_scale = INITIAL_SCALE;
	private int current_centerX = imageSizeX / 2;
	private int current_centerY = imageSizeY / 2;
	private int current_drawable = R.drawable.map_2013;

	private int moveHistorySize;
	private float lastTwoXMoves[] = new float[2];
	private float lastTwoYMoves[] = new float[2];
	private long downTimer;
	
	private BitmapFactory.Options opts = new BitmapFactory.Options();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		opts.inScaled = false;
		mapImageView = (MapImageView) findViewById(R.id.image);
		zoomInButton = (ImageButton) findViewById(R.id.zoomIn);
		zoomOutButton = (ImageButton) findViewById(R.id.zoomOut);
		
		sourceRect = new RectF();
		destinationRect = new RectF();
		matrix = new Matrix();

		if (savedInstanceState != null) {
			current_centerX = savedInstanceState.getInt("centerX");
			current_centerY = savedInstanceState.getInt("centerY");
			current_scale = savedInstanceState.getFloat("scale");
			current_drawable = savedInstanceState.getInt("drawable");
			imageSizeX = savedInstanceState.getInt("sizeX");
			imageSizeY = savedInstanceState.getInt("sizeY");
		}

		timer = new Timer();
		animation = new MapAnimation(handle, current_centerX, current_centerY, current_scale);

		mapImageView.setHandle(handle);
		mapImageView.setCallBack(sizeCallback);

		animation.stopProcess();
		animation.setCallBack(animationCallBack);
		timer.scheduleAtFixedRate(animation, 200, 30);

		mapImageView.setOnTouchListener(mapTouchListener);
		zoomInButton.setOnClickListener(zoomInListener);
		zoomOutButton.setOnClickListener(zoomOutListener);

		bitmap = BitmapFactory.decodeResource(getResources(), current_drawable, opts);
		imageSizeX = bitmap.getWidth();
		imageSizeY = bitmap.getHeight();

		mapImageView.setImageBitmap(bitmap);
		mapImageView.getDrawable().setFilterBitmap(true);
		mapImageView.setImageMatrix(matrix);
		
		showInitialInfoDialog();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onRestoreInstanceState(Bundle inState) {
		current_centerX = inState.getInt("centerX");
		current_centerY = inState.getInt("centerY");
		current_scale = inState.getFloat("scale");
		current_drawable = inState.getInt("drawable");
		imageSizeX = inState.getInt("sizeX");
		imageSizeY = inState.getInt("sizeY");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("centerX", current_centerX);
		outState.putInt("centerY", current_centerY);
		outState.putFloat("scale", current_scale);
		outState.putInt("drawable", current_drawable);
		outState.putInt("sizeX", imageSizeX);
		outState.putInt("sizeY", imageSizeY);
	}

	public void onDestroy() {
		if (!bitmap.isRecycled())
			bitmap.recycle();
		super.onDestroy();
	}

	private OnTouchListener mapTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {			
			long downUpTime = event.getEventTime() - downTimer;
			if (event.getAction() == MotionEvent.ACTION_UP && downUpTime < 150) {
				handleMapOnClick(event.getX(), event.getY());
				return true;
			}

			if ((event.getAction() == MotionEvent.ACTION_MOVE)) {

				moveHistorySize++;
				lastTwoXMoves[1] = lastTwoXMoves[0];
				lastTwoXMoves[0] = event.getX();
				lastTwoYMoves[1] = lastTwoYMoves[0];
				lastTwoYMoves[0] = event.getY();

				if (moveHistorySize >= 2) {
					current_centerX += (int) ((lastTwoXMoves[1] - lastTwoXMoves[0]) * (imageSizeX / current_scale) / mapImageView.getWidth());
					current_centerY += (int) ((lastTwoYMoves[1] - lastTwoYMoves[0]) * (imageSizeY / current_scale) / mapImageView.getHeight());

					updateDisplay();
				}
			} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
				animation.stopProcess();
				lastTwoXMoves[0] = event.getX();
				lastTwoYMoves[0] = event.getY();
				downTimer = event.getEventTime();
				moveHistorySize = 1;
			} else if ((event.getAction() == MotionEvent.ACTION_UP) && (moveHistorySize >= 1)) {

				if (event.getEventTime() != downTimer) {
					float speedX = (lastTwoXMoves[1] - lastTwoXMoves[0]) * (imageSizeX / current_scale) / mapImageView.getWidth();
					float speedY = (lastTwoYMoves[1] - lastTwoYMoves[0]) * (imageSizeY / current_scale) / mapImageView.getHeight();

					speedX /= event.getEventTime() - downTimer;
					speedY /= event.getEventTime() - downTimer;

					speedX *= 30;
					speedY *= 30;

					animation.setInfo(speedX, speedY, current_centerX, current_centerY);
				}
			}

			return true;
		}
	};
	
	private void handleMapOnClick(float displayX, float displayY) {
		RectF rect = sourceRect;
		
		float left = (rect.left > 0) ? rect.left : 0;
		float right = (rect.right > 0) ? rect.right : imageSizeX;
		float top = (rect.top > 0) ? rect.top : 0;
		float bottom = (rect.bottom > 0) ? rect.bottom : imageSizeY;
		
		int screenWidth = mapImageView.getWidth();
		int screenHeight = mapImageView.getHeight();
		
		float xRatio = displayX / screenWidth;
		float yRatio = displayY / screenHeight;
		
		float x = left + (right-left)*xRatio;
		float y = top + (bottom-top)*yRatio;
				
		String toastMessage = null;
		// Telttalava
		if (x > 744 && x < 920 &&
				y > 420 && y < 515) {
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.LOCATION, getBaseContext());
		}
		
		// Converse
		if (x > 1280 && x < 1370 &&
				y > 552 && y < 620) {
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.AREA, getBaseContext());
		}
		
		// Niitty
		if (x > 900 && x < 1015 &&
				y > 837 && y < 920) {
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.TENT, getBaseContext());
		}
		
		// Ranta
		if (x > 1355 && x < 1465 &&
				y > 1146 && y < 1225) {
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.STAGE, getBaseContext());
		}
		
		
		Rect placeRect = new Rect(1450, 850, 1530, 930);
		if (placeRect.contains((int)x, (int)y)) {
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.PLACE, getBaseContext());
		}
		
		// Show message if applicable
		if (toastMessage != null) {
			showToast(toastMessage);
		}
		
	}
	
	private void showToast(String msg) {
		if (mapToast == null) {
			mapToast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG);
		}
		mapToast.setText(msg);
		mapToast.show();
	}
		
	private OnClickListener zoomInListener = new OnClickListener() {
		public void onClick(View v) {
			animation.stopProcess();

			if (current_scale <= 5) {
				animation.setScaleInfo(current_scale, current_scale * MAGNIFY_SCALE);
			}

		}
	};

	private OnClickListener zoomOutListener = new OnClickListener() {
		public void onClick(View v) {
			animation.stopProcess();

			if (current_scale >= MAGNIFY_SCALE * INITIAL_SCALE) {
				animation.setScaleInfo(current_scale, current_scale / MAGNIFY_SCALE);
			} else if ((current_scale > INITIAL_SCALE)) {
				animation.setScaleInfo(current_scale, INITIAL_SCALE);
			}
		}
	};

	private MapAnimationCallback animationCallBack = new MapAnimationCallback() {
		public void onTimer(int centerX, int centerY, float scale) {
			current_centerX = centerX;
			current_centerY = centerY;
			current_scale = scale;
			updateDisplay();
		}
	};

	private SizeCallback sizeCallback = new SizeCallback() {
		public void onSizeChanged(int w, int h) {
			destinationRect.set((float) 0, (float) 0, (float) w, (float) h);
			updateDisplay();
		}
	};

	private void updateDisplay() {
		calculateSourceRect(current_centerX, current_centerY, current_scale);
		matrix.setRectToRect(sourceRect, destinationRect, Matrix.ScaleToFit.FILL);
		mapImageView.setImageMatrix(matrix);
		//drawCurrentLocation();
	}

	private void calculateSourceRect(int centerX, int centerY, float scale) {
		int xSubValue;
		int ySubValue;

		if (destinationRect.bottom >= destinationRect.right) {
			ySubValue = (int) ((imageSizeY / 2) / scale);
			xSubValue = ySubValue;

			xSubValue = (int) (xSubValue * ((float) mapImageView.getWidth() / (float) mapImageView.getHeight()));
		} else {
			xSubValue = (int) ((imageSizeX / 2) / scale);
			ySubValue = xSubValue;

			ySubValue = (int) (ySubValue * ((float) mapImageView.getHeight() / (float) mapImageView.getWidth()));
		}

		if (centerX - xSubValue < 0) {
			animation.stopProcess();
			centerX = xSubValue;
		}
		if (centerY - ySubValue < 0) {
			animation.stopProcess();
			centerY = ySubValue;
		}
		if (centerX + xSubValue >= imageSizeX) {
			animation.stopProcess();
			centerX = imageSizeX - xSubValue - 1;
		}
		if (centerY + ySubValue >= imageSizeY) {
			animation.stopProcess();
			centerY = imageSizeY - ySubValue - 1;
		}

		current_centerX = centerX;
		current_centerY = centerY;

		sourceRect.set(centerX - xSubValue, centerY - ySubValue, centerX + xSubValue, centerY + ySubValue);
	}

	public void setNewDrawable(int resId) {
		current_drawable = resId;
		bitmap.recycle();
		bitmap = BitmapFactory.decodeResource(getResources(), resId, opts);
		mapImageView.setImageBitmap(bitmap);
		mapImageView.getDrawable().setFilterBitmap(true);

		current_scale = INITIAL_SCALE;
		imageSizeX = bitmap.getWidth();
		imageSizeY = bitmap.getHeight();
		current_centerX = imageSizeX / 2;
		current_centerY = imageSizeY / 2;

		animation.setInfo(0, 0, current_centerX, current_centerY);
		animation.setScaleInfo(current_scale, current_scale);

		updateDisplay();
	}
	
	private void showInitialInfoDialog() {
		SharedPreferences pref = this.getSharedPreferences(FestAppConstants.PREFERENCE_GLOBAL, Context.MODE_PRIVATE);
		final String key = "showInitialMapInfo";
		
		if (pref.getBoolean(key, true)) {
			Editor editor = pref.edit();
			editor.putBoolean(key, false);
			editor.commit();
			UIUtil.showDialog(getString(R.string.mapActivity_initialInfo_title), getString(R.string.mapActivity_initialInfo_message), this);
		}
	}
	
}
