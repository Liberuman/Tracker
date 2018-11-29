package com.sxu.trackerlibrary;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*******************************************************************************
 * Description: 
 *
 * Author: Freeman
 *
 * Date: 2018/11/29
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class ThreadPoolManager {

	/**
	 * 线程队列默认容量
	 */
	private final static int DEFAULT_QUEUE_SIZE = 128;
	/**
	 * CPU核数
	 */
	private final static int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	/**
	 * 核心线程数
	 */
	private final static int CORE_THREAD_COUNT = CPU_COUNT + 1;
	/**
	 * 最大线程数
	 */
	private final static int MAX_THREAD_COUNT = CPU_COUNT * 2 + 1;

	private static Executor executor;

	static {
		init();
	}

	private static void init() {
		if (executor == null) {
			executor = new ThreadPoolExecutor(CORE_THREAD_COUNT, MAX_THREAD_COUNT, 30L, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(DEFAULT_QUEUE_SIZE));
		}
	}

	public static void executeTask(Runnable runnable) {
		executor.execute(runnable);
	}
}
