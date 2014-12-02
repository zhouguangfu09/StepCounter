package com.cn.stepcounter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Calendar;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.cn.stepcounter.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;


/** 应用程序的用户界面，
 * 主要功能就是按照XML布局文件的内容显示界面，
 * 并与用户进行交互
 * 负责前台界面展示
 * 在android中Activity负责前台界面展示，service负责后台的需要长期运行的任务。
 * Activity和Service之间的通信主要由Intent负责
 */
@SuppressLint("HandlerLeak")
public class StepCounterActivity extends Activity {

	//定义文本框控件
	private TextView tv_show_step;// 步数
	private TextView tv_week_day;// 星期
	private TextView tv_date;// 日期

	private TextView tv_timer;// 运行时间

	private TextView tv_distance;// 行程
	private TextView tv_calories;// 卡路里
	private TextView tv_velocity;// 速度

	private Button btn_start;// 开始按钮
	private Button btn_stop;// 停止按钮

	private GifView gifView;
	private boolean isRun = false;

	// 十颗星标
	private ImageView iv_star_1;                
	private ImageView iv_star_2;
	private ImageView iv_star_3;
	private ImageView iv_star_4;
	private ImageView iv_star_5;
	private ImageView iv_star_6;
	private ImageView iv_star_7;
	private ImageView iv_star_8;
	private ImageView iv_star_9;
	private ImageView iv_star_10;

	private long timer = 0;// 运动时间
	private  long startTimer = 0;// 开始时间

	private  long tempTime = 0;

	private Double distance = 0.0;// 路程：米
	private Double calories = 0.0;// 热量：卡路里
	private Double velocity = 0.0;// 速度：米每秒

	private int step_length = 0;  //步长
	private int weight = 0;       //体重
	private int total_step = 0;   //走的总步数

	private Thread thread;  //定义线程对象

	private TableRow hide1, hide2;
	private TextView step_counter;

	// 当创建一个新的Handler实例时, 它会绑定到当前线程和消息的队列中,开始分发数据
	// Handler有两个作用, (1) : 定时执行Message和Runnalbe 对象
	// (2): 让一个动作,在不同的线程中执行.

	Handler handler = new Handler() {// Handler对象用于更新当前步数,定时发送消息，调用方法查询数据用于显示？？？？？？？？？？
		//主要接受子线程发送的数据, 并用此数据配合主线程更新UI
		//Handler运行在主线程中(UI线程中), 它与子线程可以通过Message对象来传递数据, 
		//Handler就承担着接受子线程传过来的(子线程用sendMessage()方法传递Message对象，(里面包含数据)
		//把这些消息放入主线程队列中，配合主线程进行更新UI。

		@Override                  //这个方法是从父类/接口 继承过来的，需要重写一次
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);        // 此处可以更新UI

			countDistance();     //调用距离方法，看一下走了多远

			if (timer != 0 && distance != 0.0) {

				// 体重、距离
				// 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
				calories = weight * distance * 0.001;
				//速度velocity
				velocity = distance * 1000 / timer;
			} else {
				calories = 0.0;
				velocity = 0.0;
			}

			countStep();          //调用步数方法

			tv_show_step.setText(total_step + "");// 显示当前步数

			tv_distance.setText(formatDouble(distance));// 显示路程
			tv_calories.setText(formatDouble(calories));// 显示卡路里
			tv_velocity.setText(formatDouble(velocity));// 显示速度

			tv_timer.setText(getFormatTime(timer));// 显示当前运行时间

