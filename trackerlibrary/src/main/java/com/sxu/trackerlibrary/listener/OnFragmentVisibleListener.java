package com.sxu.trackerlibrary.listener;

import android.support.v4.app.Fragment;

/*******************************************************************************
 * Description: 监听Fragment显示与否
 *
 * Author: Freeman
 *
 * Date: 2018/11/27
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public interface OnFragmentVisibleListener {

	/**
	 * Fragment的onHidden或setUserVisibleHint被调用时触发
	 * @param visible
	 */
	void onVisibleChanged(Fragment f, boolean visible);
}
