package com.sxu.trackerlibrary.http;

/*******************************************************************************
 * Description: 网络应答数据协议
 *
 * Author: Freeman
 *
 * Date: 2018/12/6
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class BaseProtocolBean<T> extends BaseBean {

	public int code;
	public String msg;
	public T data;
}
