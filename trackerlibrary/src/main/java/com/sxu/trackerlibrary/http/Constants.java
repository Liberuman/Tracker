package com.sxu.trackerlibrary.bean;

/*******************************************************************************
 * Description: 
 *
 * Author: Freeman
 *
 * Date: 2018/12/5
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class Constants {

	/**
	 * 数据传输协议
	 */
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

	/**
	 * 数据上传策略
	 */
	public enum UPLOAD_CATEGORY {
		REAL_TIME(0),           // 实时传输，用于收集配置信息
		NEXT_LAUNCH(-1),        // 下次启动时上传
		NEXT_15_MINUTER(15),    // 每15分钟上传一次
		NEXT_30_MINUTER(30),    // 每30分钟上传一次
		NEXT_KNOWN_MINUTER(-1); // 使用服务器下发的上传策略

		private int value;

		UPLOAD_CATEGORY(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static UPLOAD_CATEGORY getCategory(int value) {
			UPLOAD_CATEGORY category;
			if (value == REAL_TIME.value) {
				category = REAL_TIME;
			} else if (value == NEXT_LAUNCH.value) {
				category = NEXT_LAUNCH;
			} else if (value == NEXT_15_MINUTER.value) {
				category = NEXT_15_MINUTER;
			} else if (value == NEXT_30_MINUTER.value) {
				category = NEXT_30_MINUTER;
			} else {
				NEXT_KNOWN_MINUTER.value = value;
				category = NEXT_KNOWN_MINUTER;
			}

			return category;
		}
	}
}
