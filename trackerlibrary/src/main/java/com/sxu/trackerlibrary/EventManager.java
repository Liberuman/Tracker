package com.sxu.trackerlibrary;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.sxu.trackerlibrary.bean.Event;
import com.sxu.trackerlibrary.db.DatabaseManager;
import com.sxu.trackerlibrary.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

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

	private int lastCommitIndex = 0;
	private Context context;
	private Fragment fragment;
	private List<Event> eventList = new ArrayList<>();

	private EventManager() {

	}

	public static EventManager getInstance() {
		return Singleton.instance;
	}

	public void setPageInfo(Context context, Fragment fragment) {
		this.context = context;
		this.fragment = fragment;
	}

	public Fragment getCurrentFragment() {
		return fragment;
	}

	/**
	 * 添加浏览页面事件
	 * @param context
	 * @param fragment
	 * @param duration
	 */
	public void addViewEvent(Context context, Fragment fragment, long duration) {
		addEvent(context, new Event(Event.generateViewPath(context, fragment), duration));
	}

	/**
	 * 添加点击事件
	 * @param view
	 * @param fragment
	 */
	public void addClickEvent(View view, Fragment fragment) {
		addEvent(view.getContext(), new Event(Event.generateClickedPath(view, fragment)));
	}

	private void addEvent(final Context context, final Event eventInfo) {
		LogUtil.i("########## eventInfo==" + eventInfo.toString());
		eventList.add(eventInfo);
		DatabaseManager.getInstance(context.getApplicationContext()).insertData(eventInfo);
	}

	public void clearEvent() {
		eventList.removeAll(eventList.subList(0, lastCommitIndex-1));
	}

	public synchronized void commitEvent() {
		lastCommitIndex = eventList.size();
	}

	private static class Singleton {
		private final static EventManager instance = new EventManager();
	}
}
