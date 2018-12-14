package com.sxu.trackerlibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.sxu.trackerlibrary.bean.CommonBean;
import com.sxu.trackerlibrary.http.BaseBean;
import com.sxu.trackerlibrary.http.BaseProtocolBean;
import com.sxu.trackerlibrary.bean.ConfigBean;
import com.sxu.trackerlibrary.bean.EventBean;
import com.sxu.trackerlibrary.db.DatabaseManager;
import com.sxu.trackerlibrary.http.DATA_PROTOCOL;
import com.sxu.trackerlibrary.http.UPLOAD_CATEGORY;
import com.sxu.trackerlibrary.http.UploadEventService;
import com.sxu.trackerlibrary.listener.ActivityLifecycleListener;
import com.sxu.trackerlibrary.message.EventInfo;
import com.sxu.trackerlibrary.http.HttpManager;
import com.sxu.trackerlibrary.util.AppUtil;
import com.sxu.trackerlibrary.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/*******************************************************************************
 * Description: 事件管理
 *
 * Author: Freeman
 *
 * Date: 2018/11/28
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class Tracker {

	private boolean isInit = false;
	private boolean requestConfig = false;
	private long lastItemEventTime = 0;
	/**
	 * 需要收集的事件列表
	 */
	private List<String> validEventPathList = null;
	/**
	 * 保存产生的事件
	 */
	private List<EventBean> eventList = new ArrayList<>();

	private Context context;
	private TrackerConfiguration config;
	private SharedPreferences preferences;

	private final int UPLOAD_EVENT_WHAT = 0xff01;
	private final int MAX_EVENT_COUNT = 50;
	private final int DEFAULT_CLEAR_COUNT = 30;
	// 是否是新设备
	private final String KEY_IS_NEW_DEVICE = "key_is_new_device";

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == UPLOAD_EVENT_WHAT) {
				uploadEventInfo();
				if (config.getUploadCategory() != UPLOAD_CATEGORY.NEXT_LAUNCH) {
					handler.sendEmptyMessageDelayed(UPLOAD_EVENT_WHAT, config.getUploadCategory().getValue() * 1000);
				}
			}
		}
	};

	private Tracker() {

	}

	public static Tracker getInstance() {
		return Singleton.instance;
	}

	public void init(Application context, TrackerConfiguration config) {
		if (config == null) {
			throw new IllegalArgumentException("config can't be null");
		}

		isInit = true;
		this.context = context;
		setTrackerConfig(config);
		preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
		if (preferences.getBoolean(KEY_IS_NEW_DEVICE, true) && !TextUtils.isEmpty(config.getNewDeviceUrl())) {
			submitDeviceInfo();
		}
		context.registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
		if (config.getUploadCategory() == UPLOAD_CATEGORY.REAL_TIME) {
			UploadEventService.enter(context, config.getHostName(), config.getHostPort(), null);
		} else {
			if (!requestConfig) {
				startRequestConfig();
			}
		}
	}

	private void setTrackerConfig(TrackerConfiguration config) {
		if (config != null) {
			this.config = config;
		}
	}

	/**
	 * 从服务器请求埋点的配置信息
	 */
	private void startRequestConfig() {
		HttpManager.getInstance(context).getQuery(config.getConfigUrl(), ConfigBean.class,
				new HttpManager.OnRequestListener<ConfigBean.DataBean>() {
			@Override
			public void onSuccess(ConfigBean.DataBean result) {
				requestConfig = true;
				if (result != null) {
					setTrackerConfig(result.baseConfig);
					validEventPathList = getValidEventList(result.validEventList);
				}
				handler.sendEmptyMessage(UPLOAD_EVENT_WHAT);
				LogUtil.i("get config success");
			}

			@Override
			public void onError(int code, String errMsg) {
				requestConfig = true;
				validEventPathList = getValidEventList(null);
				LogUtil.i("get config failed " + errMsg);
			}
		});
	}

	/**
	 * 获取需要收集的埋点路径列表
	 * @param validEventList
	 * @return
	 */
	private List<String> getValidEventList(List<EventBean> validEventList) {
		List<String> validEventPathList = new ArrayList<>();
		if (validEventList != null && validEventList.size() > 0) {
			for (EventBean event : validEventList) {
				validEventPathList.add(event.getPath());
			}
		}

		return validEventPathList;
	}

	/**
	 * 添加浏览页面事件
	 * @param context
	 * @param fragment
	 * @param duration
	 */
	public void addViewEvent(Context context, Fragment fragment, long duration) {
		addEvent(new EventBean(EventBean.generateViewPath(context, fragment), duration));
	}

	/**
	 * 添加点击事件
	 * @param view
	 * @param fragment
	 */
	public void addClickEvent(View view, Fragment fragment) {
		addEvent(new EventBean(EventBean.generateClickedPath(view, fragment)));
	}

	private void addEvent(final EventBean eventInfo) {
		if (!isInit) {
			throw new IllegalArgumentException("Not init Tracker!!!");
		}

		LogUtil.i(eventInfo.toString());
		if (config.getUploadCategory() == UPLOAD_CATEGORY.REAL_TIME) {
			commitRealTimeEvent(eventInfo);
		} else {
			if (validEventPathList != null && validEventPathList.size() > 0
					&& !validEventPathList.contains(eventInfo.getPath())) {
				return;
			}
			eventList.add(eventInfo);
			DatabaseManager.getInstance(context.getApplicationContext()).insertData(eventInfo);

			if (eventList.size() >= MAX_EVENT_COUNT) {
				eventList.remove(eventList.subList(0, DEFAULT_CLEAR_COUNT));
			}
		}
	}

	/**
	 * 通过后台服务实时上传埋点数据
	 * @param eventInfo
	 */
	private void commitRealTimeEvent(EventBean eventInfo) {
		UploadEventService.enter(context, eventInfo);
	}

	/**
	 * 上传埋点数据
	 */
	private synchronized void uploadEventInfo() {
		List<EventBean> appendCommitEventList = DatabaseManager.getInstance(context.getApplicationContext()).getAllData();
		if (appendCommitEventList != null && appendCommitEventList.size() > 0) {
			lastItemEventTime = appendCommitEventList.get(appendCommitEventList.size() - 1).getEventTime();
			realUploadEventInfo(getByteData(appendCommitEventList));
		}
	}

	private byte[] convertDataToJson(List<EventBean> eventList) {
		return BaseBean.toJson(eventList, new TypeToken<List<EventBean>>(){}.getType()).getBytes();
	}

	private byte[] convertDataToProtocolBuffer(List<EventBean> eventList) {
		EventInfo.EventList.Builder builder = EventInfo.EventList.newBuilder();
		for (EventBean event : eventList) {
			builder.addEvents(EventInfo
					.Event.newBuilder()
					.setPath(event.getPath())
					.setType(event.getType())
					.setDuration(event.getDuration())
					.setEventTime(event.getEventTime()));
		}

		return builder.build().toByteArray();
	}

	private byte[] getByteData(List<EventBean> eventList) {
		return config.getDataProtocol() == DATA_PROTOCOL.JSON ? convertDataToJson(eventList)
				: convertDataToProtocolBuffer(eventList);
	}

	/**
	 * 上传埋点数据
	 * @param data
	 */
	private void realUploadEventInfo(byte[] data) {
		if (data == null || data.length == 0) {
			return;
		}

		HttpManager.getInstance(context).postQuery(config.getUploadUrl(), data, BaseProtocolBean.class,
			new HttpManager.OnRequestListener() {
				@Override
				public void onSuccess(Object result) {
					if (lastItemEventTime != 0) {
						DatabaseManager.getInstance(context).removeData(lastItemEventTime);
						lastItemEventTime = 0;
					}
					LogUtil.i("event info upload success");
				}

				@Override
				public void onError(int code, String errMsg) {
					LogUtil.i("event info upload failed " + errMsg);
				}
			});
	}

	/**
	 * 提交新设备信息到服务器
	 */
	private void submitDeviceInfo() {
		String deviceInfo = config.getDeviceInfo();
		if (TextUtils.isEmpty(deviceInfo)) {
			CommonBean commonInfo = new CommonBean(context);
			deviceInfo = commonInfo.getParameters(config.getNewDeviceUrl().contains("?") ? "&" : "?");
		}
		HttpManager.getInstance(context).postQuery(config.getNewDeviceUrl(), deviceInfo,
				BaseProtocolBean.class, new HttpManager.OnRequestListener() {
			@Override
			public void onSuccess(Object result) {
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(KEY_IS_NEW_DEVICE, false);
				editor.apply();
				LogUtil.i("deviceInfo submit success");
			}

			@Override
			public void onError(int code, String errMsg) {
				LogUtil.i("deviceInfo submit failed " + errMsg);
			}
		});
	}

	public static class Singleton {
		private final static Tracker instance = new Tracker();
	}
}
