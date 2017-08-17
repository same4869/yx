package com.ml.yx.comm;

import java.util.HashMap;

public class ParamHelper {
	private static ParamHelper self = new ParamHelper();

	private HashMap<String, HashMap<String, Object>> mMap = new HashMap<String, HashMap<String, Object>>();
	
	private HashMap<String, Object> emptyMap = new HashMap<String, Object>();

	private HashMap<String, Object> acceptParamsInternal(Class cls, boolean removeAfter) {
		if (cls == null) {
			throw new IllegalArgumentException("cls == null");
		}

		HashMap<String, Object> map = null;

		String targetName = cls.getName();

		if (removeAfter) {
			map = mMap.remove(targetName);
		} else {
			map = mMap.get(targetName);
		}
		
		//avoid caller null exception....
		if (map == null) {
			emptyMap.clear();
			map = emptyMap;
		}

		return map;
	}

	private HashMap<String, Object> acquireParamsReceiverInternal(Class cls) {
		if (cls == null) {
			throw new IllegalArgumentException("cls == null");
		}

		HashMap<String, Object> map = null;

		String targetName = cls.getName();

		map = mMap.get(targetName);

		if (map == null) {
			map = new HashMap<String, Object>();
			mMap.put(targetName, map);
		}

		return map;
	}

	private void clearInternal() {
		mMap.clear();
	}

	public static HashMap<String, Object> acquireParamsReceiver(Class cls) {
		return self.acquireParamsReceiverInternal(cls);
	}

	public static HashMap<String, Object> acceptParams(Class cls) {
		return self.acceptParamsInternal(cls, true);
	}

	/**
	 * acceptParams
	 * 
	 * @param cls
	 * @param removeAfter
	 *            remove the target paramsMap when true
	 * @return
	 */
	public static HashMap<String, Object> acceptParams(Class cls, boolean removeAfter) {
		return self.acceptParamsInternal(cls, removeAfter);
	}

	public static void clear() {
		self.clearInternal();
	}
}
