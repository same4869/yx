/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import android.text.format.DateUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.CustomMultipartEntity;
import com.android.volley.CustomMultipartEntity.ProgressListener;
import com.android.volley.Request;
import com.android.volley.Request.FileParam;
import com.android.volley.Request.Method;
import com.ml.yx.comm.DateUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An HttpStack that performs request over an {@link HttpClient}.
 */
public class HttpClientStack implements HttpStack {
	protected final DefaultHttpClient mClient;

	private final static String HEADER_CONTENT_TYPE = "Content-Type";

	public HttpClientStack(DefaultHttpClient client) {
		mClient = client;
	}

	private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
		for (String key : headers.keySet()) {
			httpRequest.setHeader(key, headers.get(key));
		}
	}

	@SuppressWarnings("unused")
	private static List<NameValuePair> getPostParameterPairs(Map<String, String> postParams) {
		List<NameValuePair> result = new ArrayList<NameValuePair>(postParams.size());
		for (String key : postParams.keySet()) {
			result.add(new BasicNameValuePair(key, postParams.get(key)));
		}
		return result;
	}

	@Override
	public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
			throws IOException, AuthFailureError {
		HttpUriRequest httpRequest = createHttpRequest(request, additionalHeaders);
		addHeaders(httpRequest, additionalHeaders);
		addHeaders(httpRequest, request.getHeaders());
		httpRequest.setHeader("User-Agent", Volley.USER_AGENT);
		onPrepareRequest(httpRequest);
		HttpParams httpParams = httpRequest.getParams();
		int timeoutMs = request.getTimeoutMs();
		// TODO: Reevaluate this connection timeout based on more wide-scale
		// data collection and possibly different for wifi vs. 3G.
		HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
		HttpConnectionParams.setSoTimeout(httpParams, timeoutMs);
		HttpResponse response = null;
		try {
			response = mClient.execute(httpRequest);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Creates the appropriate subclass of HttpUriRequest for passed in request.
	 */
	@SuppressWarnings("deprecation")
	/* protected */static HttpUriRequest createHttpRequest(Request<?> request, Map<String, String> additionalHeaders)
			throws AuthFailureError {
		switch (request.getMethod()) {
		case Method.DEPRECATED_GET_OR_POST: {
			// This is the deprecated way that needs to be handled for backwards
			// compatibility.
			// If the request's post body is null, then the assumption is that
			// the request is
			// GET. Otherwise, it is assumed that the request is a POST.
			byte[] postBody = request.getPostBody();
			if (postBody != null) {
				HttpPost postRequest = new HttpPost(request.getUrl());
				postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
				HttpEntity entity;
				entity = new ByteArrayEntity(postBody);
				postRequest.setEntity(entity);
				return postRequest;
			} else {
				return new HttpGet(request.getUrl());
			}
		}
		case Method.GET:
			return new HttpGet(request.getUrl());
		case Method.DELETE:
			return new HttpDelete(request.getUrl());
		case Method.POST: {
			HttpPost postRequest = new HttpPost(request.getUrl());
			postRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
			setEntityIfNonEmptyBody(postRequest, request);
			return postRequest;
		}
		case Method.POST_FILE: {
			HttpPost postRequest = new HttpPost(request.getUrl());
			// postRequest.addHeader(HEADER_CONTENT_TYPE,
			// request.getBodyContentType());
			setMultipartEntityIfNonEmptyBody(postRequest, request);
			return postRequest;
		}
		case Method.PUT: {
			HttpPut putRequest = new HttpPut(request.getUrl());
			putRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
			setEntityIfNonEmptyBody(putRequest, request);
			return putRequest;
		}
		default:
			throw new IllegalStateException("Unknown request method.");
		}
	}

	private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest, Request<?> request)
			throws AuthFailureError {
		byte[] body = request.getBody();
		if (body != null) {
			HttpEntity entity = new ByteArrayEntity(body);
			httpRequest.setEntity(entity);
		}
	}

	private static void setMultipartEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
			final Request<?> request) throws AuthFailureError {
		request.getBody();// 显示参数log

		CustomMultipartEntity multipartContent = new CustomMultipartEntity(new ProgressListener() {
			@Override
			public void transferred(long num) {
				request.requestProgress(num);
			}
		});
		Map<String, String> params = request.getParams();
		if (params != null && params.size() > 0) {
			Set<Entry<String, String>> sets = params.entrySet();
			try {
				for (Entry<String, String> entry : sets) {
					multipartContent.addPart(entry.getKey(),
							new StringBody(entry.getValue(), Charset.forName("UTF-8")));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		FileParam fileParam = request.getFileParam();
		if (fileParam != null) {
			String[][] headers = fileParam.getHeaders();// add http request
														// header
			if (headers != null && headers.length > 0) {
				for (int i = 0; i < headers.length; i++) {
					String[] header = headers[i];
					if (header != null && header.length == 2) {
						httpRequest.setHeader(header[0], header[1]);
					}
				}
			}
			// add http reqeust multipartEntity
			multipartContent.addPart(fileParam.getParamKey(),
					new FileBody(new File(fileParam.getFilePath()), fileParam.getFileType()));
		}
		request.setRequestContentTotal(multipartContent.getContentLength());
		request.setRequestContentCurrent(0);
		httpRequest.setEntity(multipartContent);
	}

	/**
	 * Called before the request is executed using the underlying HttpClient.
	 * 
	 * <p>
	 * Overwrite in subclasses to augment the request.
	 * </p>
	 */
	protected void onPrepareRequest(HttpUriRequest request) throws IOException {
		// Nothing.
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setCookies(WenbaCookies cookies) throws Exception {
		if (cookies == null) {
			return;
		}
		
		List cookieObjList = cookies.getCookieEntrys();
		
		if (cookieObjList == null || cookieObjList.size() == 0) {
			return;
		}
		
		Object obj = cookieObjList.get(0);
		
		List<CookieEntry> cookieEntrys = null;
		if (obj instanceof CookieEntry) {
			cookieEntrys = cookieObjList;
		}else if (obj instanceof Map){
			//convert to CookieEntry
			cookieEntrys = new ArrayList<CookieEntry>();
			
			for (int i = 0; i < cookieObjList.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) cookieObjList.get(i);
				if (map.size() == 0) {
					continue;
				}
				
				if (!map.containsKey("name")) {
					continue;
				}
				
				CookieEntry entry = new CookieEntry();
				
				entry.setName(String.valueOf(map.get("name")));
				
			    if (map.containsKey("value")) {
			    	entry.setValue(String.valueOf(map.get("value")));
				}
			    
			    if (map.containsKey("domain")) {
					entry.setDomain(String.valueOf(map.get("domain")));
				}
			    
			    if (map.containsKey("path")) {
					entry.setPath(String.valueOf(map.get("path")));
				}
			    
			    if (map.containsKey("expired")) {
			    	Object expire = map.get("expired");
			    	if (expire instanceof Long) {
			    		Long expireVal = (Long) expire;
			    		entry.setExpired(expireVal);
			    	}else if (expire instanceof String) {
			    		String expireVal = (String) expire;
			    		
			    		try {
							entry.setExpired(Long.valueOf(expireVal));
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		}
		
		if (cookieEntrys != null && cookieEntrys.size() > 0) {
			CookieStore store = new BasicCookieStore();
			for (CookieEntry entry : cookieEntrys) {
				if (entry != null) {
					BasicClientCookie2 cookie = new BasicClientCookie2(entry.getName(), entry.getValue());
					cookie.setDomain(entry.getDomain());
					cookie.setPath(entry.getPath());
					store.addCookie(cookie);
				}
			}
			mClient.setCookieStore(store);
		}
	}

	@Override
	public WenbaCookies getCookies() {
		CookieStore store = mClient.getCookieStore();
		List<Cookie> lst = store.getCookies();
		WenbaCookies wenbaCookies = new WenbaCookies();
		for (Cookie cookie : lst) {
			if (WenbaCookies.SESSION_COOKIE_NAME.equals(cookie.getName())
					|| WenbaCookies.SESSION_COOKIE_NAME2.equals(cookie.getName())) {
				long expired = DateUtil.getCurWenbaTime() + DateUtils.WEEK_IN_MILLIS * 2;
				if (cookie.getExpiryDate() != null) {
					if (cookie.getExpiryDate().after(new Date())) {
						expired = cookie.getExpiryDate().getTime();
					}
				}
				CookieEntry entry = new CookieEntry(cookie.getName(), cookie.getValue(), cookie.getDomain(),
						cookie.getPath(), expired);
				wenbaCookies.addCookies(entry);
				// if (wenbaCookies.getExpired() == -1 || expired <
				// wenbaCookies.getExpired()) {
				// wenbaCookies.setExpired(expired);
				// }
			}
		}
		return wenbaCookies;
	}

}
