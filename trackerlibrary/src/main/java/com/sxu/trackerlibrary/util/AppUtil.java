package com.sxu.trackerlibrary.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;

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
		String versionName = "";
		PackageManager pm = context.getPackageManager();
		if (pm != null) {
			try {
				PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
				if (packageInfo != null) {
					versionName = packageInfo.versionName;
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		return versionName;
	}

	public static String getOSVersion() {
		return Build.VERSION.RELEASE;
	}

	public static String getChannel(Context context) {
		String channel = "";
		PackageManager pm = context.getPackageManager();
		if (pm != null) {
			try {
				ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
				if (appInfo != null && appInfo.metaData != null) {
					channel = appInfo.metaData.getString("TRACKER_CHANNEL");
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		return channel;
	}

	public static String getDeviceId(Context context) {
		String deviceId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
		if (TextUtils.isEmpty(deviceId)) {
			deviceId = UUID.randomUUID().toString();
		}

		return deviceId;
	}
}
