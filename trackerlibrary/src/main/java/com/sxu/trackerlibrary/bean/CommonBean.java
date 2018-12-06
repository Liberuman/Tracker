package com.sxu.trackerlibrary.bean;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.sxu.trackerlibrary.util.AppUtil;

import java.io.Serializable;

/*******************************************************************************
 * Description: 埋点统计的公共数据
 *
 * Author: Freeman
 *
 * Date: 2018/11/26
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class CommonBean implements Serializable {

	/**
	 * 包名
	 */
	private String packageName;
	/**
	 * 渠道号
	 */
	private String channel;
	/**
	 * APP版本号
	 */
	private String version;
	/**
	 * 位置信息，格式：经度_维度
	 */
	private String position;
	/**
	 * 手机唯一识别码，为了避免用户不授权，使用自己生成的唯一码
	 */
	private String deviceId;
	/**
	 * 手机品牌
	 */
	private String phoneBrand;
	/**
	 * 手机系统版本
	 */
	private String OSVersion;

	private CommonBean(Context context) {
		initData(context);
	}

	private void initData(Context context) {
		packageName = AppUtil.getPackageName(context);
		channel = "";
		version = AppUtil.getAppVersionName(context);
		phoneBrand = AppUtil.getPhoneBrand(context);
		OSVersion = AppUtil.getOSVersion();
	}

	public String getParamters() {
		StringBuilder builder = new StringBuilder();
		builder.append("packageName=").append(packageName)
				.append("&channel=").append(channel)
				.append("&version=").append(version)
				.append("&position=").append(position)
				.append("&deviceId=").append(deviceId)
				.append("&phoneBrand=").append(phoneBrand)
				.append("&OSVersion=").append(OSVersion);
		return builder.toString();
	}
}
