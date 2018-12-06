package com.sxu.tracker;

import android.app.ActivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sxu.trackerlibrary.bean.EventBean;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.normal_text).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String viewPath = EventBean.generateViewPath(MainActivity.this, null);
				String clickedPath = EventBean.generateClickedPath(view, null);
				Log.i("out", "path===view===" + viewPath + " clicked=" + clickedPath);
				LifecycleActivity.enter(MainActivity.this, LifecycleActivity.STYLE_NORMAL);

				ActivityManager am;
			}
		});
		findViewById(R.id.inner_text).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LifecycleActivity.enter(MainActivity.this, LifecycleActivity.STYLE_INNER);
			}
		});
		findViewById(R.id.pager_text).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LifecycleActivity.enter(MainActivity.this, LifecycleActivity.STYLE_PAGER);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("out", "width==" + findViewById(R.id.normal_text).getWidth());
	}
}
