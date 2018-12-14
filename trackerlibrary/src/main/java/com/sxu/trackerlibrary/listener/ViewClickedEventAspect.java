package com.sxu.trackerlibrary.listener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/*******************************************************************************
 * Description: 监听全局的点击事件
 *
 * Author: Freeman
 *
 * Date: 2018/11/28
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
@Aspect
public class ViewClickedEventAspect {

	@After("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
	public void viewClicked(final ProceedingJoinPoint joinPoint) {
		/**
		 * 保存点击事件
		 */
		Context context = getContext(joinPoint.getThis());
		try {
			joinPoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace(System.err);
		}
	}

	private Context getContext(Object object) {
		Context context = null;
		if (object instanceof Activity) {
			context = (Context) object;
		} else if (object instanceof Fragment) {
			context = ((Fragment) object).getActivity();
		} else if (object instanceof android.support.v4.app.Fragment) {
			context = ((android.support.v4.app.Fragment) object).getActivity();
		} else if (object instanceof Dialog) {
			context = ((Dialog) object).getContext();
		} else if (object instanceof View) {
			context = ((View) object).getContext();
		}

		return context;
	}
}
