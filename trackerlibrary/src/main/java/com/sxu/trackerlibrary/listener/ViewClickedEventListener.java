package com.sxu.trackerlibrary;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

/*******************************************************************************
 * Description: 监听View的点击事件
 *
 * Author: Freeman
 *
 * Date: 2018/11/28
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class ViewClickedEventTracker extends View.AccessibilityDelegate {

	private final int FRAGMENT_TAG_KEY = 0xffff0001;

	private ViewClickedEventTracker() {

	}

	public static ViewClickedEventTracker getInstance() {
		return ViewClickedEventTracker.Singleton.instance;
	}

	/**
	 * 设置Activity页面中View的事件监听
	 * @param activity
	 */
	public void setActivityTracker(Activity activity) {
		View contentView = activity.findViewById(android.R.id.content);
		if (contentView != null) {
			setViewClickedTracker(contentView, null);
		}
	}

	/**
	 * 设置Fragment页面中View的事件监听
	 * @param fragment
	 */
	public void setFragmentTracker(Fragment fragment) {
		View contentView = fragment.getView();
		if (contentView != null) {
			setViewClickedTracker(contentView, fragment);
		}
	}

	public void setViewPagerTracker(View contentView) {
		if (contentView == null || !(contentView instanceof ViewGroup)) {
			return;
		}

		for (int i = 0, size = ((ViewGroup) contentView).getChildCount(); i < size; i++) {
			View childView = ((ViewGroup) contentView).getChildAt(i);
			if (!(childView instanceof ViewPager)) {
				if (childView instanceof ViewGroup) {
					setViewPagerTracker(childView);
				}
				continue;
			}

			PagerAdapter adapter = ((ViewPager) childView).getAdapter();
			if (adapter != null && (adapter instanceof FragmentPagerAdapter
					|| adapter instanceof FragmentStatePagerAdapter)) {
				for (int j = 0, count = adapter.getCount(); j < count; j++) {
					Fragment fragment = (Fragment) adapter.instantiateItem((ViewPager)childView, j);
					View fragmentView = fragment.getView();
					if (fragmentView != null) {
						setViewClickedTracker(fragment.getView(), null);
					}
				}
			}
		}
	}

	private void setViewClickedTracker(View view, Fragment fragment) {
		if (view == null) {
			return;
		}

		if (needTracker(view)) {
			if (fragment != null) {
				view.setTag(FRAGMENT_TAG_KEY, fragment);
			}
			view.setAccessibilityDelegate(ViewClickedEventTracker.this);
		}
		if (view instanceof ViewGroup) {
			int childCount = ((ViewGroup) view).getChildCount();
			for (int i = 0; i < childCount; i++) {
				setViewClickedTracker(((ViewGroup) view).getChildAt(i), fragment);
			}
		}
	}

	private boolean needTracker(View view) {
		if (view.getVisibility() == View.VISIBLE && view.isClickable() && ViewCompat.hasOnClickListeners(view)) {
			return true;
		}

		return false;
	}

	@Override
	public void sendAccessibilityEvent(View host, int eventType) {
		super.sendAccessibilityEvent(host, eventType);
		if (AccessibilityEvent.TYPE_VIEW_CLICKED == eventType && host != null) {
			EventManager.getInstance().addClickEvent(host, (Fragment) host.getTag(FRAGMENT_TAG_KEY));
		}
	}

	private static class Singleton {
		private final static ViewClickedEventTracker instance = new ViewClickedEventTracker();
	}
}
