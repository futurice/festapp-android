package com.futurice.festapp;

import java.text.DecimalFormat;
import java.util.Timer;

import com.flurry.android.FlurryAgent;
import com.futurice.festapp.dao.ConfigDAO;
import com.futurice.festapp.dao.GigDAO;
import com.futurice.festapp.domain.to.MapLayerOptions;
import com.futurice.festapp.domain.to.StageType;
import com.futurice.festapp.gps.GPSLocationListener;
import com.futurice.festapp.ui.map.MapAnimation;
import com.futurice.festapp.ui.map.MapAnimationCallback;
import com.futurice.festapp.ui.map.MapImageView;
import com.futurice.festapp.ui.map.SizeCallback;
import com.futurice.festapp.util.FestAppConstants;
import com.futurice.festapp.util.UIUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.futurice.festapp.R;

/**
 * View for festival Map.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class MapActivity extends BaseActivity {
	
	private static final int REQUEST_CODE_GPS = 33;
	private static final double referenceLatitude = 60.42836515775148;
	private static final double referenceLongitude = 22.18308629415958;
	private static final Location referenceLocation = new Location("FestApp_MapActivity");
	
	static {
		referenceLocation.setLatitude(referenceLatitude);
		referenceLocation.setLongitude(referenceLongitude);
	}
	
	private MapImageView mapImageView;
	private ImageButton zoomInButton;
	private ImageButton zoomOutButton;
	private ImageButton menuButton;
	private TextView gpsStatusText;
	private LocationManager locationManager;
	private MapLayerOptions mapLayerOptions;
	private GPSLocationListener gpsLocationListener;
	private ImageView currentPositionImage;
	private boolean gpsListenerOnline;
	private TextView mapBubble;
	private boolean mapBubbleIsWithinMapArea;
	private boolean mapBubbleAnimationInProgress = false;
	private boolean lockMap = false;
	private Matrix matrix;
	private RectF sourceRect;
	private RectF destinationRect;
	private Bitmap bitmap;
	private Timer timer;
	private MapAnimation animation;
	private Location location;
	private int locationX = -1;
	private int locationY = -1;
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
	
	private static final long CURRENT_POSITION_ANIM_FREQ = 1000L;
	private Runnable currentPositionRunnable = new Runnable() {
		@Override
		public void run() {
			int level = currentPositionImage.getDrawable().getLevel();
			switch (level) {
			case 2500:
				currentPositionImage.getDrawable().setLevel(5000);
				break;
			case 5000:
				currentPositionImage.getDrawable().setLevel(7500);
				break;
			case 7500:
				currentPositionImage.getDrawable().setLevel(10000);
				break;
			default:
				currentPositionImage.getDrawable().setLevel(2500);
				break;
			}
			currentPositionHandler.postDelayed(this, CURRENT_POSITION_ANIM_FREQ);
		}
	};
	private Handler currentPositionHandler = new Handler();
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		opts.inScaled = false;
		gpsStatusText = (TextView) findViewById(R.id.mapGpsStatusText);
		gpsStatusText.bringToFront();
		gpsStatusText.setVisibility(View.GONE);
		gpsLocationListener = new GPSLocationListener(this);
		mapImageView = (MapImageView) findViewById(R.id.image);
		zoomInButton = (ImageButton) findViewById(R.id.zoomIn);
		zoomOutButton = (ImageButton) findViewById(R.id.zoomOut);
		menuButton = (ImageButton) findViewById(R.id.mapMenu);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mapLayerOptions = ConfigDAO.findMapLayers(MapActivity.this);
		currentPositionImage = (ImageView) findViewById(R.id.currentPosition);
		currentPositionImage.setVisibility(View.GONE);
		mapBubble = (TextView) findViewById(R.id.mapBubble);
		mapBubble.setVisibility(View.GONE);
		
		if (isGpsLayerSelected() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			activateGpsListener(true);
		} else {
			mapLayerOptions.setOptionValue(getString(R.string.mapActivity_layer_gps), false);
		}

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
		menuButton.setOnClickListener(menuListener);

		bitmap = BitmapFactory.decodeResource(getResources(), current_drawable, opts);
		imageSizeX = bitmap.getWidth();
		imageSizeY = bitmap.getHeight();

		mapImageView.setImageBitmap(bitmap);
		mapImageView.getDrawable().setFilterBitmap(true);
		mapImageView.setImageMatrix(matrix);
		
		showInitialInfoDialog();
		FlurryAgent.logEvent("Kartta");
	}
	
	@Override
	protected void onPause() {
		activateGpsListener(false);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if (isGpsLayerSelected() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			activateGpsListener(true);
		}
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
	
	private void activateGpsListener(boolean turnOn) {
		gpsStatusText.setVisibility(View.GONE);
		hideMapBubble();
		if (turnOn) {
			if (!gpsListenerOnline) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000L, 2f, gpsLocationListener);
				locationManager.addGpsStatusListener(gpsLocationListener);
				showToast(getString(R.string.mapActivity_gpsActivated));
			}
			gpsListenerOnline = true;
			currentPositionHandler.post(currentPositionRunnable);
		} else {
			if (gpsListenerOnline) {
				locationManager.removeUpdates(gpsLocationListener);
				locationManager.removeGpsStatusListener(gpsLocationListener);
			}
			gpsListenerOnline = false;
			currentPositionImage.setVisibility(View.GONE);
			currentPositionHandler.removeCallbacks(currentPositionRunnable);
			locationX = -1;
			locationY = -1;
			location = null;
			drawCurrentLocation();
		}
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
			if (lockMap) {
				if (!mapBubbleAnimationInProgress) {
					mapImageView.postDelayed(new Runnable() {
						@Override
						public void run() {
							startMapBubbleFadeOut();
						}
					}, 1);
				}
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
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.TELTTA, getBaseContext());
		}
		
		// Converse
		if (x > 1280 && x < 1370 &&
				y > 552 && y < 620) {
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.LOUNA, getBaseContext());
		}
		
		// Niitty
		if (x > 900 && x < 1015 &&
				y > 837 && y < 920) {
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.NIITTY, getBaseContext());
		}
		
		// Ranta
		if (x > 1355 && x < 1465 &&
				y > 1146 && y < 1225) {
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.RANTA, getBaseContext());
		}
		
		
		Rect rantaMini = new Rect(1450, 850, 1530, 930);
		if (rantaMini.contains((int)x, (int)y)) {
			toastMessage = GigDAO.findNextArtistOnStageMessage(StageType.RANTA_MINI, getBaseContext());
		}
		
		// Show message if applicable
		if (toastMessage != null) {
			FlurryAgent.logEvent("Lavaa klikattu kartalla");
			showToast(toastMessage);
		}
		
	}
	
	private void handleMapLayerSelection(MapLayerOptions mapLayerOptions) {
		if (isGpsLayerSelected() && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showNoGpsDialog();
		} else {
			updateMapLayers();
		}
	}
	
	private void showToast(String msg) {
		if (mapToast == null) {
			mapToast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG);
		}
		mapToast.setText(msg);
		mapToast.show();
	}
	
	private OnClickListener menuListener = new OnClickListener() {
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
			builder.setTitle(getString(R.string.mapActivity_ChooseLayers));
			
		    builder.setCancelable(false)
		       .setPositiveButton(getString(R.string.Save), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		                handleMapLayerSelection(mapLayerOptions);
		           }
		       })
		       .setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });

			
			mapLayerOptions = ConfigDAO.findMapLayers(MapActivity.this);
			final CharSequence[] items = mapLayerOptions.getOptions();
			final boolean[] itemValues = mapLayerOptions.getOptionBooleans();
			builder.setMultiChoiceItems(items, itemValues, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					mapLayerOptions.setOptionValue(which, isChecked);
				}
			});
			
			AlertDialog alert = builder.create();
			alert.show();
		}
	};
	
	private void showNoGpsDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.mapActivity_prompt_EnableGPS_title));
		builder.setMessage(getString(R.string.mapActivity_prompt_EnableGPS_message)).setCancelable(false)
			.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, REQUEST_CODE_GPS);
				}
			}).setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		final AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GPS) {
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mapLayerOptions.setOptionValue(getString(R.string.mapActivity_layer_gps), false);
			}
			updateMapLayers();
		}
	}
	
	private void updateMapLayers() {
		ConfigDAO.updateMapLayers(this, mapLayerOptions);
		activateGpsListener(isGpsLayerSelected());
	}

	private boolean isGpsLayerSelected() {
		return mapLayerOptions.isOptionSelected(getString(R.string.mapActivity_layer_gps));
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
		drawCurrentLocation();
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
	
	public void setGpsStatusText(String text) {
		if (text == null) {
			gpsStatusText.setText("");
			gpsStatusText.setVisibility(View.GONE);
		} else {
			gpsStatusText.setText(text);
			gpsStatusText.setVisibility(View.VISIBLE);
		}
	}

	public void updateGpsLocation(Location location) {
		boolean isCurrentLocationWithinMap = isCurrentLocationWithinMap();
		this.location = location;
		
		calculateNewPixelsFromLocation();
		drawCurrentLocation();
		
		if (!isCurrentLocationWithinMap && isCurrentLocationWithinMap()) {
			this.current_centerX = locationX;
			this.current_centerY = locationY;
			animation.setCenter(locationX, locationY);
			mapBubbleIsWithinMapArea = true;
			lockMap = true;
			mapBubbleAnimationInProgress = false;
			mapBubble.setVisibility(View.VISIBLE);
			updateDisplay();
		}
		
		// Show GPS-status text
		if (!isCurrentLocationWithinMap()) {
			if (location != null) {
				try {
					float distance = location.distanceTo(referenceLocation) / 1000; // in kilometers
					setGpsStatusText(getString(R.string.mapActivity_distanceToMap, new DecimalFormat("0.0").format(distance)));
				} catch (Exception e) {
					gpsStatusText.setVisibility(View.GONE);
				}
			}
		} else if (location != null && location.hasAccuracy() && location.getAccuracy() > 0) {
			setGpsStatusText(getString(R.string.mapActivity_gpsAccuracy, new DecimalFormat("0.0").format(location.getAccuracy())));
		} else {
			gpsStatusText.setVisibility(View.GONE);
		}
	}
	
	private void hideMapBubble() {
		mapBubble.setVisibility(View.GONE);
		mapBubbleAnimationInProgress = false;
		mapBubbleIsWithinMapArea = false;
		lockMap = false;
	}
	
	private void calculateNewPixelsFromLocation() {
		if (location == null) {
			locationX = -1;
			locationY = -1;
			return;
		}
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		
		if (latitude < 59 || latitude > 62 || longitude < 21 || longitude > 23) {
			locationX = -1;
			locationY = -1;
			return;
		}
		
		double referenceX = 1342;
		double referenceY = 736;
		
		double latitudeGain = 0.00412661358/100;
		double longitudeGain = 0.00507205725/100;
		double latitudeGainXPixelChange = -4.74;
		double latitudeGainYPixelChange = -7.56;
		double longitudeGainXPixelChange = 4.7;
		double longitudeGainYPixelChange = -2.92;
	    
		double changeInLatitude = (latitude - referenceLatitude) / latitudeGain;
		double changeInX = changeInLatitude * latitudeGainXPixelChange;
		double changeInY = changeInLatitude * latitudeGainYPixelChange;
		
		double changeInLongitude = (longitude - referenceLongitude) / longitudeGain;
	    changeInX += changeInLongitude * longitudeGainXPixelChange;
	    changeInY += changeInLongitude * longitudeGainYPixelChange;
	    
	    locationX = (int) (referenceX + changeInX);
	    locationY = (int) (referenceY + changeInY);
	}
	
	private boolean isCurrentLocationWithinMap() {
		if (locationY < 0 || locationY >= imageSizeY) {
			return false;
		}
		if (locationX < 0 || locationX >= imageSizeX) {
			return false;
		}
		return true;
	}
	
	private void drawCurrentLocation() {
		if (locationY < 0 || locationX < 0) {
			currentPositionImage.setVisibility(View.GONE);
			return;
		}
		
		PointF displayPoint = convertMapPointToDisplayPoint(locationX, locationY);
		if (displayPoint == null) {
			currentPositionImage.setVisibility(View.GONE);
		} else {
			float xOnDisplay = displayPoint.x;
			float yOnDisplay = displayPoint.y;
			
			int currentPosSize = getCurrentPositionSize();
			int margin = currentPosSize / 2;
			
			int leftMargin = (int) xOnDisplay - margin;
			int topMargin = (int) yOnDisplay - margin;
			if (leftMargin > (mapImageView.getWidth() - currentPosSize) || topMargin > (mapImageView.getHeight() - currentPosSize)) {
				currentPositionImage.setVisibility(View.GONE);
			} else {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(currentPosSize, currentPosSize);
				lp.leftMargin = leftMargin;
				lp.topMargin = topMargin;
				currentPositionImage.setLayoutParams(lp);
				currentPositionImage.setVisibility(View.VISIBLE);
				currentPositionImage.bringToFront();
			}
		}
		drawCurrentPositionMapBubble();
	}
	
	private int getCurrentPositionSize() {
		return getResources().getDimensionPixelSize(R.dimen.map_currentPosition_size);
	}
	
	private PointF convertMapPointToDisplayPoint(float mapX, float mapY) {
		RectF rect = sourceRect;
		
		float left = (rect.left > 0) ? rect.left : 0;
		float right = (rect.right > 0) ? rect.right : imageSizeX;
		float top = (rect.top > 0) ? rect.top : 0;
		float bottom = (rect.bottom > 0) ? rect.bottom : imageSizeY;
		
		boolean tooLeft = mapX < left;
		boolean tooRight = mapX > right;
		boolean tooTop = mapY < top;
		boolean tooBottom = mapY > bottom;
		
		if (tooLeft || tooRight || tooTop || tooBottom) {
			return null;
		} else {
			float xRatio = (mapX-left)/(right-left);
			float yRatio = (mapY-top)/(bottom-top);
			
			float xOnDisplay = xRatio * mapImageView.getWidth();
			float yOnDisplay = yRatio * mapImageView.getHeight();
			return new PointF(xOnDisplay, yOnDisplay);
		}
	}
	
	private void drawCurrentPositionMapBubble() {
		PointF point = convertMapPointToDisplayPoint(locationX, locationY);
		if (mapBubbleIsWithinMapArea && isCurrentLocationWithinMap() && isPointWithinDisplay(point)) {
			mapBubble.setVisibility(View.VISIBLE);
			
			int theX = (int) (point.x - getResources().getDimensionPixelSize(R.dimen.mapBubble_width)/2);
			int theY = (int) (point.y - getResources().getDimensionPixelSize(R.dimen.mapBubble_height));
			
			ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mapBubble.getLayoutParams();
			mlp.setMargins(theX, theY, 0, 0);
		} else {
			mapBubble.setVisibility(View.GONE);
		}
	}
	
	private boolean isPointWithinDisplay(PointF point) {
		if (point == null) {
			return false;
		}
		int width = mapImageView.getWidth();
		int height = mapImageView.getHeight();
		if (point.x < 0 || point.x > width || point.y < 0 || point.y > height) {
			return false;
		}
		return true;
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

	private void startMapBubbleFadeOut() {
		Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_to_invisible);
		fadeOutAnimation.setAnimationListener(new MapBubbleFadeOutListener());
		mapBubble.startAnimation(fadeOutAnimation);
	}

	public void gpsStatusChanged(int event) {
		if (event == GpsStatus.GPS_EVENT_STARTED) {
			if (location == null) {
				String statusText = getString(R.string.mapActivity_gpsWaitingForFix);
				setGpsStatusText(statusText);
			}
		}
	}
	
	class MapBubbleFadeOutListener implements AnimationListener {
		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
			mapBubbleAnimationInProgress = true;
			lockMap = true;
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mapBubbleIsWithinMapArea = false;
			mapBubble.setVisibility(View.GONE);
			lockMap = false;
		}
	}
	
	
}
