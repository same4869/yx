/*
 * Copyright (C) 2012 The Android Open Source Project
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

import android.content.Context;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.ml.yx.comm.AppInfoUtils;

import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.File;

public class Volley {

	/** Default on-disk cache directory. */
	private static final String DEFAULT_CACHE_DIR = "volley";

	public static final int CONNECTION_TIME_OUT = 15000;
	public static final int SOCKET_TIME_OUT = 30000;
	public static String USER_AGENT = "com.wenba.bangbang/Bangbang/rev3.2.1";

	/**
	 * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
	 * 
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @param stack
	 *            An {@link HttpStack} to use for the network, or null for default.
	 * @return A started {@link RequestQueue} instance.
	 */

	public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
		File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

		USER_AGENT = "com.wenba.bangbang/Bangbang/rev" + AppInfoUtils.getCurrentVersionCode(context);

		if (stack == null) {
			// 需要使用httpClient的cookie，所以在这里需要获得HttpClient，就不能用HurlStack了
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				stack = new HttpClientStack(getThreadSafeClient());
			}
		}

		Network network = new BasicNetwork(stack);

		RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
		queue.start();

		return queue;
	}

	/**
	 * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
	 * 
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context) {
		return newRequestQueue(context, null);
	}

	public static DefaultHttpClient getThreadSafeClient(SSLSocketFactory socketFactory) {
		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIME_OUT);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIME_OUT);
		HttpProtocolParams.setUseExpectContinue(params, false);
		HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);

		SchemeRegistry schemeRegistry = mgr.getSchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		if (socketFactory == null) {
			socketFactory = SSLSocketFactory.getSocketFactory();
		}
		Scheme sch = new Scheme("https", socketFactory, 443);
		schemeRegistry.register(sch);
		schemeRegistry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));

		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
		return client;
	}

	public static DefaultHttpClient getThreadSafeClient() {
		return getThreadSafeClient(null);
	}

}
