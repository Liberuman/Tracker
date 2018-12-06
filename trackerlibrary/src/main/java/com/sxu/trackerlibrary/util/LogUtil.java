package com.sxu.trackerlibrary.util;

import android.util.Log;

/*******************************************************************************
 * Description: 打印日志
 *
 * Author: Freeman
 *
 * Date: 2018/11/28
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class LogUtil {

	private final static String tag = "Tracker: ";
	private static boolean isOpened = false;

	public static void openLog(boolean open) {
		isOpened = open;
	}

	public static void i(String info) {
		if (isOpened) {
			Log.i(tag, info);
		}
	}

	public static void e(String info) {
		if (isOpened) {
			Log.e(tag, info);
		}
	}

	public static void d(String info) {
		if (isOpened) {
			Log.d(tag, info);
		}
	}

	public static void v(String info) {
		if (isOpened) {
			Log.v(tag, info);
		}
	}

	public static void w(String info) {
		if (isOpened) {
			Log.w(tag, info);
		}
	}
}
