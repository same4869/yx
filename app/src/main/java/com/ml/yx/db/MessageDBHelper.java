package com.ml.yx.db;


import android.content.ContentValues;
import android.database.Cursor;
;
import com.ml.yx.model.MessageBean;
import com.ml.yx.web.JsonToBeanHandler;

import java.util.ArrayList;
import java.util.List;

public class MessageDBHelper extends BaseDBHelper<MessageBean> {

    private YouXinDatabaseHelper mHelper;
    private static MessageDBHelper instance;

    private MessageDBHelper() {
        mHelper = YouXinDatabaseHelper.getInstance(getContext());
    }

    private static synchronized void initInstanceSyn() {
        if (instance == null) {
            instance = new MessageDBHelper();
        }
    }

    public static MessageDBHelper getInstance() {
        if (instance == null) {
            initInstanceSyn();
        }
        return instance;
    }

    @Override
    public String getTable() {
        return "message";
    }

    @Override
    public void save(MessageBean obj) {
        if (obj == null) {
            return;
        }
        byte[] data = null;
        try {
            data = JsonToBeanHandler.getInstance().toByteArray(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sql = "insert into " + getTable() + "("
                + "message_id,create_time,message_bean"
                + ")values(?,?,?)";
        Object[] bindArgs = {obj.getMsg_id(), obj.getCreateTime(), data};
        mHelper.execSQL(sql, bindArgs);
    }

    @Override
    public void update(MessageBean obj) {
        if (obj == null) {
            return;
        }
        byte[] data = null;
        try {
            data = JsonToBeanHandler.getInstance().toByteArray(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String selection = null;
        String[] selectionArgs = null;
        selection = "message_id = ?";
        selectionArgs = new String[]{obj.getMsg_id()};
        ContentValues values = new ContentValues();
        values.put("message_bean", data);
        mHelper.update(getTable(), values, selection, selectionArgs);
    }

    @Override
    public void delete(String id) {
        if (id == null) {
            return;
        }
        String sql = "delete from " + getTable() + " where message_id = \""
                + id + "\"";
        mHelper.execSQL(sql);
    }

    public void deleteAll() {
        String sql = "delete from " + getTable();
        mHelper.execSQL(sql);
    }

    @Override
    public MessageBean find(String msg_id) {
        if (msg_id == null) {
            return null;
        }
        MessageBean task = null;
        String selection = "message_id = ?";
        String[] selectionArgs = new String[]{msg_id};
        String[] columns = {"message_bean"};
        Cursor cursor = null;
        try {
            cursor = mHelper.query(getTable(), columns, selection,
                    selectionArgs, null, null, null);
            if (cursor.moveToNext()) {
                byte[] data = cursor.getBlob(0);
                task = JsonToBeanHandler.getInstance().fromByteArray(
                        data, MessageBean.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return task;
    }

    @Override
    public List<MessageBean> getAllData() {
        return getData(0, 0);
    }

    @Override
    public int getCount() {
        int count = 0;
        String str = "select count(*)  from " + getTable();
        Cursor cursor = null;
        try {
            cursor = mHelper.rawQuery(str, null);
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    public List<MessageBean> getData(int offset, int count) {
        String selection = null;
        String[] selectionArgs = null;
        String[] columns = {"message_bean"};
        List<MessageBean> tasks = new ArrayList<>();
        Cursor cursor = null;
        try {
            String orderbyArgs;
            if (offset == 0 && count == 0) {
                orderbyArgs = "";
            } else {
                orderbyArgs = " limit " + offset + "," + count;
            }
            cursor = mHelper.query(getTable(), columns, selection,
                    selectionArgs, null, null, "create_time desc " + orderbyArgs);
            while (cursor.moveToNext()) {
                byte[] data = cursor.getBlob(0);
                try {
                    MessageBean task = JsonToBeanHandler.getInstance()
                            .fromByteArray(data, MessageBean.class);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tasks;
    }

    public List<MessageBean> getDataBefore(long createTime, int count) {
        String selection = "create_time < ?";
        String[] selectionArgs = new String[]{String.valueOf(createTime)};
        String[] columns = {"message_bean"};
        List<MessageBean> tasks = new ArrayList<>();
        Cursor cursor = null;
        try {
            String orderbyArgs = " limit " + count;
            cursor = mHelper.query(getTable(), columns, selection,
                    selectionArgs, null, null, "create_time desc " + orderbyArgs);
            while (cursor.moveToNext()) {
                byte[] data = cursor.getBlob(0);
                try {
                    MessageBean task = JsonToBeanHandler.getInstance()
                            .fromByteArray(data, MessageBean.class);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tasks;
    }

}
