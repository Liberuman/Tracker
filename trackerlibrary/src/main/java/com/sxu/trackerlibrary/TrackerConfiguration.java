package com.sxu.trackerlibrary.bean;

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

	private boolean isDebug;
	private boolean openLog;
	private int hostPort;
	private int uploadCategory;
	private int dataProtocol;
	private String configUrl;
	private String uploadUrl;
	private Constants.UPLOAD_CATEGORY _uploadCategory;
	private Constants.DATA_PROTOCOL _dataProtocol;

	public TrackerConfiguration() {
		isDebug = false;
		openLog = false;
		_uploadCategory = Constants.UPLOAD_CATEGORY.NEXT_LAUNCH;
		_dataProtocol = Constants.DATA_PROTOCOL.PROTOCOL_BUFFER;
		LogUtil.openLog(openLog);
	}

	public TrackerConfiguration isDebug(boolean isDebug) {
		this.isDebug = isDebug;
		return this;
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

	public String getUploadUrl() {
		return uploadUrl;
	}

	public String getHostName() {
		String hostName = null;
		try {
			hostName = new URL(uploadUrl).getHost();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

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
		this._uploadCategory = Constants.UPLOAD_CATEGORY.getCategory(uploadCategory);
		return this;
	}

	public TrackerConfiguration setDataProtocol(int dataProtocol) {
		this.dataProtocol = dataProtocol;
		this._dataProtocol = Constants.DATA_PROTOCOL.getDataProtocol(dataProtocol);
		return this;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public boolean isOpenLog() {
		return openLog;
	}

	public Constants.UPLOAD_CATEGORY getUploadCategory() {
		return _uploadCategory;
	}

	public Constants.DATA_PROTOCOL getDataProtocol() {
		return _dataProtocol;
	}
}
