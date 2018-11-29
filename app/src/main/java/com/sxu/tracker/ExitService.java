package com.sxu.tracker;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.sxu.trackerlibrary.db.DatabaseManager;

/*******************************************************************************
 * Description: 
 *
 * Author: Freeman
 *
 * Date: 2018/11/26
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class ExitService extends Service {

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final Handler handler = new Handler();
		for (int i = 0; i < 50; i++) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "服务启动了", Toast.LENGTH_SHORT).show();
				}
			}, 3000);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Toast.makeText(getApplicationContext(), "APP退出了", Toast.LENGTH_SHORT).show();
		DatabaseManager.getInstance(getApplicationContext()).close();
		super.onTaskRemoved(rootIntent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Toast.makeText(getApplicationContext(), "onLowMemory", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		Toast.makeText(getApplicationContext(), "onTrimMemory" + level, Toast.LENGTH_SHORT).show();
	}
}