			changeStep();// 设置当前步数和星标

		}

		/**
		 * 设置当前步数和星标
		 */
		private void changeStep() {
			int level = StepDetector.CURRENT_SETP / 100;  //每走150步亮一颗星星
			switch (level) {
			case 10:
				iv_star_10.setImageResource(R.drawable.start_disable);
			case 9:
				iv_star_9.setImageResource(R.drawable.start_red);
			case 8:
				iv_star_8.setImageResource(R.drawable.start_red);
			case 7:
				iv_star_7.setImageResource(R.drawable.start_red);
			case 6:
				iv_star_6.setImageResource(R.drawable.start_red);
			case 5:
				iv_star_5.setImageResource(R.drawable.start_green);
			case 4:
				iv_star_4.setImageResource(R.drawable.start_green);
			case 3:
				iv_star_3.setImageResource(R.drawable.start_green);
			case 2:
				iv_star_2.setImageResource(R.drawable.start_green);
			case 1:
				iv_star_1.setImageResource(R.drawable.start_green);
				break;
			case 0:
				iv_star_1.setImageResource(R.drawable.star_enable);
				iv_star_2.setImageResource(R.drawable.star_enable);
				iv_star_3.setImageResource(R.drawable.star_enable);
				iv_star_4.setImageResource(R.drawable.star_enable);
				iv_star_5.setImageResource(R.drawable.star_enable);
				iv_star_6.setImageResource(R.drawable.star_enable);
				iv_star_7.setImageResource(R.drawable.star_enable);
				iv_star_8.setImageResource(R.drawable.star_enable);
				iv_star_9.setImageResource(R.drawable.star_enable);
				iv_star_10.setImageResource(R.drawable.star_enable);
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.main);  //设置当前屏幕

		if (SettingsActivity.sharedPreferences == null) {
			SettingsActivity.sharedPreferences = this.getSharedPreferences(
					SettingsActivity.SETP_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
		}

		Bundle extras = getIntent().getExtras(); 
		isRun = extras.getBoolean("run");

		if(isRun){
			//健身
			gifView = (GifView)findViewById(R.id.gif_view);
			gifView.setGifImageType(GifImageType.COVER);
			gifView.setShowDimension(100, 100);
			gifView.setGifImage(R.drawable.walk_gif);
			gifView.showCover();

		}else{
			gifView = (GifView)findViewById(R.id.gif_view);
			gifView.setGifImageType(GifImageType.COVER);
			gifView.setShowDimension(100, 100);
			gifView.setGifImage(R.drawable.run_gif);
			gifView.showCover();
		}

		if (thread == null) {

			thread = new Thread() {// 子线程用于监听当前步数的变化

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					int temp = 0;
					while (true) {
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (StepCounterService.FLAG) {
							Message msg = new Message();
							if (temp != StepDetector.CURRENT_SETP) {
								temp = StepDetector.CURRENT_SETP;
							}
							if (startTimer != System.currentTimeMillis()) {
								timer = tempTime + System.currentTimeMillis()
										- startTimer;
							}
							handler.sendMessage(msg);// 通知主线程
						}
					}
				}
			};
			thread.start();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Log.i("APP", "on resuame.");
		// 获取界面控件
		addView();

		// 初始化控件
		init();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 获取Activity相关控件
	 */
	private void addView() {
		tv_show_step = (TextView) this.findViewById(R.id.show_step);
		tv_week_day = (TextView) this.findViewById(R.id.week_day);
		tv_date = (TextView) this.findViewById(R.id.date);

		tv_timer = (TextView) this.findViewById(R.id.timer);

		tv_distance = (TextView) this.findViewById(R.id.distance);
		tv_calories = (TextView) this.findViewById(R.id.calories);
		tv_velocity = (TextView) this.findViewById(R.id.velocity);

		btn_start = (Button) this.findViewById(R.id.start);
		btn_stop = (Button) this.findViewById(R.id.stop);

		// 星标
		iv_star_1 = (ImageView) this.findViewById(R.id.iv_1);
		iv_star_2 = (ImageView) this.findViewById(R.id.iv_2);
		iv_star_3 = (ImageView) this.findViewById(R.id.iv_3);
		iv_star_4 = (ImageView) this.findViewById(R.id.iv_4);
		iv_star_5 = (ImageView) this.findViewById(R.id.iv_5);
		iv_star_6 = (ImageView) this.findViewById(R.id.iv_6);
		iv_star_7 = (ImageView) this.findViewById(R.id.iv_7);
		iv_star_8 = (ImageView) this.findViewById(R.id.iv_8);
		iv_star_9 = (ImageView) this.findViewById(R.id.iv_9);
		iv_star_10 = (ImageView) this.findViewById(R.id.iv_10);

		hide1 = (TableRow)findViewById(R.id.hide1);
		hide2 = (TableRow)findViewById(R.id.hide2);
		step_counter = (TextView)findViewById(R.id.step_counter);


		if(isRun){
			hide1.setVisibility(View.GONE);
			hide2.setVisibility(View.GONE);
			step_counter.setText("次数");
		}

		Intent service = new Intent(this, StepCounterService.class);
		stopService(service);
		StepDetector.CURRENT_SETP = 0;
		tempTime = timer = 0;
		tv_timer.setText(getFormatTime(timer));      //如果关闭之后，格式化时间
		tv_show_step.setText("0");
		tv_distance.setText(formatDouble(0.0));
		tv_calories.setText(formatDouble(0.0));
		tv_velocity.setText(formatDouble(0.0));

		handler.removeCallbacks(thread);

	}

	/**
	 * 初始化界面
	 */
	private void init() {

		step_length = SettingsActivity.sharedPreferences.getInt(
				SettingsActivity.STEP_LENGTH_VALUE, 70);
		weight = SettingsActivity.sharedPreferences.getInt(
				SettingsActivity.WEIGHT_VALUE, 50);

		countDistance();
		countStep();
		if ((timer += tempTime) != 0 && distance != 0.0) {  //tempTime记录运动的总时间，timer记录每次运动时间

			// 体重、距离
			// 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036，换算一下
			calories = weight * distance * 0.001;

			velocity = distance * 1000 / timer;
		} else {
			calories = 0.0;
			velocity = 0.0;
		}

		tv_timer.setText(getFormatTime(timer + tempTime));

		tv_distance.setText(formatDouble(distance));
		tv_calories.setText(formatDouble(calories));
		tv_velocity.setText(formatDouble(velocity));

		tv_show_step.setText(total_step + "");

		btn_start.setEnabled(!StepCounterService.FLAG);
		btn_stop.setEnabled(StepCounterService.FLAG);

		if (StepCounterService.FLAG) {
			btn_stop.setText(getString(R.string.pause));
		} else if (StepDetector.CURRENT_SETP > 0) {
			btn_stop.setEnabled(true);
			btn_stop.setText(getString(R.string.cancel));
		}

		setDate();
	}

	/**
	 * 设置显示的日期
	 */
	private void setDate() {
		Calendar mCalendar = Calendar.getInstance();// 获取当天Calendar对象
		int weekDay = mCalendar.get(Calendar.DAY_OF_WEEK);// 当天的星期
		int month = mCalendar.get(Calendar.MONTH) + 1;// 当前月份
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);// 当前日期

		tv_date.setText(month + getString(R.string.month) + day
				+ getString(R.string.day));// 显示当前日期

		String week_day_str = new String();
		switch (weekDay) {
		case Calendar.SUNDAY:// 星期天
			week_day_str = getString(R.string.sunday);
			break;

		case Calendar.MONDAY:// 星期一
			week_day_str = getString(R.string.monday);
			break;

		case Calendar.TUESDAY:// 星期二
			week_day_str = getString(R.string.tuesday);
			break;

		case Calendar.WEDNESDAY:// 星期三
			week_day_str = getString(R.string.wednesday);
			break;

		case Calendar.THURSDAY:// 星期四
			week_day_str = getString(R.string.thursday);
			break;

		case Calendar.FRIDAY:// 星期五
			week_day_str = getString(R.string.friday);
			break;

		case Calendar.SATURDAY:// 星期六
			week_day_str = getString(R.string.saturday);
			break;
		}
		tv_week_day.setText(week_day_str);
	}

	/**
	 * 计算并格式化doubles数值，保留两位有效数字
	 * 
	 * @param doubles
	 * @return 返回当前路程
	 */
	private String formatDouble(Double doubles) {
		DecimalFormat format = new DecimalFormat("####.##");
		String distanceStr = format.format(doubles);
		return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
				: distanceStr;
	}

	public void onClick(View view) {
		Intent service = new Intent(this, StepCounterService.class);
		switch (view.getId()) {
		case R.id.start:
			gifView.showAnimation();
			startService(service);
			btn_start.setEnabled(false);
			btn_stop.setEnabled(true);
			btn_stop.setText(getString(R.string.pause));
			startTimer = System.currentTimeMillis();
			tempTime = timer;
			break;

		case R.id.stop:
			stopService(service);
			gifView.showCover();
			if (StepCounterService.FLAG && StepDetector.CURRENT_SETP > 0) {
				btn_stop.setText(getString(R.string.cancel));
			} else {
				StepDetector.CURRENT_SETP = 0;
				tempTime = timer = 0;

				btn_stop.setText(getString(R.string.pause));
				btn_stop.setEnabled(false);

				tv_timer.setText(getFormatTime(timer));      //如果关闭之后，格式化时间

				tv_show_step.setText("0");
				tv_distance.setText(formatDouble(0.0));
				tv_calories.setText(formatDouble(0.0));
				tv_velocity.setText(formatDouble(0.0));

				handler.removeCallbacks(thread);
			}
			btn_start.setEnabled(true);
			break;	
		}
	}

	/**
	 * 得到一个格式化的时间
	 * 
	 * @param time
	 *            时间 毫秒
	 * @return 时：分：秒：毫秒
	 */
	private String getFormatTime(long time) {
		time = time / 1000;
		long second = time % 60;
		long minute = (time % 3600) / 60;
		long hour = time / 3600;

		// 毫秒秒显示两位
		// String strMillisecond = "" + (millisecond / 10);
		// 秒显示两位
		String strSecond = ("00" + second)
				.substring(("00" + second).length() - 2);
		// 分显示两位
		String strMinute = ("00" + minute)
				.substring(("00" + minute).length() - 2);
		// 时显示两位
		String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

		return strHour + ":" + strMinute + ":" + strSecond;
		// + strMillisecond;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_step, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;

		case R.id.ment_information:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 计算行走的距离                                                                        
	 */
	private void countDistance() {
		if (StepDetector.CURRENT_SETP % 2 == 0) {
			distance = (StepDetector.CURRENT_SETP / 2) * 3 * step_length * 0.01;
		} else {
			distance = ((StepDetector.CURRENT_SETP / 2) * 3 + 1) * step_length * 0.01;
		}
	}

	/**
	 * 实际的步数
	 */
	private void countStep() {
		if (StepDetector.CURRENT_SETP % 2 == 0) {
			total_step = StepDetector.CURRENT_SETP;
		} else {
			total_step = StepDetector.CURRENT_SETP +1;
		}

		total_step = StepDetector.CURRENT_SETP;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}
