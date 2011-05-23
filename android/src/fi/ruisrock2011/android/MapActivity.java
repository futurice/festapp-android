package fi.ruisrock2011.android;

import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import fi.ruisrock2011.android.dao.ConfigDAO;
import fi.ruisrock2011.android.domain.to.MapLayerOptions;
import fi.ruisrock2011.android.gps.GPSLocationListener;
import fi.ruisrock2011.android.ui.map.Animation;
import fi.ruisrock2011.android.ui.map.AnimationCallback;
import fi.ruisrock2011.android.ui.map.MapImageView;
import fi.ruisrock2011.android.ui.map.SizeCallback;

public class MapActivity extends Activity {
	
	private static final int REQUEST_CODE_GPS = 33;
	
	private MapImageView mapImageView;
	private ImageButton zoomInButton;
	private ImageButton zoomOutButton;
	private ImageButton menuButton;
	private LocationManager locationManager;
	private MapLayerOptions mapLayerOptions;
	private GPSLocationListener gpsLocationListener;
	private ImageView currentPositionImage;
	private boolean gpsListenerOnline;
	private Matrix matrix;
	private RectF sourceRect;
	private RectF destinationRect;
	private Bitmap bitmap;
	private Timer timer;
	private Animation animation;
	private Handler handle = new Handler();

	private int imageSizeX = 2047;
	private int imageSizeY = 2047;
	private static final float INITIAL_SCALE = (float) 1;
	private static final float MAGNIFY_SCALE = (float) 1.9;

	private float current_scale = INITIAL_SCALE;
	private int current_centerX = imageSizeX / 2;
	private int current_centerY = imageSizeY / 2;
	private int current_drawable = R.drawable.map;

	private int moveHistorySize;
	private float lastTwoXMoves[] = new float[2];
	private float lastTwoYMoves[] = new float[2];
	private long downTimer;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		gpsLocationListener = new GPSLocationListener(this);
		mapImageView = (MapImageView) findViewById(R.id.image);
		zoomInButton = (ImageButton) findViewById(R.id.zoomIn);
		zoomOutButton = (ImageButton) findViewById(R.id.zoomOut);
		menuButton = (ImageButton) findViewById(R.id.mapMenu);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mapLayerOptions = ConfigDAO.findMapLayers(MapActivity.this);
		currentPositionImage = (ImageView) findViewById(R.id.currentPosition);
		currentPositionImage.setVisibility(View.GONE);
		
		
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
		animation = new Animation(handle, current_centerX, current_centerY, current_scale);

		mapImageView.setHandle(handle);
		mapImageView.setCallBack(sizeCallback);

		animation.stopProcess();
		animation.setCallBack(animationCallBack);
		timer.scheduleAtFixedRate(animation, 200, 30);

		mapImageView.setOnTouchListener(metroListener);
		zoomInButton.setOnClickListener(zoomInListener);
		zoomOutButton.setOnClickListener(zoomOutListener);
		menuButton.setOnClickListener(menuListener);

		bitmap = BitmapFactory.decodeResource(getResources(), current_drawable);

		imageSizeX = bitmap.getWidth();
		imageSizeY = bitmap.getHeight();

		mapImageView.setImageBitmap(bitmap);
		mapImageView.getDrawable().setFilterBitmap(true);
		mapImageView.setImageMatrix(matrix);
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
		if (turnOn) {
			if (!gpsListenerOnline) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000L, 0f, gpsLocationListener);
				Toast.makeText(this, getString(R.string.mapActivity_gpsActivated), Toast.LENGTH_LONG).show();
			}
			gpsListenerOnline = true;
		} else {
			if (gpsListenerOnline) {
				locationManager.removeUpdates(gpsLocationListener);
			}
			gpsListenerOnline = false;
			currentPositionImage.setVisibility(View.GONE);
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

	private OnTouchListener metroListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {

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
	
	private void handleMapLayerSelection(MapLayerOptions mapLayerOptions) {
		if (isGpsLayerSelected() && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showNoGpsDialog();
		} else {
			ConfigDAO.updateMapLayers(this, mapLayerOptions);
			activateGpsListener(isGpsLayerSelected());
		}
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
			ConfigDAO.updateMapLayers(this, mapLayerOptions);
			activateGpsListener(isGpsLayerSelected());
		}
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

	private AnimationCallback animationCallBack = new AnimationCallback() {
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
		bitmap = BitmapFactory.decodeResource(getResources(), resId);
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

	public void updateGpsLocation(Location location) {
		Toast.makeText(this, "" + location.getTime() + "\nLAT: " + location.getLatitude()+ "\nLONG: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
		
		// TODO: Proper implementation
		// north-south,east-west
		// latitude,longitude,
		// topRight: 60.493000,24.722333
		// bottomRight: 60.493000,25.320000
		// bottomLeft: 60.128000,25.320000
		// topLeft: 60.128000,24.722333
		
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.topMargin = 0 + (int)(Math.random() * ((400 - 0) + 1));
		lp.leftMargin = 0 + (int)(Math.random() * ((400 - 0) + 1));
		currentPositionImage.setLayoutParams(lp);
		currentPositionImage.setVisibility(View.VISIBLE);
		currentPositionImage.bringToFront();
		
		//Projection proj = ProjectionFactory.readProjectionFile();
		
		
		
	}
}
