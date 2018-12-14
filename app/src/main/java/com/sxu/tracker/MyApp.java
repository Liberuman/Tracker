package com.sxu.tracker;

import android.app.Application;

import com.sxu.trackerlibrary.Tracker;
import com.sxu.trackerlibrary.TrackerConfiguration;
import com.sxu.trackerlibrary.http.UPLOAD_CATEGORY;

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
		TrackerConfiguration configuration = new TrackerConfiguration()
				// 开启log
				.openLog(true)
				// 设置日志的上传策略
				.setUploadCategory(UPLOAD_CATEGORY.REAL_TIME.getValue())
				// 设置获取埋点配置信息列表的URL
				.setConfigUrl("http://m.baidu.com")
				// 设置实时上传日志信息的IP和端口
				.setHostName("127.0.0.1")
				.setHostPort(10001)
				// 设置提交新设备信息的URL
				.setNewDeviceUrl("http://m.baidu.com")
				// 设置需要提交的新设备信息
				.setDeviceInfo("?deviceId=123456&osVersion=8.0")
				// 设置埋点信息上传的URL
				.setUploadUrl("http://m.baidu.com")
				// 设置上传埋点信息的公共参数
				.setCommonParameter("?channel=mi&version=1.0");
		Tracker.getInstance().init(this, configuration);
	}
}
