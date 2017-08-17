package com.android.volley.toolbox;

import java.io.Serializable;

public class CookieEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3476899773933182455L;
	
	private String name;
	private String value;
	private String domain;
	private String path;
	private long expired;

	public CookieEntry() {
	}

	public CookieEntry(String name, String value, String domain, String path, long expired) {
		this.name = name;
		this.value = value;
		this.domain = domain;
		this.path = path;
		this.expired = expired;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getExpired() {
		return expired;
	}

	public void setExpired(long expired) {
		this.expired = expired;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name).append("=").append(value).append(",expired=").append(expired).append(",path=").append(path);
		return sb.toString();
	}
}
