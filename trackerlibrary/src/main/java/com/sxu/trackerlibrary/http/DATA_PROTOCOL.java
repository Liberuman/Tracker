package com.sxu.trackerlibrary.http;

/*******************************************************************************
 * Description: 数据传输协议
 *
 * Author: Freeman
 *
 * Date: 2018/12/14
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public enum DATA_PROTOCOL {

	JSON(0),
	PROTOCOL_BUFFER(1);

	private final int value;

	DATA_PROTOCOL(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static DATA_PROTOCOL getDataProtocol(int value) {
		if (value == JSON.value) {
			return JSON;
		} else {
			return PROTOCOL_BUFFER;
		}
	}
}
