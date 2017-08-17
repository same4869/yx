package com.ml.yx.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.ml.yx.utils.DownloadTask;
import com.ml.yx.web.JsonToBeanHandler;

import java.util.List;

public class DownloadTaskDBHelper extends BaseDBHelper<DownloadTask> {

	private YouXinDatabaseHelper mHelper;
	private static DownloadTaskDBHelper instance;

	private DownloadTaskDBHelper() {
		mHelper = YouXinDatabaseHelper.getInstance(getContext());
	}

	private static synchronized void initInstanceSyn() {
		if (instance == null) {
			instance = new DownloadTaskDBHelper();
		}
	}

	public static DownloadTaskDBHelper getInstance() {
		if (instance == null) {
			initInstanceSyn();
		}
		return instance;
	}

	@Override
	public String getTable() {
		return "download_task";
	}

	public void save(String taskId, String url, int totalSize, int curSize) {
		String sql = "insert into " + getTable() + "(" + "TASK_ID,URL,TOTAL_SIZE,CUR_SIZE" + ")values(?,?,?,?)";
		Object[] bindArgs = { taskId, url, totalSize, curSize };
		mHelper.execSQL(sql, bindArgs);
	}

	public void update(String url, int totalSize, int curSize) {

		String sql = "UPDATE " + getTable() + " SET CUR_SIZE = ? WHERE URL = ? AND TOTAL_SIZE = ?";
		mHelper.execSQL(sql, new Object[] { curSize, url, totalSize });
	}

	@Override
	public void save(DownloadTask obj) {
		if (obj != null) {
			byte[] data = null;
			try {
				data = JsonToBeanHandler.getInstance().toByteArray(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String sql = "insert into " + getTable() + "(" + "TASK_ID,URL,DEST_FILE,TOTAL_SIZE,CUR_SIZE,STATUS,TAKS_BEAN" + ")values(?,?,?,?,?,?,?)";
			Object[] bindArgs = { obj.getTaskId(), obj.getUrl(), obj.getDestFile(), obj.getTotalSize(), obj.getCurSize(), obj.getStatus(), data };
			mHelper.execSQL(sql, bindArgs);
		}
	}

	@Override
	public void update(DownloadTask obj) {

		String selection = null;
		String[] selectionArgs = null;
		selection = "URL = ?";
		selectionArgs = new String[] { obj.getUrl() };

		ContentValues values = new ContentValues();
		values.put("CUR_SIZE", obj.getCurSize());
		mHelper.update(getTable(), values, selection, selectionArgs);
	}

	@Override
	public void delete(String url) {
		String sql = "DELETE FROM " + getTable() + " WHERE URL = ?";
		mHelper.execSQL(sql, new Object[] { url });
	}
	
	@Override
	public void deleteAll() {
		String sql = "DELETE FROM " + getTable();
		mHelper.execSQL(sql);
	}

	@Override
	public DownloadTask find(String url) {
		DownloadTask task = null;
		String sql = "SELECT TASK_ID,URL,TOTAL_SIZE,CUR_SIZE FROM " + getTable() + " WHERE URL=?";
		Cursor cursor = null;
		try {
			cursor = mHelper.rawQuery(sql, new String[] { url });
			if (cursor.moveToNext()) {
				task = new DownloadTask(cursor.getString(0), cursor.getString(1), null, cursor.getInt(3), cursor.getInt(2), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null){
				cursor.close();
			}
		}
		return task;
	}
	
	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public List<DownloadTask> getAllData() {

		return null;
	}

}
