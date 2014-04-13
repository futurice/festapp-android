package com.futurice.festapp.ui.map;

import java.util.TimerTask;

import android.os.Handler;

public class MapAnimation extends TimerTask {

	private MapAnimationCallback callBack;
	private Handler handle;
	private Runnable cbkAction;
	private float speedX;
	private float speedY;
	private float currentCenterX, currentCenterY;
	private float current_scale, target_scale;
	private boolean runningMove;
	private boolean runningScale;
	private boolean scaleDirectionPlus;

	private static final float SCALE_STEP = (float) 0.04;
	private static final float MAX_SPEED = 100;
	private static final float END_SPEED_THRESHOLD = (float) 4;
	private static final float SPEED_STEP = (float) 1.1;

	public MapAnimation(Handler handle, int current_centerX, int current_centerY, float currentScale) {
		super();
		speedX = 0;
		speedY = 0;
		runningMove = false;
		runningScale = false;
		this.currentCenterX = current_centerX;
		this.currentCenterY = current_centerY;
		this.current_scale = currentScale;
		this.handle = handle;
		cbkAction = new Runnable() {
			public void run() {
				callBack.onTimer((int) currentCenterX, (int) currentCenterY, current_scale);
			}
		};
	}

	@Override
	public void run() {
		if (runningMove) {
			float speedAbs = speedX * speedX + speedY * speedY;

			if (speedAbs >= (MAX_SPEED * MAX_SPEED)) {
				speedX = (speedX / speedAbs) * MAX_SPEED * MAX_SPEED;
				speedY = (speedY / speedAbs) * MAX_SPEED * MAX_SPEED;
			}

			if (speedAbs <= END_SPEED_THRESHOLD) {
				runningMove = false;
			} else {
				currentCenterX += speedX;
				currentCenterY += speedY;
				handle.post(cbkAction);
				speedX /= SPEED_STEP;
				speedY /= SPEED_STEP;
			}
		}

		if (runningScale) {
			if (scaleDirectionPlus) {
				current_scale += SCALE_STEP * current_scale;
				if (current_scale >= target_scale) {
					runningScale = false;
					current_scale = target_scale;
				}
				handle.post(cbkAction);
			} else {
				current_scale -= SCALE_STEP * current_scale;
				if (current_scale <= target_scale) {
					runningScale = false;
					current_scale = target_scale;
				}
				handle.post(cbkAction);
			}
		}
	}

	public void setCallBack(MapAnimationCallback cbk) {
		callBack = cbk;
	}

	public void setInfo(float speedX_, float speedY_, int centerX_, int centerY_) {
		speedX = speedX_;
		speedY = speedY_;
		currentCenterX = centerX_;
		currentCenterY = centerY_;
		runningMove = true;
	}
	
	public void setCenter(float centerX, float centerY) {
		currentCenterX = centerX;
		currentCenterY = centerY;
	}

	public void setScaleInfo(float current_scale, float target_scale) {
		this.current_scale = current_scale;
		this.target_scale = target_scale;

		if (current_scale < target_scale) {
			scaleDirectionPlus = true;
		} else {
			scaleDirectionPlus = false;
		}

		runningScale = true;
	}

	public void stopProcess() {
		runningMove = false;
	}
}
