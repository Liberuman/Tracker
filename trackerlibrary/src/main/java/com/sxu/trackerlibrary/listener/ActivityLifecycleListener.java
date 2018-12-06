package com.sxu.trackerlibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

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
		Log.i("out", "*********onActivityCreated");
		durationMap.put(activity, 0L);
		eventTrackerMap.put(activity, false);
		registerFragmentLifecycleListener(activity);
	}

	@Override
	public void onActivityStarted(Activity activity) {
		Log.i("out", "*********onActivityStarted");
	}

	@Override
	public void onActivityResumed(Activity activity) {
		resumeTimeMap.put(activity, System.currentTimeMillis());
		Log.i("out", "*********onActivityResumed");
		if (!eventTrackerMap.get(activity)) {
			ViewClickedEventTracker.getInstance().setActivityTracker(activity);
			eventTrackerMap.put(activity, true);
		}
	}

	@Override
	public void onActivityPaused(Activity activity) {
		durationMap.put(activity, durationMap.get(activity)
				+ (System.currentTimeMillis() - resumeTimeMap.get(activity)));
		Log.i("out", "*********onActivityPaused");
	}

	@Override
	public void onActivityStopped(Activity activity) {
		Log.i("out", "*********onActivityStopped");
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		long duration = durationMap.get(activity);
		if (duration > 0) {
			EventManager.getInstance().addViewEvent(activity, null, duration);
		}
		resumeTimeMap.remove(activity);
		durationMap.remove(activity);
		eventTrackerMap.remove(activity);
		// todo 释放之后Fragment onStop之后的生命周期不再执行
		// unregisterFragmentLifecycleListener(activity);
		Log.i("out", "*********onActivityDestroyed");
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
