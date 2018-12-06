package com.sxu.trackerlibrary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sxu.trackerlibrary.http.ThreadPoolManager;
import com.sxu.trackerlibrary.bean.EventBean;
import com.sxu.trackerlibrary.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

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
	public void insertData(EventBean eventInfo) {
		addTask.updateData(eventInfo);
		ThreadPoolManager.executeTask(addTask);
	}

	/**
	 * 将事件暂存在数据库
	 */
	public List<EventBean> getAllData() {
		List<EventBean> eventList = null;
		Cursor cursor = database.query(DEFAULT_TABLE_NAME, null, null, null,
				null, null, null);
		if (cursor != null) {
			eventList = new ArrayList<>();
			while (cursor.moveToNext()) {
				String path = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KEY_PATH));
				int type = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_KEY_TYPE));
				long duration = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_KEY_DURATION));
				long eventTime = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_KEY_EVENT_TIME));
				if (type == EventBean.EVENT_TYPE_VIEW) {
					eventList.add(new EventBean(path, duration, eventTime));
				} else if (type == 2) {
					eventList.add(new EventBean(eventTime, path));
				} else {
					/**
					 * Nothing
					 */
				}
			}
		}

		return eventList;
	}

	/**
	 * 事件上传后删除最后一个事件之前的所有事件
	 * @param eventTime
	 */
	public void removeData(long eventTime) {
		database.delete(DEFAULT_TABLE_NAME, "event_time <= ?", new String[]{eventTime + ""});
	}

	public void close() {
		if (database != null && database.isOpen()) {
			database.close();
		}
	}

	private class AddEventToDBTask implements Runnable {

		private EventBean eventInfo;

		public void updateData(EventBean eventInfo) {
			this.eventInfo = eventInfo;
		}

		public void insertData() {
			ContentValues values = new ContentValues();
			values.put("type", eventInfo.getType());
			values.put("path", eventInfo.getPath());
			values.put("duration", eventInfo.getDuration());
			values.put("event_time", eventInfo.getEventTime());
			long result = database.insert(DEFAULT_TABLE_NAME, null, values);
			LogUtil.i("result======" + result);
		}

		@Override
		public void run() {
			insertData();
		}
	}
}
