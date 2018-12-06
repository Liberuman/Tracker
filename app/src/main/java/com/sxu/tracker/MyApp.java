package com.sxu.tracker;

import android.app.Application;

import com.sxu.trackerlibrary.Tracker;
import com.sxu.trackerlibrary.TrackerConfiguration;

/*******************************************************************************
 * Description: 
 *
 * Author: Freeman
 *
 * Date: 2018/11/27
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class MyApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		TrackerConfiguration configuration = new TrackerConfiguration();
		Tracker.getInstance().init(this, configuration);
	}
}
