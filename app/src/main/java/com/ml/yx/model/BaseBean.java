package com.ml.yx.model;

import com.ml.yx.web.JsonToBeanHandler;

import java.io.Serializable;

public class BaseBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7007529319848861177L;

	protected int status;
	protected String msg = "";

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess() {
		return status == 0 ? true : false;
	}

	@Override
	public String toString() {
		try {
			return JsonToBeanHandler.getInstance().toJsonString(this);
		}  catch (Exception e) {
			e.printStackTrace();
		}

		return super.toString();
	}

}
