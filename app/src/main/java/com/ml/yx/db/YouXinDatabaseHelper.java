package com.ml.yx.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 
 * @Author:Lijj
 * @Date:2014-5-16上午10:44:01
 * @Todo:TODO
 */
public class YouXinDatabaseHelper extends AbstractDatabaseHelper {
	private static YouXinDatabaseHelper instance = null;

	private String databaseName = "Youxin.db";
	private String tag = "Youxin_database";
	private int databaseVersion = 1;
	private Context context;

	@Override
	protected String[] createDBTables() {
		String[] object = {

				"CREATE TABLE IF NOT EXISTS MESSAGE("
						+ "ID INTEGER PRIMARY KEY AUTOINCREMENT"
						+ ",MESSAGE_ID VARCHAR(32)"
						+ ",CREATE_TIME TIMESTAMP"
						+ ",MESSAGE_BEAN BLOB" + ")",

				"CREATE TABLE IF NOT EXISTS DOWNLOAD_TASK( "
						+ "ID INTEGER PRIMARY KEY AUTOINCREMENT"
						+ ",UID VARCHAR(32)" + ",TASK_ID VARCHAR(100)"
						+ ",URL VARCHAR(100)" + ",DEST_FILE VARCHAR(100)"
						+ ",TOTAL_SIZE INTEGER" + ",CUR_SIZE INTEGER"
						+ ",CREATE_TIME TIMESTAMP" + ",STATUS VARHCHAR(20)"
						+ ",TARKS_BEAN BLOB" + ")"};
		return object;
	}

	@Override
	protected String[] dropDBTables() {
		String[] object = {
				"DROP TABLE IF EXISTS DOWNLOAD_TASK",
				"DROP TABLE IF EXISTS MESSAGE" };
		return object;
	}

	@Override
	protected String getMyDatabaseName() {
		return databaseName;
	}

	@Override
	protected int getDatabaseVersion() {
		return databaseVersion;
	}

	@Override
	protected String getTag() {
		return tag;
	}

	private static synchronized void initSyn(Context context) {
		instance = new YouXinDatabaseHelper(context);
	}

	public static YouXinDatabaseHelper getInstance(Context context) {
		if (instance == null) {
			initSyn(context);
		}
		return instance;
	}

	private YouXinDatabaseHelper(Context context) {
		this.context = context;
	}

	public void execSQL(String sql, Object[] bindArgs) {
		init(context);
		if (mDb == null) {
			return;
		}
		try {
			mDb.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execSQL(String[] sql, Object[][] bindArgs) {
		if (sql == null || sql.length == 0) {
			return;
		}
		init(context);
		if (mDb == null) {
			return;
		}
		for (int i = 0; i < sql.length; i++) {
			try {
				mDb.execSQL(sql[i], bindArgs[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void execSQL(String sql) {
		init(context);
		if (mDb == null) {
			return;
		}
		try {
			mDb.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		init(context);
		if (mDb == null) {
			return;
		}
		try {
			mDb.update(table, values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		init(context);
		return mDb.rawQuery(sql, selectionArgs);
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		init(context);
		return mDb.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);
	}
}
