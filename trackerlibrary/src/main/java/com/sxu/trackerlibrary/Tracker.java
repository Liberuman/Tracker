package com.sxu.trackerlibrary;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxu.trackerlibrary.bean.BaseBean;
import com.sxu.trackerlibrary.bean.BaseProtocolBean;
import com.sxu.trackerlibrary.bean.ConfigBean;
import com.sxu.trackerlibrary.bean.Constants;
import com.sxu.trackerlibrary.bean.Event;
import com.sxu.trackerlibrary.bean.TrackerConfiguration;
import com.sxu.trackerlibrary.db.DatabaseManager;
import com.sxu.trackerlibrary.message.EventInfo;
import com.sxu.trackerlibrary.util.HttpManager;

import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 * Description: 事件管理
 *
 * Author: Freeman
 *
 * Date: 2018/11/28
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class EventManager {

	private boolean requestConfig = false;
	private long lastItemEventTime = 0;
	/**
	 * 需要收集的事件列表
	 */
	private List<String> validEventPathList = null;
	/**
	 * 保存产生的事件
	 */
	private List<Event> eventList = new ArrayList<>();

	private Context context;
	private TrackerConfiguration config;
	private static EventManager instance;

	private final int UPLOAD_EVENT_WHAT = 0xff01;
	private final int MAX_EVENT_COUNT = 50;
	private final int DEFAULT_CLEAR_COUNT = 30;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == UPLOAD_EVENT_WHAT) {
				uploadEventInfo();
				if (config.getUploadCategory() != Constants.UPLOAD_CATEGORY.NEXT_LAUNCH) {
					handler.sendEmptyMessageDelayed(UPLOAD_EVENT_WHAT, config.getUploadCategory().getValue() * 1000);
				}
			}
		}
	};

	private EventManager() {

	}

	public static EventManager getInstance() {
		if (instance == null) {
			synchronized (EventManager.class) {
				if (instance == null) {
					instance = new EventManager();
				}
			}
		}

		return instance;
	}

	public void init(Context context, TrackerConfiguration config) {
		if (config == null) {
			throw new IllegalArgumentException("config can't be null");
		}

		setTrackerConfig(config);
		this.context = context.getApplicationContext();
		if (config.getUploadCategory() == Constants.UPLOAD_CATEGORY.REAL_TIME) {
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
			}

			@Override
			public void onError(int code, String errMsg) {
				requestConfig = true;
				validEventPathList = getValidEventList(null);
			}
		});
	}

	/**
	 * 获取需要收集的埋点路径列表
	 * @param validEventList
	 * @return
	 */
	private List<String> getValidEventList(List<Event> validEventList) {
		List<String> validEventPathList = new ArrayList<>();
		if (validEventList != null && validEventList.size() > 0) {
			for (Event event : validEventList) {
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
		addEvent(new Event(Event.generateViewPath(context, fragment), duration));
	}

	/**
	 * 添加点击事件
	 * @param view
	 * @param fragment
	 */
	public void addClickEvent(View view, Fragment fragment) {
		addEvent(new Event(Event.generateClickedPath(view, fragment)));
	}

	private void addEvent(final Event eventInfo) {
		if (config.getUploadCategory() == Constants.UPLOAD_CATEGORY.REAL_TIME) {
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
	private void commitRealTimeEvent(Event eventInfo) {
		UploadEventService.enter(context, eventInfo);
	}

	/**
	 * 上传埋点数据
	 */
	private synchronized void uploadEventInfo() {
		List<Event> appendCommitEventList = DatabaseManager.getInstance(context.getApplicationContext()).getAllData();
		if (appendCommitEventList != null && appendCommitEventList.size() > 0) {
			lastItemEventTime = appendCommitEventList.get(appendCommitEventList.size() - 1).getEventTime();
			realUploadEventInfo(getByteData(appendCommitEventList));
		}
	}

	private byte[] convertDataToJson(List<Event> eventList) {
		return BaseBean.toJson(eventList, new TypeToken<List<Event>>(){}.getType()).getBytes();
	}

	private byte[] convertDataToProtocolBuffer(List<Event> eventList) {
		EventInfo.EventList.Builder builder = EventInfo.EventList.newBuilder();
		for (Event event : eventList) {
			builder.addEvents(EventInfo
					.Event.newBuilder()
					.setPath(event.getPath())
					.setType(event.getType())
					.setDuration(event.getDuration())
					.setEventTime(event.getEventTime()));
		}

		return builder.build().toByteArray();
	}

	private byte[] getByteData(List<Event> eventList) {
		return config.getDataProtocol() == Constants.DATA_PROTOCOL.JSON ? convertDataToJson(eventList)
				: convertDataToProtocolBuffer(eventList);
	}

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
				}

				@Override
				public void onError(int code, String errMsg) {

				}
			});
	}
}
