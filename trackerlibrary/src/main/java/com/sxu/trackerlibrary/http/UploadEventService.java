package com.sxu.trackerlibrary.http;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.sxu.trackerlibrary.bean.EventBean;
import com.sxu.trackerlibrary.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/*******************************************************************************
 * Description: 实时发送信息
 *
 * Author: Freeman
 *
 * Date: 2018/12/6
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class UploadEventService extends Service {

	private String hostName;
	private int hostPort = 0;
	private EventBean eventInfo;
	private DatagramSocket socket;
	private Runnable runnable = new MyRunnable();

	private final static String EXTRA_KEY_HOST_NAME = "hostName";
	private final static String EXTRA_KEY_HOST_PORT = "hostPort";
	private final static String EXTRA_KEY_EVENT_INFO = "eventInfo";

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (hostName == null) {
			hostName = intent.getStringExtra(EXTRA_KEY_HOST_NAME);
		}
		if (hostPort == 0) {
			hostPort = intent.getIntExtra(EXTRA_KEY_HOST_PORT, 0);
		}
		eventInfo = (EventBean) intent.getSerializableExtra(EXTRA_KEY_EVENT_INFO);
		ThreadPoolManager.executeTask(runnable);

		return super.onStartCommand(intent, flags, startId);
	}

	public static void enter(Context context, EventBean eventInfo) {
		Intent intent = new Intent(context, UploadEventService.class);
		intent.putExtra(EXTRA_KEY_EVENT_INFO, eventInfo);
		context.startService(intent);
	}

	public static void enter(Context context, String hostName, int hostPort, EventBean eventInfo) {
		Intent intent = new Intent(context, UploadEventService.class);
		intent.putExtra(EXTRA_KEY_HOST_NAME, hostName);
		intent.putExtra(EXTRA_KEY_HOST_PORT, hostPort);
		intent.putExtra(EXTRA_KEY_EVENT_INFO, eventInfo);
		context.startService(intent);
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private void initNotificationChannel(String channelId, String channelName) {
		NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
		channel.setLightColor(Color.GRAY);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (notificationManager != null) {
			notificationManager.createNotificationChannel(channel);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (socket != null && socket.isConnected()) {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	private class MyRunnable implements Runnable {

		@Override
		public void run() {
			if (socket == null) {
				try {
					SocketAddress address = new InetSocketAddress(hostName, hostPort);
					socket = new DatagramSocket(null);
					socket.setReuseAddress(true);
					socket.bind(address);
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}

			if (socket != null) {
				LogUtil.i("正在实时上传数据");
				if (eventInfo != null) {
					try {
						byte[] data = eventInfo.toJson().getBytes("UTF-8");
						if (data != null) {
							socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(hostName), hostPort));
						}
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
			}
		}
	}
}
