package com.sxu.trackerlibrary.bean;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;

/*******************************************************************************
 * Description: 实体类的基类
 *
 * Author: Freeman
 *
 * Date: 2018/12/6
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class BaseBean implements Serializable {

	public String toJson() {
		return toJson(this);
	}

	public static String toJson(Object object) {
		return new Gson().toJson(object);
	}

	public static String toJson(Object object, Type type) {
		return new Gson().toJson(object, type);
	}

	public static <T> T fromJson(String jsonStr, Class<T> tClass) {
		return new Gson().fromJson(jsonStr, tClass);
	}

	@Override
	public String toString() {
		return toJson();
	}
}
