package com.sxu.tracker;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sxu.trackerlibrary.bean.EventBean;
import com.sxu.trackerlibrary.http.ThreadPoolManager;
import com.sxu.trackerlibrary.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;

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

		FragmentManager fm = getSupportFragmentManager();
		fm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
			@Override
			public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
				super.onFragmentAttached(fm, f, context);
			}

			@Override
			public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
				super.onFragmentResumed(fm, f);
			}

			@Override
			public void onFragmentPaused(@NonNull FragmentManager fm, @NonNull Fragment f) {
				super.onFragmentPaused(fm, f);
			}

			@Override
			public void onFragmentDetached(@NonNull FragmentManager fm, @NonNull Fragment f) {
				super.onFragmentDetached(fm, f);
			}
		}, true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("out", "width==" + findViewById(R.id.normal_text).getWidth());
	}
}
