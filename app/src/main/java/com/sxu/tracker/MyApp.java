package com.sxu.tracker;

import android.app.Application;

import com.sxu.trackerlibrary.Tracker;

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
		Tracker.getInstance().init(this);
	}
}
