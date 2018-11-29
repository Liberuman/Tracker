package com.sxu.trackerlibrary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sxu.trackerlibrary.ThreadPoolManager;
import com.sxu.trackerlibrary.bean.BaseBean;
import com.sxu.trackerlibrary.bean.ClickEvent;
import com.sxu.trackerlibrary.bean.DurationEvent;
import com.sxu.trackerlibrary.bean.Event;

import static com.sxu.trackerlibrary.db.DatabaseHelper.DEFAULT_TABLE_NAME;

/*******************************************************************************
 * Description: 
 *
 * Author: Freeman
 *
 * Date: 2018/11/26
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class DatabaseManager {

	private SQLiteDatabase database;
	private AddEventToDBTask addTask;
	private static DatabaseManager instance;

	private DatabaseManager(Context context) {
		addTask = new AddEventToDBTask();
		database = new DatabaseHelper(context.getApplicationContext()).getWritableDatabase();
	}

	public static DatabaseManager getInstance(Context context) {
		if (instance == null) {
			synchronized (DatabaseManager.class) {
				if (instance == null) {
					instance = new DatabaseManager(context.getApplicationContext());
				}
			}
		}

		return instance;
	}

	/**
	 * 将事件暂存在数据库
	 * @param eventInfo
	 */
	public void insertData(Event eventInfo) {
		addTask.updateData(eventInfo);
		ThreadPoolManager.executeTask(addTask);
	}

	/**
	 * 事件上传后删除最后一个事件之前的所有事件
	 * @param lastId
	 */
	public void removeData(String lastId) {
		database.delete(DEFAULT_TABLE_NAME, "id=?", new String[]{lastId});
	}

	public void close() {
		if (database != null && database.isOpen()) {
			database.close();
		}
	}

	private class AddEventToDBTask implements Runnable {

		private Event eventInfo;

		public void updateData(Event eventInfo) {
			this.eventInfo = eventInfo;
		}

		public void insertData(Event eventInfo) {
			ContentValues values = new ContentValues();
			values.put("type", eventInfo.getType());
			values.put("path", eventInfo.getPath());
			values.put("duration", eventInfo.getDuration());
			values.put("create_time", eventInfo.getCreateTime());
			database.insert(DEFAULT_TABLE_NAME, null, values);
		}

		@Override
		public void run() {
			insertData(eventInfo);
		}
	}
}
