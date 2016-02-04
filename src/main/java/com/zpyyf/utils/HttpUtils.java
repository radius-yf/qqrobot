package com.zpyyf.utils;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

@SuppressWarnings("unused")
public class HttpUtils {
	private static final Logger log = Logger.getLogger(HttpUtils.class);
	private static final String UA = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.82 Safari/537.36";

	private HttpClient client;


	public HttpResponse get(String url, Header... headers) {
		log.debug("发送GET请求：" + url);
		HttpGet request = new HttpGet(url);

		request.setHeader("Connection", "keep-alive");
		request.setHeader("User-Agent", UA);
		for (Header header : headers) {
			request.setHeader(header);
		}
		try {
			return client.execute(request);
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	public HttpResponse post(String url, String body, Header... headers) {
		log.debug("发送POST请求：" + url);
		HttpPost request = new HttpPost(url);
		request.setEntity(new StringEntity(body, "UTF-8"));

		request.setHeader("Connection", "keep-alive");
		request.setHeader("User-Agent", UA);
		for (Header header : headers) {
			request.setHeader(header);
		}
		try {
			return client.execute(request);
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}


	public static String getString(HttpResponse response) {
		try {
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	public static InputStream getInputStream(HttpResponse response) {
		try {
			return response.getEntity().getContent();
		} catch (IOException e) {
			log.error(e);
		}

		return null;
	}


	public HttpUtils() {
		int timeout = 10000; //10秒超时
		RequestConfig config = RequestConfig.custom()
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.build();
		client = HttpClients.custom()
				.setDefaultRequestConfig(config)
				.build();
	}
}
