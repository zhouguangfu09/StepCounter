package com.cn.stepcounter;

import com.cn.stepcounter.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.animation.Animation;

/**
 * 程序启动界面
 * 完成开机动画
 * 并跳转到主程序运行界面StepActivity
 */
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		if (StepCounterService.FLAG || StepDetector.CURRENT_SETP > 0) {// 程序已经启动，直接跳转到运行界面
			Intent intent = new Intent(SplashActivity.this, StepCounterActivity.class); //创建一个新的Intent，指定当前应用程序上下文
																				//和要启动的StepActivity类
			startActivity(intent);												//传递这个intent给startActivity
			this.finish();
		} else {
			new CountDownTimer(2000L, 1000L)
			{
				public void onFinish()
				{

					//启动界面淡入淡出效果
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, StartActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					finish();

				}

				public void onTick(long paramLong)
				{
				}
			}
			.start();
		}
	}

}

