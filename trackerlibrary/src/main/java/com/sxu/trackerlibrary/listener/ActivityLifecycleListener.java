package com.sxu.trackerlibrary.listener;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.sxu.trackerlibrary.Tracker;
import com.sxu.trackerlibrary.util.LogUtil;

import java.util.Map;
import java.util.WeakHashMap;

/*******************************************************************************
 * Description: Activity生命周期监听
 *
 * Author: Freeman
 *
 * Date: 2018/11/27
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

	private Map<Context, Long> resumeTimeMap = new WeakHashMap<>();
	private Map<Context, Long> durationMap = new WeakHashMap<>();
	private Map<Context, Boolean> eventTrackerMap = new WeakHashMap<>();
	private Map<Context, FragmentLifecycleListener> listenerMap = new WeakHashMap<>();

	@Override
	public void onActivityCreated(Activity activity, Bundle bundle) {
		durationMap.put(activity, 0L);
		eventTrackerMap.put(activity, false);
		registerFragmentLifecycleListener(activity);
	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {
		resumeTimeMap.put(activity, System.currentTimeMillis());
		if (!eventTrackerMap.get(activity)) {
			ViewClickedEventListener.getInstance().setActivityTracker(activity);
			eventTrackerMap.put(activity, true);
		}
		LogUtil.i(activity.getClass().getSimpleName() + " onActivityResumed");
	}

	@Override
	public void onActivityPaused(Activity activity) {
		durationMap.put(activity, durationMap.get(activity)
				+ (System.currentTimeMillis() - resumeTimeMap.get(activity)));
		LogUtil.i(activity.getClass().getSimpleName() + " onActivityPaused");
	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		long duration = durationMap.get(activity);
		if (duration > 0) {
			Tracker.getInstance().addViewEvent(activity, null, duration);
		}
		resumeTimeMap.remove(activity);
		durationMap.remove(activity);
		eventTrackerMap.remove(activity);
		// 注销之后Fragment onStop之后的生命周期将不再执行
		// unregisterFragmentLifecycleListener(activity);
	}

	private void registerFragmentLifecycleListener(final Activity context) {
		if (context instanceof FragmentActivity) {
			FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
			FragmentLifecycleListener listener = new FragmentLifecycleListener();
			listenerMap.put(context, listener);
			fm.registerFragmentLifecycleCallbacks(listener, true);
		}
	}

	private void unregisterFragmentLifecycleListener(final Activity context) {
		FragmentLifecycleListener listener = listenerMap.get(context);
		if (listener != null) {
			((FragmentActivity)context).getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(listener);
			listenerMap.remove(context);
		}
	}
}
