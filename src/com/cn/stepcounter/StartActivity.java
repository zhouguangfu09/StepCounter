package com.cn.stepcounter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends Activity {

	private TextView runTextView, strongTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start);

		runTextView = (TextView)findViewById(R.id.run);
		strongTextView = (TextView)findViewById(R.id.strong);

		runTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(StartActivity.this, StepCounterActivity.class);
				Bundle bundle = new Bundle(); 
				bundle.putBoolean("run", false);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		strongTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(StartActivity.this, StepCounterActivity.class);
				Bundle bundle = new Bundle(); 
				bundle.putBoolean("run", true);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

	}


}
