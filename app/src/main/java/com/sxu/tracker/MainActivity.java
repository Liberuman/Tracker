package com.sxu.tracker;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sxu.trackerlibrary.bean.Event;
import com.sxu.trackerlibrary.util.HttpManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.normal_text).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String viewPath = Event.generateViewPath(MainActivity.this, null);
				String clickedPath = Event.generateClickedPath(view, null);
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
