package com.android.volley.toolbox;

import java.io.Serializable;
import java.util.Vector;

public class WenbaCookies implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3489717061564860801L;

	public static final String SESSION_COOKIE_NAME = "PHPSESSID";
	public static final String SESSION_COOKIE_NAME2 = "XBSID";

	private long expired = -1;
	private Vector<CookieEntry> cookieEntrys = new Vector<CookieEntry>();
	private Vector<String> httpCookies = new Vector<String>();

	public long getExpired() {
		return expired;
	}

	public void setExpired(long expired) {
		this.expired = expired;
	}
	
	public Vector<CookieEntry> getCookieEntrys() {
		return cookieEntrys;
	}

	public void setCookieEntrys(Vector<CookieEntry> cookieEntrys) {
		this.cookieEntrys.clear();
		httpCookies.clear();
		
		if (cookieEntrys == null) {
			return;
		}
		for (CookieEntry cookie : cookieEntrys) {
			if (cookie != null) {
				addCookies(cookie);
			}
		}
	}

	public void addCookies(CookieEntry cookie) {
		cookieEntrys.add(cookie);
		httpCookies.add(new StringBuffer().append(cookie.getName()).append("=").append(cookie.getValue()).append(";").toString());

		if (getExpired() == -1 || cookie.getExpired() < getExpired()) {
			setExpired(cookie.getExpired());
		}
	}

	public Vector<String> getHttpCookies() {
		return httpCookies;
	}

}
