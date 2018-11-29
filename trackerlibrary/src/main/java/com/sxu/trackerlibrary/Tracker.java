package com.sxu.trackerlibrary;

import android.app.Application;
import android.content.Context;

import com.sxu.trackerlibrary.util.LogUtil;

/*******************************************************************************
 * Description: 
 *
 * Author: Freeman
 *
 * Date: 2018/11/26
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class Tracker {

	private Tracker() {

	}

	public static Tracker getInstance() {
		return Singleton.instance;
	}

	public void init(Application context) {
		setActivityLifecycleListener(context);
	}

	public void openLog(boolean open) {
		LogUtil.openLog(open);
	}

	private void setActivityLifecycleListener(Application context) {
		context.registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
	}

	/**
	 * 手动添加事件
	 * @param context
	 * @param eventName
	 */
	public void onEvent(Context context, String eventName) {

	}

	private static class Singleton {
		private final static Tracker instance = new Tracker();
	}
}

/*
* Activity+Fragment的页面：Activity默认i不统计；
* Activity+ViewPager的页面：Activity默认不统计；
* 事件的统计：全局统计，View定位，配置派发；
*
*
*
* */