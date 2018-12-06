package com.sxu.trackerlibrary.http;

import android.content.Context;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*******************************************************************************
 * Description: 网络请求
 *
 * Author: Freeman
 *
 * Date: 2018/12/6
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class HttpManager<T extends BaseProtocolBean> {

	private Context context;
	private Handler handler;
	private static HttpManager instance;

	private int DEFAULT_ERROR_CODE = 0xff00;

	private HttpManager(Context applicationContext) {
		this.context = applicationContext;
		this.handler = new Handler(context.getMainLooper());
	}

	public static HttpManager getInstance(Context applicationContext) {
		if (instance == null) {
			synchronized (HttpManager.class) {
				if (instance == null) {
					instance = new HttpManager(applicationContext);
				}
			}
		}

		return instance;
	}

	public void getQuery(String urlStr, Class<T> tClass, OnRequestListener<T> listener) {
		query(urlStr, "GET", null, tClass, listener);
	}

	public void postQuery(String urlStr, Class<T> tClass, OnRequestListener<T> listener) {
		query(urlStr, "POST", null, tClass, listener);
	}

	public void postQuery(String urlStr, Map<String, String> paramsMap,
	                      Class<T> tClass, OnRequestListener<T> listener) {
		byte[] data = null;
		if (paramsMap != null) {
			StringBuilder paramsBuilder = new StringBuilder();
			for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
				if (paramsBuilder.length() == 0) {
					if (urlStr.contains("?")) {
						paramsBuilder.append("&");
					} else {
						paramsBuilder.append("?");
					}
				} else {
					paramsBuilder.append("&");
				}
				paramsBuilder.append(entry.getKey()).append("=").append(entry.getValue());
				try {
					data = paramsBuilder.toString().getBytes("UTF-8");
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		query(urlStr, "POST", data, tClass, listener);
	}

	public void postQuery(String urlStr, byte[] data,
	                      Class<T> tClass, OnRequestListener<T> listener) {
		query(urlStr, "POST", data, tClass, listener);
	}

	private void query(final String urlStr, final String method, final byte[] data,
	                   final Class<T> classT, final OnRequestListener listener) {
		ThreadPoolManager.executeTask(new Runnable() {
			@Override
			public void run() {
				realQuery(urlStr, method, data, classT, listener);
			}
		});
	}
	
	private void realQuery(String urlStr, String method, byte[] data, final Class<T> classT,
	                   final OnRequestListener listener) {
		boolean completed = false;
		HttpURLConnection connection = null;
		final StringBuilder builder = new StringBuilder();
		try {
			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod(method);
			if (connection instanceof HttpsURLConnection) {
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, new TrustManager[]{new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[0];
					}
				}}, new SecureRandom());
				((HttpsURLConnection)connection).setSSLSocketFactory(sslContext.getSocketFactory());
			}

			if (data != null && data.length > 0) {
				connection.getOutputStream().write(data);
			}
			InputStream inputStream = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			completed = true;
			reader.close();
			inputStream.close();
		} catch (final Exception e) {
			e.printStackTrace(System.err);
			if (!completed && listener != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						listener.onError(DEFAULT_ERROR_CODE, e.getMessage());
					}
				});
			}
		} finally {
			if (completed && listener != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						T result = BaseBean.fromJson(builder.toString(), classT);
						if (result.code == 1) {
							listener.onSuccess(result.data);
						} else {
							listener.onError(result.code, result.msg);
						}
					}
				});
			}

			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public interface OnRequestListener<T> {
		void onSuccess(T result);
		void onError(int code, String errMsg);
	}
}
