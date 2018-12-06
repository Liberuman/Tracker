package com.sxu.trackerlibrary;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sxu.trackerlibrary.bean.Event;
import com.sxu.trackerlibrary.message.EventInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
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

	private DatagramSocket socket;

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
		if (socket == null) {
			String hostName = intent.getStringExtra(EXTRA_KEY_HOST_NAME);
			int hostPort = intent.getIntExtra(EXTRA_KEY_HOST_PORT, 0);
			try {
				SocketAddress address = new InetSocketAddress(hostName, hostPort);
				socket = new DatagramSocket(address);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		if (socket != null) {
			Event eventInfo = (Event) intent.getSerializableExtra(EXTRA_KEY_EVENT_INFO);
			if (eventInfo != null) {
				try {
					byte[] data = eventInfo.toJson().getBytes("UTF-8");
					if (data != null) {
						socket.send(new DatagramPacket(data, data.length));
					}
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	public static void enter(Context context, Event eventInfo) {
		Intent intent = new Intent(context, UploadEventService.class);
		intent.putExtra(EXTRA_KEY_EVENT_INFO, eventInfo);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(intent);
	}

	public static void enter(Context context, String hostName, int hostPort, Event eventInfo) {
		Intent intent = new Intent(context, UploadEventService.class);
		intent.putExtra(EXTRA_KEY_HOST_NAME, hostName);
		intent.putExtra(EXTRA_KEY_HOST_PORT, hostPort);
		intent.putExtra(EXTRA_KEY_EVENT_INFO, eventInfo);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(intent);
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
}
