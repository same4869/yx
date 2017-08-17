package com.ml.yx.db;

import android.content.Context;

import com.ml.yx.YouXinApplication;

import java.util.List;

/**
 * 
 * @Author:Lijj
 * @Date:2014-5-16上午10:49:15
 * @Todo:TODO
 */
public abstract class BaseDBHelper<T> {

	Context getContext() {
		return YouXinApplication.getInstance();
	}

	public abstract String getTable();

	public abstract void save(T obj);

	public abstract void update(T obj);

	public abstract void delete(String id);

	public abstract void deleteAll();

	public abstract T find(String id);

	public abstract int getCount();

	public abstract List<T> getAllData();
}
