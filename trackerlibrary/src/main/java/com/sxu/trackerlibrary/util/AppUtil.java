package com.sxu.trackerlibrary.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/*******************************************************************************
 * Description: 获取APP的相关数据
 *
 * Author: Freeman
 *
 * Date: 2018/11/26
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class AppUtil {

	/**
	 * 获取包名
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		return context.getPackageName();
	}

	public static String getPhoneBrand(Context context) {
		return Build.DEVICE;
	}

	public static String getAppVersionName(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return "";
	}

	public static String getOSVersion() {
		return Build.VERSION.RELEASE;
	}


}
