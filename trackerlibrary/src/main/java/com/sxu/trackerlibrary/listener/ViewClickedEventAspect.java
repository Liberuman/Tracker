package com.sxu.trackerlibrary.listener;

import android.util.Log;

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
		Log.i("out", "*************View is clicked");
	}
}
