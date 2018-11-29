package com.sxu.trackerlibrary.bean;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import java.io.Serializable;

/*******************************************************************************
 * Description: 事件描述
 *
 * Author: Freeman
 *
 * Date: 2018/11/28
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class Event implements Serializable {

	/**
	 * 事件类型
	 */
	public final static int EVENT_TYPE_CLICKED = 1;   // 点击事件
	public final static int EVENT_TYPE_VIEW = 2;      // 页面停留时长

	private final int MILL_OF_SECOND = 1000;

	/**
	 * 事件类型
	 */
	private int type;
	/**
	 * 事件发生时间
	 */
	private long createTime;
	/**
	 * 页面停留时长
	 */
	private long duration;
	/**
	 * 路径
	 */
	private String path;


	public Event(String path, long duration) {
		this.type = EVENT_TYPE_VIEW;
		this.createTime = System.currentTimeMillis() / MILL_OF_SECOND;
		this.path = path;
		this.duration = duration / MILL_OF_SECOND;
	}

	public Event(String path) {
		this.type = EVENT_TYPE_CLICKED;
		this.createTime = System.currentTimeMillis() / MILL_OF_SECOND;
		this.path = path;
	}

	public int getType() {
		return type;
	}

	public long getCreateTime() {
		return createTime;
	}

	public long getDuration() {
		return duration;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "Event{" +
				"type=" + type +
				", createTime=" + createTime +
				", duration=" + duration +
				", path='" + path + '\'' +
				'}';
	}

	/**
	 * 为预览页面事件生成path
	 * @param context
	 * @param fragment
	 * @return
	 */
	public static String generateViewPath(@NonNull Context context, Fragment fragment) {
		StringBuilder builder = new StringBuilder();
		builder.append(context.getClass().getName());
		if (fragment != null) {
			builder.append("$").append(fragment.getClass().getName());
		}
		return builder.toString();
	}

	/**
	 * 为点击事件生成path
	 * @param view
	 * @param fragment
	 * @return
	 */
	public static String generateClickedPath(@NonNull View view, Fragment fragment) {
		StringBuilder builder = new StringBuilder(generateViewPath(view.getContext(), fragment));
		builder.append("$").append(view.getClass().getName());
		if (view.getId() != View.NO_ID) {
			String resourceName = view.getResources().getResourceEntryName(view.getId());
			if (!TextUtils.isEmpty(resourceName)) {
				builder.append("$").append(resourceName);
			}
		}

		return builder.toString();
	}
}
