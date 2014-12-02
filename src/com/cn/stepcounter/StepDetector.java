package com.cn.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
//走步检测器，用于检测走步并计数
/**
 * 具体算法不太清楚，本算法是从谷歌计步器：Pedometer上截取的部分计步算法
 * 
 */
public class StepDetector implements SensorEventListener {

	public static int CURRENT_SETP = 0;

	public static float SENSITIVITY = 0;   //SENSITIVITY灵敏度

	private float mLastValues[] = new float[3 * 2];
	private float mScale[] = new float[2];
	private float mYOffset;
	private static long end = 0;
	private static long start = 0;

	/**
	 * 最后加速度方向
	 */
	private float mLastDirections[] = new float[3 * 2];
	private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
	private float mLastDiff[] = new float[3 * 2];
	private int mLastMatch = -1;

	/**
	 * 传入上下文的构造函数
	 * 
	 * @param context
	 */
	public StepDetector(Context context) {
		// TODO Auto-generated constructor stub
		super();
		int h = 480;
		mYOffset = h * 0.5f;
		mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
		mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
		if (SettingsActivity.sharedPreferences == null) {
			SettingsActivity.sharedPreferences = context.getSharedPreferences(
					SettingsActivity.SETP_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
		}
		SENSITIVITY = SettingsActivity.sharedPreferences.getInt(
				SettingsActivity.SENSITIVITY_VALUE, 3);
	}

	// public void setSensitivity(float sensitivity) {
	// SENSITIVITY = sensitivity; // 1.97 2.96 4.44 6.66 10.00 15.00 22.50
	// // 33.75
	// // 50.62
	// }

	// public void onSensorChanged(int sensor, float[] values) {
	@Override
	public void onSensorChanged(SensorEvent event) {
		// Log.i(Constant.STEP_SERVER, "StepDetector");
		Sensor sensor = event.sensor;
		// Log.i(Constant.STEP_DETECTOR, "onSensorChanged");
		synchronized (this) {
			if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
			} else {
				int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
				if (j == 1) {
					float vSum = 0;
					for (int i = 0; i < 3; i++) {
						final float v = mYOffset + event.values[i] * mScale[j];
						vSum += v;
					}
					int k = 0;
					float v = vSum / 3;

					float direction = (v > mLastValues[k] ? 1: (v < mLastValues[k] ? -1 : 0));
					if (direction == -mLastDirections[k]) {
						// Direction changed
						int extType = (direction > 0 ? 0 : 1); // minumum or
						// maximum?
						mLastExtremes[extType][k] = mLastValues[k];
						float diff = Math.abs(mLastExtremes[extType][k]- mLastExtremes[1 - extType][k]);

						if (diff > SENSITIVITY) {
							boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
							boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
							boolean isNotContra = (mLastMatch != 1 - extType);

							if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
								end = System.currentTimeMillis();
								if (end - start > 500) {// 此时判断为走了一步
									Log.i("StepDetector", "CURRENT_SETP:"
											+ CURRENT_SETP);
									CURRENT_SETP++;
									mLastMatch = extType;
									start = end;
								}
							} else {
								mLastMatch = -1;
							}
						}
						mLastDiff[k] = diff;
					}
					mLastDirections[k] = direction;
					mLastValues[k] = v;
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

}
