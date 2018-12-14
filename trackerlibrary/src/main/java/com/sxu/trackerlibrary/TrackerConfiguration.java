package com.sxu.trackerlibrary;

import android.text.TextUtils;

import com.sxu.trackerlibrary.http.DATA_PROTOCOL;
import com.sxu.trackerlibrary.http.UPLOAD_CATEGORY;
import com.sxu.trackerlibrary.util.LogUtil;

import java.net.URL;

/*******************************************************************************
 * Description: 配置信息
 *
 * Author: Freeman
 *
 * Date: 2018/12/5
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class TrackerConfiguration {

	private boolean openLog;
	/**
	 * 实时上传埋点数据需要的IP和端口
	 */
	private int hostPort;
	private String hostName;
	/**
	 * 上传策略，详见{@link UPLOAD_CATEGORY}
	 */
	private int uploadCategory;
	/**
	 * 数据传输协议，详见{@link DATA_PROTOCOL}
	 */
	private int dataProtocol;
	/**
	 * 获取配置信息的URL
	 */
	private String configUrl;
	/**
	 * 上传统计数据的URL
	 */
	private String uploadUrl;
	/**
	 * 上传新设备信息的URL
	 */
	private String newDeviceUrl;
	/**
	 * 上传日志信息的公共参数, URL参数的形式
	 */
	private String commonParameter;
	/**
	 * 保存新设备的信息，将需要上传的设备信息以URL参数的形式拼接，如"deviceId=12345&os_version=7.0"
	 */
	private String deviceInfo;

	private UPLOAD_CATEGORY _uploadCategory;
	private DATA_PROTOCOL _dataProtocol;

	public TrackerConfiguration() {
		openLog = false;
		_uploadCategory = UPLOAD_CATEGORY.NEXT_LAUNCH;
		_dataProtocol = DATA_PROTOCOL.PROTOCOL_BUFFER;
	}

	public TrackerConfiguration openLog(boolean openLog) {
		this.openLog = openLog;
		LogUtil.openLog(openLog);
		return this;
	}

	public String getConfigUrl() {
		return configUrl;
	}

	public TrackerConfiguration setConfigUrl(String configUrl) {
		this.configUrl = configUrl;
		return this;
	}

	public String getNewDeviceUrl() {
		return newDeviceUrl;
	}

	public TrackerConfiguration setNewDeviceUrl(String newDeviceUrl) {
		this.newDeviceUrl = newDeviceUrl;
		return this;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public TrackerConfiguration setHostName(String hostName) {
		this.hostName = hostName;
		return this;
	}

	public String getHostName() {
		return hostName;
	}

	public int getHostPort() {
		return hostPort;
	}
	public TrackerConfiguration setHostPort(int hostPort) {
		this.hostPort = hostPort;
		return this;
	}

	public TrackerConfiguration setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
		return this;
	}

	public TrackerConfiguration setUploadCategory(int uploadCategory) {
		this.uploadCategory = uploadCategory;
		this._uploadCategory = UPLOAD_CATEGORY.getCategory(uploadCategory);
		return this;
	}

	public TrackerConfiguration setDataProtocol(int dataProtocol) {
		this.dataProtocol = dataProtocol;
		this._dataProtocol = DATA_PROTOCOL.getDataProtocol(dataProtocol);
		return this;
	}

	public boolean isOpenLog() {
		return openLog;
	}

	public String getCommonParameter() {
		return commonParameter;
	}

	public TrackerConfiguration setCommonParameter(String commonParameter) {
		this.commonParameter = commonParameter;
		if (!TextUtils.isEmpty(uploadUrl)) {
			uploadUrl += commonParameter;
		}
		return this;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public TrackerConfiguration setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
		return this;
	}

	public UPLOAD_CATEGORY getUploadCategory() {
		return _uploadCategory;
	}

	public DATA_PROTOCOL getDataProtocol() {
		return _dataProtocol;
	}
}
