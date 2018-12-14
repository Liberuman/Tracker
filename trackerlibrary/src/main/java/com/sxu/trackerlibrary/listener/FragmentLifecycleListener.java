package com.sxu.trackerlibrary.listener;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.sxu.trackerlibrary.Tracker;
import com.sxu.trackerlibrary.util.LogUtil;

import java.util.Map;
import java.util.WeakHashMap;

/*******************************************************************************
 * Description: Fragment生命周期监听
 *
 * Author: Freeman
 *
 * Date: 2018/11/27
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class FragmentLifecycleListener extends FragmentManager.FragmentLifecycleCallbacks implements OnFragmentVisibleListener {

	private Map<Fragment, Long> resumeTimeMap = new WeakHashMap<>();
	private Map<Fragment, Long> durationMap = new WeakHashMap<>();
	private Map<Fragment, Boolean> eventTrackerMap = new WeakHashMap<>();

	@Override
	public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
		super.onFragmentAttached(fm, f, f.getActivity());
		resumeTimeMap.put(f, 0L);
		durationMap.put(f, 0L);
		eventTrackerMap.put(f, false);
		if (f instanceof LifecycleFragment) {
			((LifecycleFragment) f).listener = this;
		}
	}

	@Override
	public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
		super.onFragmentResumed(fm, f);
		if (!f.isHidden() && f.getUserVisibleHint()) {
			onVisibleChanged(f, true);
		}
		LogUtil.i(f.getClass().getSimpleName() + " onFragmentResumed");
	}

	@Override
	public void onFragmentPaused(@NonNull FragmentManager fm, @NonNull Fragment f) {
		super.onFragmentPaused(fm, f);
		if (!f.isHidden() && f.getUserVisibleHint()) {
			onVisibleChanged(f, false);
		}
		// 解决viewpager中fragment切换时事件统计失效的问题
		eventTrackerMap.put(f, false);
		LogUtil.i(f.getClass().getSimpleName() + " onFragmentPaused");
	}

	@Override
	public void onFragmentDetached(@NonNull FragmentManager fm, @NonNull Fragment f) {
		super.onFragmentDetached(fm, f);
		long duration = durationMap.get(f);
		if (duration > 0) {
			Tracker.getInstance().addViewEvent(f.getActivity(), f, duration);
		}
		resumeTimeMap.remove(f);
		durationMap.remove(f);
		eventTrackerMap.remove(f);
	}

	@Override
	public void onVisibleChanged(Fragment f, boolean visible) {
		if (visible) {
			resumeTimeMap.put(f, System.currentTimeMillis());
			if (!eventTrackerMap.get(f)) {
				ViewClickedEventListener.getInstance().setFragmentTracker(f);
				eventTrackerMap.put(f, true);
			}
		} else {
			durationMap.put(f, durationMap.get(f) + System.currentTimeMillis() - resumeTimeMap.get(f));
		}
		LogUtil.i(f.getClass().getSimpleName() + " Visible is " + visible);
	}
}
