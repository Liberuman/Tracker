package com.sxu.trackerlibrary.listener;

import android.support.v4.app.Fragment;

import com.sxu.trackerlibrary.listener.OnFragmentVisibleListener;

/*******************************************************************************
 * Description: 解决Fragment在显示/隐藏或者在ViewPager中切换时不走生命周期的问题
 *
 * Author: Freeman
 *
 * Date: 2018/11/27
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class LifecycleFragment extends Fragment {

	protected OnFragmentVisibleListener listener;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (listener != null && isResumed()) {
			listener.onVisibleChanged(this, isVisibleToUser);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (listener != null) {
			listener.onVisibleChanged(this, !hidden);
		}
	}
}
