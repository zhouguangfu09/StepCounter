package com.cn.stepcounter;

import com.cn.stepcounter.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 应用程序的设置界面
 */
public class SettingsActivity extends Activity {

	public static final String WEIGHT_VALUE = "weight_value";

	public static final String STEP_LENGTH_VALUE = "step_length_value";// 步长

	public static final String SENSITIVITY_VALUE = "sensitivity_value";// 灵敏值

	public static final String SETP_SHARED_PREFERENCES = "setp_shared_preferences";// 设置

	public static SharedPreferences sharedPreferences;

	private Editor editor;

	private TextView tv_sensitivity_vlaue;
	private TextView tv_step_length_vlaue;
	private TextView tv_weight_value;

	private SeekBar sb_sensitivity;
	private SeekBar sb_step_length;
	private SeekBar sb_weight;

	private int sensitivity = 0;
	private int step_length = 0;
	private int weight = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);

		addView();

		init();

		listener();

	}

	/**
	 * SeekBar的拖动监听
	 */
	private void listener() {
		sb_sensitivity
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {    // 灵敏值动作的监听

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// TODO Auto-generated method stub
						sensitivity = progress;
						tv_sensitivity_vlaue.setText(sensitivity + "");
					}
				});

		sb_step_length
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// TODO Auto-generated method stub
						step_length = progress * 5 + 40;
						tv_step_length_vlaue.setText(step_length
								+ getString(R.string.cm));
					}
				});

		sb_weight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				weight = progress * 2 + 30;
				tv_weight_value.setText(weight + getString(R.string.kg));
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		if (sharedPreferences == null) {    //SharedPreferences是Android平台上一个轻量级的存储类，
											//主要是保存一些常用的配置比如窗口状态
			sharedPreferences = getSharedPreferences(SETP_SHARED_PREFERENCES,
					MODE_PRIVATE);
		}

		editor = sharedPreferences.edit();

		sensitivity = 10 - sharedPreferences.getInt(SENSITIVITY_VALUE, 7);
		step_length = sharedPreferences.getInt(STEP_LENGTH_VALUE, 70);
		weight = sharedPreferences.getInt(WEIGHT_VALUE, 50);

		sb_sensitivity.setProgress(sensitivity);
		sb_step_length.setProgress((step_length - 40) / 5);               //步长按钮在进度条上占得比例
		sb_weight.setProgress((weight - 30) / 2);

		tv_sensitivity_vlaue.setText(sensitivity + "");
		tv_step_length_vlaue.setText(step_length + getString(R.string.cm));
		tv_weight_value.setText(weight + getString(R.string.kg));
	}

	private void addView() {
		tv_sensitivity_vlaue = (TextView) this
				.findViewById(R.id.sensitivity_value);
		tv_step_length_vlaue = (TextView) this
				.findViewById(R.id.step_lenth_value);
		tv_weight_value = (TextView) this.findViewById(R.id.weight_value);

		sb_sensitivity = (SeekBar) this.findViewById(R.id.sensitivity);
		sb_step_length = (SeekBar) this.findViewById(R.id.step_lenth);
		sb_weight = (SeekBar) this.findViewById(R.id.weight);

	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.save:
			editor.putInt(SENSITIVITY_VALUE, 10 - sensitivity);
			editor.putInt(STEP_LENGTH_VALUE, step_length);
			editor.putInt(WEIGHT_VALUE, weight);
			editor.commit();
			
			Toast.makeText(SettingsActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
			
			this.finish();
			StepDetector.SENSITIVITY = 10 - sensitivity;
			break;
			
		case R.id.cancle:
			this.finish();
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		init();
	}
}
